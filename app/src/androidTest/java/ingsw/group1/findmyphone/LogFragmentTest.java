package ingsw.group1.findmyphone;

import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import ingsw.group1.findmyphone.activity.NavHolderActivity;
import ingsw.group1.findmyphone.contacts.SMSContact;
import ingsw.group1.findmyphone.event.LogEventType;
import ingsw.group1.findmyphone.event.SMSLogDatabase;
import ingsw.group1.findmyphone.event.SMSLogEvent;
import ingsw.group1.findmyphone.fragment.LogFragment;
import ingsw.group1.findmyphone.location.GeoPosition;
import ingsw.group1.msglibrary.RandomSMSPeerGenerator;

import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static org.junit.Assert.assertNotNull;

public class LogFragmentTest {

    private static final RandomSMSPeerGenerator GENERATOR = new RandomSMSPeerGenerator();
    private static final String DB_NAME = "test-db";

    private LogFragment fragment;

    private List<SMSLogEvent> exampleEvents = Arrays.asList(
            new SMSLogEvent(
                    LogEventType.RING_REQUEST_SENT,
                    new SMSContact(
                            GENERATOR.generateValidPeer(),
                            "User 1"
                    ),
                    System.currentTimeMillis(),
                    String.valueOf(1000)
            ),
            new SMSLogEvent(
                    LogEventType.RING_REQUEST_RECEIVED,
                    new SMSContact(
                            GENERATOR.generateValidPeer(),
                            "User 2"
                    ),
                    System.currentTimeMillis(),
                    String.valueOf(1000)
            ),
            new SMSLogEvent(
                    LogEventType.LOCATION_REQUEST_RECEIVED,
                    new SMSContact(
                            GENERATOR.generateValidPeer(),
                            "User 3"
                    ),
                    System.currentTimeMillis(),
                    new GeoPosition(
                            100, 100
                    ).toString()
            ),
            new SMSLogEvent(
                    LogEventType.LOCATION_REQUEST_SENT,
                    new SMSContact(
                            GENERATOR.generateValidPeer(),
                            "User 4"
                    ),
                    System.currentTimeMillis(),
                    new GeoPosition(
                            100, 100
                    ).toString()
            ),
            //This event should be translated into an item with red text.
            new SMSLogEvent(
                    LogEventType.RING_REQUEST_RECEIVED,
                    new SMSContact(
                            GENERATOR.generateValidPeer(),
                            "User 5"
                    ),
                    System.currentTimeMillis(),
                    null
            ),
            //This event should be not shown.
            new SMSLogEvent()
    );

    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    /**
     * Rule to create an Activity
     */
    @Rule
    public ActivityTestRule<NavHolderActivity> rule =
            new ActivityTestRule<>(NavHolderActivity.class);

    /**
     * Rule to prepare some fake data in the database
     */
    @Before
    public void prepareDataAndAddFragment() {
        SMSLogDatabase.getInstance(rule.getActivity(), DB_NAME).clear();
        SMSLogDatabase.getInstance(rule.getActivity(), DB_NAME).addEvents(exampleEvents);
        fragment = new LogFragment(rule.getActivity(), DB_NAME);
        rule.getActivity().replaceFragment(fragment);
    }

    /**
     * Wait for 30 seconds to allow viewing results.
     */
    @Test
    public void assertFragmentExists() {
        Espresso.onView(isRoot()).perform(waitFor(30000));
        assertNotNull(fragment);
    }

    /**
     * Clear the database from the fake data.
     */
    @After
    public void resetData() {
        SMSLogDatabase.getInstance(rule.getActivity(), DB_NAME).clear();
    }
}
