## QuotesApp and Admin Panel-FirebaseFirestore

*This repository contains the source code of a simple quote application with quotes from famous people.
This repository contains code samples for Firestore.*

*[Download](https://github.com/tugrulkara/QuotesApp-FirebaseFirestore/releases/tag/2.4) the latest APK* 

![ezgif com-gif-maker](https://user-images.githubusercontent.com/74429693/201745230-32d364b4-c6cb-46e2-b399-870d9b71b9e9.gif)

## Quotes Maker

![ezgif com-gif-maker (1)](https://user-images.githubusercontent.com/74429693/201746478-3767ed54-2989-486e-a23a-584828b3cf91.gif)

## Admin Panel App

![ezgif com-gif-maker (1)](https://user-images.githubusercontent.com/74429693/202216263-12aebd56-9c60-4a53-a370-a1a4fb010edb.gif)

## Features

*CRUD author-category-quote*

*Firebase Messaging-Notification*

*add quotes to favorites*

*share quotes*

*quote of the day*

**create picture quote**

*author and categories*

*search for quotes (searchview)*

*daily notification*

## Installation

*1. Create a Firebase project*

*2. [Enable Email/Password sign-in](https://firebase.google.com/docs/auth/android/password-auth?hl=en&authuser=0#before_you_begin)*

![ezgif com-gif-maker (3)](https://user-images.githubusercontent.com/74429693/201983838-b9b82169-dc51-475c-97a4-3097396900e1.gif)

*3. [Create database in FirestoreDatabase](https://firebase.google.com/docs/firestore/quickstart?hl=en&authuser=0#create)*

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


*4. [Register your app with Firebase](https://firebase.google.com/docs/android/setup?authuser=0&hl=en#register-app)*

**Move your config file into the module (app-level) root directory of your app. (google-services.json)**

**Note: add it to both the application and the admin panel (google-services.json)**

![Ekran Alıntısı](https://user-images.githubusercontent.com/74429693/201753476-43b3744e-467a-4c74-bb0a-dfdeb2244721.PNG)

## Built With
This app is developed using Java.

I hope it helps :)

## License

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
