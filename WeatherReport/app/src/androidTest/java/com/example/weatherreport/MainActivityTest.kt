package com.example.weatherreport

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherreport.ui.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    val idlingResource: IdlingResource? = null

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun registerIdlingResource() {
        val idlingResource: IdlingResource = ElapsedTimeIdlingResource(1000L)
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @Test
    fun checkAnyOftheViewDisplayed() {
        onView(withId(R.id.temparature)).check(matches(isDisplayed()))
    }

    @Test
    fun checkViewIsRendered() {
        onView(withText("Feels like")).check(matches(isDisplayed()))
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}