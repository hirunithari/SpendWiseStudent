package com.example.spendwisestudent

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ExpenseDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "spendwise.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_EXPENSES = "expenses"
        private const val COL_ID = "id"
        private const val COL_TITLE = "title"
        private const val COL_AMOUNT = "amount"
        private const val COL_CATEGORY = "category"
        private const val COL_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase) {

        val query = """
            CREATE TABLE $TABLE_EXPENSES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_AMOUNT REAL NOT NULL,
                $COL_CATEGORY TEXT NOT NULL,
                $COL_DATE TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(query)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }


    // --------------------------------
    // INSERT EXPENSE
    // --------------------------------
    fun insertExpense(expense: Expense): Long {

        val values = ContentValues().apply {
            put(COL_TITLE, expense.title)
            put(COL_AMOUNT, expense.amount)
            put(COL_CATEGORY, expense.category)
            put(COL_DATE, expense.date)
        }

        return writableDatabase.insert(
            TABLE_EXPENSES,
            null,
            values
        )
    }


    // --------------------------------
    // UPDATE EXPENSE
    // --------------------------------
    fun updateExpense(expense: Expense): Int {

        val values = ContentValues().apply {
            put(COL_TITLE, expense.title)
            put(COL_AMOUNT, expense.amount)
            put(COL_CATEGORY, expense.category)
            put(COL_DATE, expense.date)
        }

        return writableDatabase.update(
            TABLE_EXPENSES,
            values,
            "$COL_ID = ?",
            arrayOf(expense.id.toString())
        )
    }


    // --------------------------------
    // DELETE EXPENSE
    // --------------------------------
    fun deleteExpense(id: Int): Int {

        return writableDatabase.delete(
            TABLE_EXPENSES,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }


    // --------------------------------
    // GET SINGLE EXPENSE
    // --------------------------------
    fun getExpenseById(id: Int): Expense? {

        val cursor = readableDatabase.query(
            TABLE_EXPENSES,
            null,
            "$COL_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {

            if (it.moveToFirst()) {

                return Expense(
                    id = it.getInt(
                        it.getColumnIndexOrThrow(COL_ID)
                    ),
                    title = it.getString(
                        it.getColumnIndexOrThrow(COL_TITLE)
                    ),
                    amount = it.getDouble(
                        it.getColumnIndexOrThrow(COL_AMOUNT)
                    ),
                    category = it.getString(
                        it.getColumnIndexOrThrow(COL_CATEGORY)
                    ),
                    date = it.getString(
                        it.getColumnIndexOrThrow(COL_DATE)
                    )
                )
            }
        }

        return null
    }


    // --------------------------------
    // GET ALL EXPENSES
    // --------------------------------
    fun getAllExpenses(): List<Expense> {

        val expenses = mutableListOf<Expense>()

        val cursor = readableDatabase.rawQuery(
            """
            SELECT *
            FROM $TABLE_EXPENSES
            ORDER BY $COL_DATE DESC, $COL_ID DESC
            """.trimIndent(),
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                expenses.add(
                    Expense(
                        id = it.getInt(
                            it.getColumnIndexOrThrow(COL_ID)
                        ),
                        title = it.getString(
                            it.getColumnIndexOrThrow(COL_TITLE)
                        ),
                        amount = it.getDouble(
                            it.getColumnIndexOrThrow(COL_AMOUNT)
                        ),
                        category = it.getString(
                            it.getColumnIndexOrThrow(COL_CATEGORY)
                        ),
                        date = it.getString(
                            it.getColumnIndexOrThrow(COL_DATE)
                        )
                    )
                )
            }
        }

        return expenses
    }


    // --------------------------------
    // SEARCH EXPENSES
    // --------------------------------
    fun searchExpenses(keyword: String): List<Expense> {

        if (keyword.isBlank()) {
            return getAllExpenses()
        }

        val expenses = mutableListOf<Expense>()

        val like = "%$keyword%"

        val cursor = readableDatabase.query(
            TABLE_EXPENSES,
            null,
            "$COL_TITLE LIKE ? OR $COL_CATEGORY LIKE ? OR $COL_DATE LIKE ?",
            arrayOf(like, like, like),
            null,
            null,
            "$COL_DATE DESC, $COL_ID DESC"
        )

        cursor.use {

            while (it.moveToNext()) {

                expenses.add(
                    Expense(
                        id = it.getInt(
                            it.getColumnIndexOrThrow(COL_ID)
                        ),
                        title = it.getString(
                            it.getColumnIndexOrThrow(COL_TITLE)
                        ),
                        amount = it.getDouble(
                            it.getColumnIndexOrThrow(COL_AMOUNT)
                        ),
                        category = it.getString(
                            it.getColumnIndexOrThrow(COL_CATEGORY)
                        ),
                        date = it.getString(
                            it.getColumnIndexOrThrow(COL_DATE)
                        )
                    )
                )
            }
        }

        return expenses
    }


    // --------------------------------
    // MONTHLY TOTAL
    // --------------------------------
    fun getMonthlyTotal(yearMonth: String): Double {

        val cursor = readableDatabase.rawQuery(
            """
            SELECT COALESCE(SUM($COL_AMOUNT), 0)
            FROM $TABLE_EXPENSES
            WHERE $COL_DATE LIKE ?
            """.trimIndent(),
            arrayOf("$yearMonth%")
        )

        cursor.use {

            if (it.moveToFirst()) {
                return it.getDouble(0)
            }
        }

        return 0.0
    }


    // --------------------------------
    // NUMBER OF EXPENSES
    // --------------------------------
    fun getExpenseCount(): Int {

        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_EXPENSES",
            null
        )

        cursor.use {

            if (it.moveToFirst()) {
                return it.getInt(0)
            }
        }

        return 0
    }


    // --------------------------------
    // TOTAL OF ALL EXPENSES
    // --------------------------------
    fun getAllTimeTotal(): Double {

        val cursor = readableDatabase.rawQuery(
            """
            SELECT COALESCE(SUM($COL_AMOUNT), 0)
            FROM $TABLE_EXPENSES
            """.trimIndent(),
            null
        )

        cursor.use {

            if (it.moveToFirst()) {
                return it.getDouble(0)
            }
        }

        return 0.0
    }


    // --------------------------------
    // HIGHEST EXPENSE
    // --------------------------------
    fun getHighestExpense(): Expense? {

        val cursor = readableDatabase.rawQuery(
            """
            SELECT *
            FROM $TABLE_EXPENSES
            ORDER BY $COL_AMOUNT DESC
            LIMIT 1
            """.trimIndent(),
            null
        )

        cursor.use {

            if (it.moveToFirst()) {

                return Expense(
                    id = it.getInt(
                        it.getColumnIndexOrThrow(COL_ID)
                    ),
                    title = it.getString(
                        it.getColumnIndexOrThrow(COL_TITLE)
                    ),
                    amount = it.getDouble(
                        it.getColumnIndexOrThrow(COL_AMOUNT)
                    ),
                    category = it.getString(
                        it.getColumnIndexOrThrow(COL_CATEGORY)
                    ),
                    date = it.getString(
                        it.getColumnIndexOrThrow(COL_DATE)
                    )
                )
            }
        }

        return null
    }


    // --------------------------------
    // TOP SPENDING CATEGORY
    // --------------------------------
    fun getTopCategory(): Pair<String, Double>? {

        val cursor = readableDatabase.rawQuery(
            """
            SELECT $COL_CATEGORY, SUM($COL_AMOUNT) AS total
            FROM $TABLE_EXPENSES
            GROUP BY $COL_CATEGORY
            ORDER BY total DESC
            LIMIT 1
            """.trimIndent(),
            null
        )

        cursor.use {

            if (it.moveToFirst()) {

                val category = it.getString(0)
                val total = it.getDouble(1)

                return Pair(category, total)
            }
        }

        return null
    }


    // --------------------------------
    // FILTER BY CATEGORY
    // --------------------------------
    fun getExpensesByCategory(
        category: String
    ): List<Expense> {

        if (category == "All Categories") {
            return getAllExpenses()
        }

        val expenses = mutableListOf<Expense>()

        val cursor = readableDatabase.query(
            TABLE_EXPENSES,
            null,
            "$COL_CATEGORY = ?",
            arrayOf(category),
            null,
            null,
            "$COL_DATE DESC, $COL_ID DESC"
        )

        cursor.use {

            while (it.moveToNext()) {

                expenses.add(
                    Expense(
                        id = it.getInt(
                            it.getColumnIndexOrThrow(COL_ID)
                        ),
                        title = it.getString(
                            it.getColumnIndexOrThrow(COL_TITLE)
                        ),
                        amount = it.getDouble(
                            it.getColumnIndexOrThrow(COL_AMOUNT)
                        ),
                        category = it.getString(
                            it.getColumnIndexOrThrow(COL_CATEGORY)
                        ),
                        date = it.getString(
                            it.getColumnIndexOrThrow(COL_DATE)
                        )
                    )
                )
            }
        }

        return expenses
    }


    // --------------------------------
    // CLEAR ALL EXPENSES
    // --------------------------------
    fun clearAllExpenses(): Int {

        return writableDatabase.delete(
            TABLE_EXPENSES,
            null,
            null
        )
    }
}