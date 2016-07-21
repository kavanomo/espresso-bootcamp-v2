# Espresso-bootcamp

This bootcamp serves as an introduction point into the Espresso framework and other testing tools for android.

Bootcamp coverts majority of the common scenarios one might encounter while developing tests for android applications.

Please refrain from modifying the weather app source code even if it stinks. The project is only for demo purposes and is a 
modified version of a "Project Sunshine" tutorial by Google.

Fork this repo and request evaluation from QA members experienced in developing Espresso tests.

Software:

    * Android studio
    * Android SDK
    * JDK 1.7 or 1.8


#Espresso basics

## Task #0: Setup and dependencies

##### Useful  material and links: 

[Espresso setup instructions] (https://google.github.io/android-testing-support-library/docs/espresso/setup/index.html)


##### Objective:
1. Set up the espresso tests on the sample app provided in the repo
2. Add a test that launches the app and waits (use SystemClock.sleep() to wait)

#####Description:

Using the links above set up an androidTest directory and add your first test.
If you addded a contrib library you might have noticed that the app crashed right away. Fix that. You will need that library.

## Task #1: Exploring Espresso API

##### Useful  material and links: 

[Espresso basics tutorial]  (https://google.github.io/android-testing-support-library/docs/espresso/basics/index.html) 

[Espresso api cheat sheet] (https://google.github.io/android-testing-support-library/docs/espresso/cheatsheet/index.html)

##### Objective:
Add a test that opens an overflow menu, selects Settings and verifies that "Forecast Preference" is displayed for a particular view

#####Description:

To click on items and assert the view you will need to know the identifiers of the views. They can be obtained using tools like [uiatomatorviewer](http://developer.android.com/tools/testing-support-library/index.html) or [hierarchy viewer] (http://developer.android.com/tools/help/hierarchy-viewer.html).

Main difference between two tools is that hierarchy viewer provides you with ENTIRE hierarchy, including items that are not visible (invisbile, gone), while uiatomatorviewer 
shows only visible views. This discrepancy is important to remember as there is a fundamental difference between a view that doesn't exist and view that is not visible.

## Task #2: More on Espresso API.

##### Objective:
Add a test that Changes the location in settings and verifies that the new location is saved in the settings

#####Description:
Same as #2 this task is just for exploring capabilities of out-of-the box features of espresso library.
Please don't test anything other than UI. Only validate that settings accepted your change. Don't test network responses and data integrity.

## Task #3: Last one on espresso API

##### Objective:
Add a pull to refresh test. Verify that snackbar is displayed.

#####Description:
Don't test anything other than snackbar.


Notice that this test fails frequently. And this is because your tests don't know yet how to synchronize with the background thread to take into the account network request and response processing times.
This will be fixed in task #11 with the implementation of the IdlingResource.

#Espresso Intermediate. Dynamic content and data validation.

## Task #4 Understanding onData

##### Useful  material and links: 

[Advanced espresso guide] (https://google.github.io/android-testing-support-library/docs/espresso/advanced/index.html)

##### Objective:
Add a test that changes the  temperature units using onData()

#####Description:

Whenever you encounter a list view in the hierarchy, this is an indication that onView() doesn't work here any more. onView will only work for items that are currently visible on the screen.
However, if an item is off the screen onView will produce view not found exception and fail the test.
onData doesn't take a view matcher, but instea it's looking for a data matcher, which matches the data in the adapter behind the ListView.
Making assertions with onData will automatically scroll to the view if it's not displayed.

Before starting the test investigate what kind of data the adapter holds in the unit selection dialog. You can write a failing test that will show you data type in the exception log.

##  Task #5: CursorAdapters and onData part 2:

##### Disclaimer:

The app does not and will not use ListViews. But there are a lot of apps out there that are still using ListViews. Understanding the concept of onData will be your introduction to the world of  adapters.

This task is important from the learning point of view, but if you are frustrated that you can't see the results feel free to skip this one and ask for a solution from those who have done it.

##### Useful  material and links: 

[Cursor matchers] (http://developer.android.com/reference/android/support/test/espresso/matcher/CursorMatchers.html)


##### Objective:
Pretend that main activity contains a list view that is backed by an adapter that is holding Cursors for each forecast item.

Write a pseudo test that uses onData and clicks on a card with today's forecast.

#####Description:

Explore the source code and find where cursors are used.
Explore the contrib library of esresso framework to find how to match data in the cursor adapters.
Annotate the test with @Ignore so it doesn't run when you run the whole test suite.

## Task #6: RecyclerViews

##### Useful  material and links: 
-
###### Objective:
Write a test that clicks on last forecast item of the main page.
**Don't use position to scroll to the item!**
**Scroll only once!**

##Task #7: Custom ViewMatcher

##### Useful  material and links: 
-

###### Objective:
Write a test that checks number of items in the recycler view of the main activity.

#####Description:
This functionality is not available in the Espresso framework and will require an implementation of a custom ViewMatcher.

Prettify the output of the matcher's description.
Notice that this test is flaky as well. Implementation of the custom idling resource (task #11) will make this test more stable.

##Task #8: External intents, Independent tests and first encounter with dependency injections.

##### Useful  material and links: 
[Espresso intents] (https://google.github.io/android-testing-support-library/docs/espresso/intents/)

###### Objective:
Write a test that verifies that correct location data is being sent to a map when Map is selected from the overflow menu.

#####Description:
To make this test independent make sure to clear the shared preference storage before the test starts. Your test should be checking for a default location value.

Before implemention your own solution take a look at the production application. Find how the data is stored and which class is used to read and write this data. Notice the injection pattern that is used throughtout the app. You won't be able to use it up until you implement your own module. For now instantiate the helper class every time you need to use it.

##Task #9: Launching into different activities
##### Useful  material and links: 

[Activity test rule] (http://developer.android.com/reference/android/support/test/rule/ActivityTestRule.html)

#####Objective:
Write a test that launches straight into the DetailsActivity and verify all that all ui elements display correct information.

#####Description:
You need to setup the test in a way that the data is preloaded into the databse. You will need to do it before activity launches.
Your activity after the setup must look identical to a normal activity that was created as a result of a selection on the main activity.


## Refactoring time.

Sit back and read [Espresso Style Guide](https://github.com/pivotal-vladimir/espresso-bootcamp/wiki/Espresso-Style-guide). Once you fully grasped the idea, refactor your tests so they look like first class tests written by Espresso master.

#Espresso Advanced. Dependency Injections. Network synchronization. Fake data. 		

## Task #10: Dagger
##### Useful  material and links: 

[Dagger] (http://square.github.io/dagger/)
[Example project] (http://blog.sqisland.com/2015/04/dagger-2-espresso-2-mockito.html)

#####Objective:

1. Create a testmodule with custom dependencies for espresso tests
2. Implement a custom test runner and use it to initiate dependencies
3. Use injection of a helper class from task #8

#####Description:
Learn how the production app initiates dependencies.
Don't add any dependencies for now. Just add your module on top of the existing one.

## Task #11: IdlingResources
##### Useful  material and links: 

[IdlingResource] (http://developer.android.com/reference/android/support/test/espresso/IdlingResource.html)
[Setting executor] (https://github.com/square/retrofit/issues/1081)

#####Objective:
1. Implement an idling resource mechanism to wait for network requests to finish before proceeding with the test.
2. Add a test that changes a location and verify that snackbar with new location appears on the home activity.

#####Description:
This is the right time to use a test module to your advantage.
You tests should pass 100% of the runs.

Using AsyncTask for this task is considered to be a hacky, but an easy solution. To become an Espresso Jedi you need to implement an IdlingResource interface.

## Task #12:MockWebServer
#####Useful  material and links: 

[MockWebServer] (https://github.com/square/okhttp/tree/master/mockwebserver)
[Test Rules] (https://google.github.io/android-testing-support-library/docs/rules/index.html)
[GSON] (https://github.com/google/gson)

#####Objective:
1. Add a mockwebserver and replace all network requests with mocked ones.
2. Add a test that clicks on the card and verifies elements of the WeatherDetails activity.
3. Add a test that tests server unavailable error

#####Description:
Responses need to be built out using GSON library. Don't include raw json in your tests.
