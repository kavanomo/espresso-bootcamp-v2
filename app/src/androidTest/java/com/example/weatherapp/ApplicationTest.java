package com.example.weatherapp;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.example.weatherapp.activities.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

@RunWith(AndroidJUnit4.class)
@LargeTest

public class ApplicationTest extends ApplicationTestCase<Application> {

    //Function to open up the Settings
    public void openSettings(){
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_settings)).perform(click());
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    public ApplicationTest() {
        super(Application.class);
    }

    /*@Test
    public void sleepAfterLaunch(){
        SystemClock.sleep(1000);
    }

    public void sleepAfterLaunch2(){
        SystemClock.sleep(1000);
    }*/

    @Test //Task 0
    public void sleepAfterLaunch(){
        SystemClock.sleep(1000);
    }

    @Test //Task 1
    public void checkForecastPreference(){
        openSettings();
        onView(withText(R.string.preference_title)).check(matches(isDisplayed()));
    }

    @Test //Task 2
    public void changeLocation(){
        openSettings();
        onView(withText(R.string.preference_zip_title)).perform(click());
        onView(withId(android.R.id.edit))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText("Waterloo,Ontario"));
        onView(withId(android.R.id.button1)).perform(ViewActions.click());
        onView(allOf(hasSibling(withText(R.string.preference_zip_title)), withId(android.R.id.summary))).check(matches(withText("Waterloo,Ontario")));
    }

    @Test //Task3
    public void refreshAndSnackbar(){
        ViewActions.swipeDown();
        onView(withId(R.id.snackbar_text)).check(matches(isDisplayed()));
    }

    @Test //Task4
    public void changeTemperatureUnits(){
        openSettings();
        onView(withText("Temperature Units")).perform(ViewActions.click());
        onData(allOf(is(instanceOf(String.class)), is(getTargetContext().getString(R.string.units_imperial)))).perform(click());
        //onView(withText(R.string.units_imperial)).perform(ViewActions.click());  <-- A way to do it without the long complicated thing above, since it doesn't use ListViews
    }

    /*@Test //Task5
    public void onDataCursor(){
        onData(withRowLong(WeatherContract.WeatherEntry.COLUMN_DATE, DateTime.now().getMillis())).perform(click());
    }*/

    @Test // Task6
    public void clickLastItem(){
        onView(withId(R.id.recyclerview_forecast)).perform(RecyclerViewActions.actionOnItemAtPosition(6, click()));
    }

    @Test //Task7
    public void checkNumberItems(){
        onView(withId(R.id.recyclerview_forecast)).check(matches(numItemsOnRecycler(7)));
    }

    public static Matcher<View> numItemsOnRecycler(final int expectedNum){  //Custom ViewMatcher to help
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class){

            @Override //Done to write the expected num to the console
            public void describeTo(final Description description){
                description.appendText("Number of items: " + expectedNum);
            }

            @Override //Gets the number of expected items
            public boolean matchesSafely(RecyclerView item) {
                return item.getAdapter().getItemCount() == expectedNum;
            }
        };
    }

    @Test //Task8
    public void checkMapIntent(){
        Uri locationUri = Uri.parse("geo:0,0").buildUpon()  //Setting up the data to be tested with (as per MainActivityFragment -> openLocationInMap)
                .appendQueryParameter("q", "Waterloo,Ontario")
                .build();
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, new Intent());

        Intents.init();
        intending(hasAction(Intent.ACTION_VIEW)).respondWith(result);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Map")).perform(click());
        intended(allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(locationUri)
        ));

        Intents.release();

    }
}