# PostealApp

## -Introduction
This is Postealapp, an Instagram-like application where you can upload your photos, follow your friends and see their latest posts, giving them feedback with likes and comments.

## -Functionalities
   ### ·Important disclaimer: 
Some of the main functionalities of this application have been based on the application proposed in [this course](https://www.udemy.com/course/instagram_jetpack/learn/lecture/29932274#questions/20478320). Part of the authentication system and the logic of the feed and search screens have been retained. The rest of the logic has been reimplemented (everything related to the single post, comments, followers, etc.). Otherwise, as much of the UI, all tests, small changes in the architecture (business layer) and additional features such as dark/light mode, localization or deletion of a post, are completely of my authorship.

   ### ·How it works: 
   First of all, in the application you will be able to create an account or log in if you already have one. Once inside, you will appear on the screen of your feed, in which the first time you see it, the last posts published in the whole application will appear, but when you follow other users, only their posts will appear.
   When selecting a post we can indicate that we like it by double clicking on it or leave a comment.
   Another option will be the post search screen. If you type in a term that belongs to the description of a post or the author's username, it will appear on the screen.
   Finally, on your posts page, apart from being able to access your posts to see your friends' feedback (likes and comments), you will be able to edit your profile. There you can change your personal data, log out and switch between light and dark modes. Finally, if you click on the profile picture with a “+” you will access the creation of a post. Select a photo from your gallery and if you want to add a description and location.

   ### ·Technical details:
   + -> The MVVM architecture has been followed.
   + -> The backend has been implemented with Firebase, specifically the storage services, the Firestore and authentication.
   + -> Dependency injection has been performed with Hilt.
   + -> Navigation has been managed with NavController.
   + -> The UI has been implemented with Jetpack Compose and the inclusion of specific themes and fonts.
   + -> Apart from Firebase, the Google Maps API has also been included to add a location to publications.
   + -> A couple of unit and navigation UI tests have been included (due to lack of time it has not been possible to test more components).
   + -> It works without internet as it saves the content locally apart from the firestore server, it just won't update the latest posts and interactions until the device reconnects.
   
