# CloudFolio

### About
-----------
Libraries are often clumsy with the multitudinous amount of books that they have. Therefore, to simplify the entire process of checking out, holding, and managing
dues, I built an application for iOS and Android called CloudFolio. I received 1st place in the state FBLA conference. 

### Instructions
-----------------
###### iOS Instructions
---------------

Welcome to CloudFolio - Your Library on the cloud. This document will detail the instructions to run both versions of the app, iOS and Android, and will walkthrough the major features of the app. 

Instructions for running the CloudFolio Application (iOS)
1. Make sure that you are running macOS High Sierra on a MacBook Pro. 
2. Install Xcode from the App Store. It is very big application and can take time.
3. Install the zip file from the link given or from the files uploaded in the web form and unzip it. Store the files in your desktop or any other folder that you will remember. 
4. Open Xcode once it has finished downloading, click on 'Open another project...' in the bottom right hand corner. 
5. Navigate to the location of the unzipped folder. Then, choose the file with the '.xcworkspace' extension. The file will be called 'Cloud Folio.xcworkspace'
6. After Xcode finishes loading all the necessary components of the app, choose an iPhone 8 or an iPhone 7 on the small box specifying the emulator on the top of the application beside the play/run and stop buttons. 
7. Then, hit run, and the app should work!

###### App Walkthrough:
------------

Login Page: The Login Page is the first page that you will land on upon opening the app. If you already have an account, you can just type in your credentials and sign in, or else, there is a button to let you create an account.

Create Account Page: This page lets you create an account in the CloudFolio Application. Fill in all of the required credentials and information for you to gain access to the app. Upon clicking on the button, you will enter the app's main page. 

App Home Page: The app's Home Page has the random book of the day section and also several shortcuts to your folders (Checkouts, Holds, Bookmarks). 

App Book Page: The app's Book Page has all of the books listed in a detailed list view. The following is the key to the list view: 

_______________________________________
[Book Name] 			
[Genre]				[Lexile]
[Author]			[Stock]
_______________________________________

Click on the that particular list item to view the book details of the app. 

Book Details Page: The Book Details Page is the page where the the list view expands for you to view the in depth description, and more properties of the book such as number of pages. Furthermore, there are other actions for you to perform: Checkout, Hold, Bookmark, and Share. 

Map Page: The Map Page is the page where the user is shown a static map of an ideal library with the markings of where the non-fiction and the fiction books would be. And, there are additional specifications of workspaces and elevators etc.

Profile Page: The profile page presents the user with several folders such as Checkouts, Holds, Bookmarks, and Dues. Each of the folders show the user's corresponding books (checked out books, held books etc.). There are additional functions such as sending feedback, editing the profile, and the about page of the app (aka. App versions). 

Nuances of the app: (iOS)
1. Note that the reload functionality of the app is only upon opening the page again. For example, if the user clicks on 'Go to Checkouts' from the Home Page, they will be presented with their checkout folder page. Then, if they go to the Books page to checkout a book, they would not see the checked out book back in the Home Page with the Checkout Folder open, as the page was not reloaded (because of firebase's asynchronous function calls). Therefore the user will have hit the back button and click on checkouts again. To keep the user experience smooth, it is recommended to hit back every time so that one does not get irked by this small extra step necessary. 

2. The books inside the checkouts, holds, and the bookmarks can be swiped from the right to the left to get access to further actions, such as Return, Pickup, and Delete respectively. 

3. There is a sneaky little feature for obtaining admin access to the app. Sign out from the usual account of the app, and sign back in, but with these credentials (Note that even a minor discrepancy in the credentials can deny the admin access). 
    Email/Username: 'admin'
    Password: 'keycodepassword'
    If the password was misspelled, a dialog message will popup, saying that the email was badly formatted as the first input field is supposed to be technically an email address field. However, if the credentials are typed in correctly, the user of the app will get special access to the app (that bypasses the firebase security check for emails). In this mode, one can add books, and view users of the app, their checkouts, holds, dues, and their feedback.
 
4. The due functionality cannot be tested within a short period of time. One would have to wait for almost 25 days to see if that functionality works as expected or not as the due date would have to be exceeded. 

5. There is a share feature for the books that have the potential to share the following: "Read the book [X] from CloudFolio" to any social media app. So, the user of the app must ideally have installed multiple social media apps on the iPhone emulator to test the social media feature, but the reality is that one cannot do that as the App Store is not available on the iPhone emulator. So, all that the user can do is copy that snippet and paste it in the browser to see that the feature (with the appropriate text works). In the Android version however, this functionality works perfectly as the emulator has the capability of running the Google Play Store. Refer to the instructions to install the correct emulator for android.

6. The pickup feature with the hold queue can be tested by holding a book from Account A today, and the same book from Account B tomorrow. This means that the user who had held the book the earliest gets the first priority. Note however that this feature does not incorporate time functionality, therefore only working with date differences. 


###### Differences between Android and iOS
--------------
1. Share functionality works better on the Android version of the app.
2. Pickup functionality works better on the iOS version of the app. 
3. Auto-sign in is enabled on the Android version of the app (this feature checks for the user last signed in to the app and signs them back in automatically)


###### Android Instructions
------------

A. Instructions for Installing Android Studio on macOS 10.13.3 (High Sierra) *macOS PREFERRED WHEN DEMOING*
1. Go to https://developer.android.com/studio/index.html using your preferred web browser.
2. Click the green button that says Download Android Studio.
3. Read the Terms and Conditions and check the box that says I have read and agree with the above terms and conditions.
4. Click the button that turned blue and says Download Android Studio for Mac.
5. Once the download is completed (which can be determined by observing the download progress bar in your browser), open a Finder window and navigate to the Downloads folder.
6. Double click on the file that is named similarly to the format android-studio-ide.###.#######-mac.dmg.
7. After macOS finishes verifying and opening the file, a pop-up window appears. Drag the Android Studio icon on the left side of the arrow to the Applications folder on the right side.
8. Once the copying process is complete (which can be determined by observing the progress bar that appears), open the previous Finder window and navigate to the Applications folder.
9. Double click on the file named Android Studio with a green icon.
10. Allow macOS to finish verifying and opening the file. If a prompt that states “Android Studio” is an application downloaded from the Internet. Are you sure you want to open it?, then click the button that says Open.
11. A window labeled Complete Installation appears. Select Do not import settings and click the Ok button.
12. A new window labeled Android Studio Setup Wizard appears. Read the text on-screen and click the Next button.
13. Select Standard and click the Next button.
14. Select either Default or Dracula (whichever is preferred) and click the Next button.
15. Read the text on-screen and click the Finish button.
16. Allow Android Studio to finish downloading and installing all the necessary components (observe the progress bar). If any prompts appear during this process asking for an administrator password, enter the correct credentials and continue.
17. Click the Finish button once it is available. Android Studio has been successfully installed!

B. Instructions for Running the CloudFolio Application by Using a Fresh Installation of Android Studio for macOS 10.13.3 (High Sierra)
1. Open the Android Studio application by opening a Finder window, navigating to the Applications folder, and double clicking on the Android Studio file.
2. Download CloudFolio from the link provided in the FBLA submission form. Use the previous Finder window to navigate to the Downloads folder and double click on the CloudFolio.zip file.
3. In Android Studio, select Import project (Gradle, Eclipse, ADT, etc.) and navigate to the location of the unzipped CloudFolio folder. It should be in the same folder as CloudFolio.zip.
4. Select the unzipped folder named CloudFolio and click the Open button. If an error message appears, click the Ok button.
5. Once Android Studio opens a new window, click the Close button on the Tip of the Day window.
6. An error message should be present on-screen. Click on the blue, italicized hyperlink that says Install missing platform(s) and sync project. This is not an error due to the CloudFolio application itself - it is due to the fact that additional components must be installed prior to running the CloudFolio application.
7. Read the License Agreement that appears in the pop-up window, select the Accept option, and click the Next button.
8. Allow Android Studio to finish downloading and installing the necessary additional components (observe the progress bar). Once it is available, click the Finish button.
9. The pop-up window closes and another error message is present. Click on the blue, italicized hyperlink that says Install Build Tools 26.0.2 and sync project. Again, this is not an error with the CloudFolio application itself - instead, some components that are not included in the default installation of Android Studio must be installed in order to run the CloudFolio application.
10. A new pop-up window appears. Allow Android Studio to finish downloading and installing the necessary additional components (observe the progress bar). Once it is available, click the Finish button.
11. The pop-up window closes. Observe the small progress bar at the bottom of the window of the Android Studio application and wait until the Android Studio icon in the dock of macOS changes to the same icon but with a green check mark at the top right corner.
12. At the top right of the Android Studio application window, click the green “play” button that looks like a triangle pointing to the right.
13. A pop-up window appears. Click the button that says Create New Virtual Device.
14. Select the Pixel 2 option and click the Next button.
15. Click the blue, hyperlinked download button next to the words API 27.
16. A new pop-up window appears. Allow Android Studio to finish downloading and installing the necessary additional components (observe the progress bar). Once it is available, click the Finish button.
17. The previous pop-up window closes. Click the Next button.
18. Click the Finish button without changing any settings on this page.
19. The previous pop-up window closes. Click the OK button.
20. A phone emulator launches and appears on-screen. Wait for it to boot up and launch the CloudFolio application.

C. Instructions for Android Studio for Windows
1. Go to https://developer.android.com/studio/index.html. Click Download Android Studio.
2. Follow the setup wizard for your specific operating system. 
3. Download CloudFolio from the link provided in the FBLA submission form and extract the .zip file.
4. In Android Studio, click on File and click open. Navigate to that specific folder location. You should be able to see all the classes and the code.
6. An error message should be present on-screen. Click on the blue, italicized hyperlink that says Install missing platform(s) and sync project. This is not an error due to the CloudFolio application itself - it is
5. Click on the Run tab, and click on Run MainActivity. Make sure that, near the green play button, the dropdown menu says MainActivity. 
6. A window will popup asking for the emulator to run on. If you prefer a physical device, refer to the instructions for a physical device. If you want to run on a virtual emulator, click on the Create New Virtual Device button. 
7. The pop-up window closes and another error message is present. Click on the blue, italicized hyperlink that says Install Build Tools 26.0.2 and sync project. Again, this is not an error with the CloudFolio application itself - instead, some components that are not included in the default installation of Android Studio must be installed in order to run the CloudFolio application.
8. If another comes, follow the blue italicized line to make sure. 
7. Select the Phone category. 
9. Choose Pixel XL or Pixel. Select API level 25. Download if necessary. It will take more than a minute. 
10. Change the AVD name if desired. Disable device frame if desired. Then click Finish. 
11. The Gradle and the Compiler will start running the program. You will see the login page of CloudFolio. 

D. Instructions for Demoing CloudFolio
1. Register a new account by clicking on “No Account? Create One.”
2. After you finish creating your account, you should go to your homepage where you will see the featured book and buttons to easily access other menus.
3. To check out, hold, or bookmark books, visit the books tab and click on the books to perform the necessary operation. 
4. To view all your checkouts, holds, pickups, dues, and bookmarks, visit your profile page.
5. Inside the Checkouts page, click on the book to view a dialog asking for if you want to return or not.
6. Inside the Hold page, visit the Holds Tab to see the current holds. Click on the 3 dots to see the hold queue, the order of the people who held books. Go to the Pickup Tab to see which books are ready for pickup. Click on the 3 dots to pickup the book. 
7. Inside the Bookmarks page, view your bookmarks and delete them if desired. 
8. Inside the Dues page, view the total amount of fine that is to be paid. (To test this, wait for the book's checkout date to exceed the current date. Then open the app on that day to see the fine owed which will increase by the day).
9. Click on Edit profile if you want to change your school, name, or grade level. 
10. Provide feedback for the app by clicking "Send Feedback" in the profile page.
11. To view the map of Chattahoochee High School's Library, visit the Map Page. More library maps coming up in the next version. 

C. Instructions for Installing CloudFolio on a Physical Android Device
(For Android Oreo:)
1. Go to Settings. Click on About Phone. Click on Build Number several times until you become a developer. 
2. Navigate to Developer Options under System Settings (go back to system). Turn Developer Options on, and scroll down to Debugging. Turn on USB debugging. Click 'ok' on the prompt. 
3. Connect the phone to the laptop and click on run. You will see your phone listed on the devices available. 
(For Android Nougat:)
1. Go to Settings and scroll all the way down to About Phone. 
2. Tap on the Build Number as many times until you become a developer. 
3. Navigate to Developer Options and turn it on. Turn on USB Debugging. Click 'ok' on the prompt. 
4. Connect the phone to the laptop and click on run. You will see your phone listed on the devices available.

*NOTE: If your phone is either Android Marshmallow or below, you cannot run it on a physical device. You will have to follow the instructions for setting up the emulator (A. 6 - 9).



