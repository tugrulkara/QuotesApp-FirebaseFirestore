## QuotesApp and Admin Panel

This repository contains the source code of a simple quote application with quotes from famous people. This repository contains code samples for Firestore.

*[Download](https://github.com/tugrulkara/QuotesApp-FirebaseFirestore/releases/tag/2.4) the latest APK*

<table>
  <tr>
    <td>QuotesApp</td>
     <td>Quotes Maker Screen</td>
     <td>Admin Panel</td>
  </tr>
  <tr>
    <td><img src="https://user-images.githubusercontent.com/74429693/201745230-32d364b4-c6cb-46e2-b399-870d9b71b9e9.gif" width=270 height=480></td>
    <td><img src="https://user-images.githubusercontent.com/74429693/201746478-3767ed54-2989-486e-a23a-584828b3cf91.gif" width=270 height=480></td>
    <td><img src="https://user-images.githubusercontent.com/74429693/202216263-12aebd56-9c60-4a53-a370-a1a4fb010edb.gif" width=270 height=480></td>
  </tr>
 </table>
 
 ## Libraries Used

* [Firebase](https://firebase.google.com/)
  * Firebase Firestore
  * Firebase Auth
  * Firebase Storage
  * Firebase Messaging
* [SqLite](https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase)
* Alarm Manager
* Notification
* **Third party**
  * [PhotoEditor](https://github.com/burhanrashid52/PhotoEditor/tree/master/app/src/main)
  * [CircularImageView](https://github.com/lopspower/CircularImageView)
  * [Snacky](https://github.com/matecode/Snacky)
  * [DotLoader](https://github.com/bhargavms/DotLoader)

## Features

* CRUD author-category-quote
* Firebase Messaging-Notification
* add quotes to favorites
* share quotes
* quote of the day
* create picture quote
* search for quotes (searchview)
* daily notification

## Installation

### 1. Create a Firebase project

### 2. [Enable Email/Password sign-in](https://firebase.google.com/docs/auth/android/password-auth?hl=en&authuser=0#before_you_begin)

![ezgif com-gif-maker (3)](https://user-images.githubusercontent.com/74429693/201983838-b9b82169-dc51-475c-97a4-3097396900e1.gif)

### 3. [Create database in FirestoreDatabase](https://firebase.google.com/docs/firestore/quickstart?hl=en&authuser=0#create)

**Cloud Firestore Rules**

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow write: if request.auth != null ;
      allow read: if request.auth == null || request.auth != null;
    }
  }
}
```

**Cloud Firestore Indexes**

![Ekran Alıntısı](https://user-images.githubusercontent.com/74429693/202220449-78c2af75-e879-4e2c-8399-193837d6f343.PNG)

### 4. [Create a default Cloud Storage bucket](https://firebase.google.com/docs/storage/android/start?hl=en&authuser=0#create-default-bucket)

**Storage Rules**

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 5. [Register your app with Firebase](https://firebase.google.com/docs/android/setup?authuser=0&hl=en#register-app)

![Ekran Alıntısı2](https://user-images.githubusercontent.com/74429693/202220452-780a87a8-2379-48af-be55-893b744ea8fb.PNG)

**Move your config file into the module (app-level) root directory of your app. (google-services.json)**

**Note: add it to both the application and the admin panel (google-services.json)**

![Ekran Alıntısı](https://user-images.githubusercontent.com/74429693/201753476-43b3744e-467a-4c74-bb0a-dfdeb2244721.PNG)

## Built With
This app is developed using Java.
