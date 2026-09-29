# SpendWise Student

SpendWise Student is a simple Android mobile application developed in Kotlin for university students to record daily expenses and control a monthly budget.

## Core Features

- Add a new expense
- Edit an existing expense
- Delete an expense using long press
- View all expenses
- Search by title, category, or date
- Expense categories
- Date picker
- Monthly total calculation
- Set a monthly budget
- Display remaining budget and budget status
- Local SQLite database persistence
- Input validation

## Technology Stack

- Android Studio
- Kotlin
- XML layouts
- SQLite / SQLiteOpenHelper
- RecyclerView
- SharedPreferences
- View Binding

## How to Run

1. Download or clone this repository.
2. Open the project folder in Android Studio.
3. Allow Gradle Sync to complete.
4. If Android Studio asks for an SDK, install Android SDK 35 or change `compileSdk` and `targetSdk` in `app/build.gradle` to an installed SDK version.
5. Start an Android emulator or connect an Android phone with USB debugging enabled.
6. Click **Run**.

## How to Use

1. Open SpendWise Student.
2. Tap **Set Budget** and enter a monthly budget.
3. Tap **Add Expense**.
4. Enter title, amount, category, and date.
5. Tap **Save Expense**.
6. Tap an expense to edit it.
7. Long press an expense to delete it.
8. Use the search field to find expenses.

## Suggested Demo Flow

1. Show home screen.
2. Set monthly budget.
3. Add two or three expenses.
4. Show monthly total and remaining budget.
5. Search for an expense.
6. Edit an expense.
7. Delete an expense with long press.
8. Close and reopen the app to show data persistence.

## Repository Notes

For a stronger Git history, commit work in logical steps, for example:

- `Initial Android project setup`
- `Add expense data model and SQLite database`
- `Implement add and edit expense screens`
- `Add RecyclerView expense list`
- `Add search and delete functions`
- `Add monthly budget tracking`
- `Improve UI and input validation`
- `Update README and final testing`
