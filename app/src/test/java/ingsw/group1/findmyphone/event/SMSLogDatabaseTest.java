package ingsw.group1.findmyphone.event;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ingsw.group1.findmyphone.contacts.SMSContact;
import ingsw.group1.msglibrary.RandomSMSPeerGenerator;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

/**
 * Tests for {@link SMSLogDatabase}. Each tests assumes to be starting with a newly
 * created, or previously emptied instance of the database.
 *
 * @author Riccardo De Zen.
 */
@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class SMSLogDatabaseTest {

    private static final String DEFAULT_DB_NAME = "test-db";
    private static final String ALTERNATIVE_DB_NAME = "another-db";

    private static final SMSContact EXAMPLE_CONTACT = new SMSContact(
            new RandomSMSPeerGenerator().generateValidPeer(),
            "Username"
    );

    private static final SMSLogEvent SIMPLE_EVENT = new SMSLogEvent(
            LogEventType.UNKNOWN,
            EXAMPLE_CONTACT,
            100L,
            "Hello"
    );
    private static final SMSLogEvent ANOTHER_EVENT = new SMSLogEvent(
            LogEventType.RING_REQUEST_RECEIVED,
            EXAMPLE_CONTACT,
            10000L,
            String.valueOf(1000L)
    );
    private static final List<SMSLogEvent> SOME_EVENTS = Arrays.asList(
            SIMPLE_EVENT, ANOTHER_EVENT
    );

    private SMSLogDatabase database;

    /**
     * Loading an instance of the database.
     */
    @Before
    public void loadDatabase() {
        database = SMSLogDatabase.getInstance(
                ApplicationProvider.getApplicationContext(),
                DEFAULT_DB_NAME
        );
    }

    /**
     * Just a facade call to allow actual clearing method to be static, to be used in any other
     * tests that need it.
     */
    @After
    public void clearInstances() {
        clearActiveInstances();
    }

    /**
     * This method is necessary, due to an issue with ObjectPools where static instances of the
     * database cannot be reset between tests automatically. Method is static to allow using it
     * from other test classes if necessary.
     */
    public static void clearActiveInstances() {
        try {
            //Getting the field named "activeInstances" (the map with all active databases)
            Field instance = SMSLogDatabase.class.getDeclaredField("activeInstances");
            //Making it accessible
            instance.setAccessible(true);
            //Casting it to a Map
            Map<?, ?> privateField = (Map<?, ?>) instance.get(null);
            //Making it empty
            if (privateField != null)
                privateField.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test confirming same name results in same instance.
     * Testing for the hashcode is implicit because the instances are the exact same.
     */
    @Test
    public void sameNamesAreSame() {
        assertSame(
                SMSLogDatabase.getInstance(
                        ApplicationProvider.getApplicationContext(),
                        DEFAULT_DB_NAME
                ),
                SMSLogDatabase.getInstance(
                        ApplicationProvider.getApplicationContext(),
                        DEFAULT_DB_NAME
                )
        );
    }

    /**
     * Test confirming different name results in different Objects.
     */
    @Test
    public void differentNamesAreNotSame() {
        SMSLogDatabase anInstance = SMSLogDatabase.getInstance(
                ApplicationProvider.getApplicationContext(),
                DEFAULT_DB_NAME
        );
        SMSLogDatabase anotherInstance = SMSLogDatabase.getInstance(
                ApplicationProvider.getApplicationContext(),
                ALTERNATIVE_DB_NAME
        );
        assertNotSame(anInstance, anotherInstance);
    }

    /**
     * Test confirming instances with different names are also not equal.
     */
    @Test
    public void differentNamesAreNotEqual() {
        SMSLogDatabase anInstance = SMSLogDatabase.getInstance(
                ApplicationProvider.getApplicationContext(),
                DEFAULT_DB_NAME
        );
        SMSLogDatabase anotherInstance = SMSLogDatabase.getInstance(
                ApplicationProvider.getApplicationContext(),
                ALTERNATIVE_DB_NAME
        );
        assertNotEquals(anInstance, anotherInstance);
    }

    /**
     * Testing whether the database actually starts empty.
     */
    @Test
    public void startsEmpty() {
        final int expectedStartCount = 0;
        assertEquals(expectedStartCount, database.count());
    }

    /**
     * Testing single insertion.
     */
    @Test
    public void canAddOne() {
        final int expectedCount = 1;
        assertTrue(
                //Add method returns true and only one item was added
                database.addEvent(SIMPLE_EVENT) && expectedCount == database.count()
        );
    }

    /**
     * Testing the insertion is actually correct, through
     * {@link SMSLogDatabase#contains(SMSLogEvent)}.
     */
    @Test
    public void canAddOneAndItsCorrect() {
        assertTrue(
                //Add method returns true and the item is actually present
                database.addEvent(SIMPLE_EVENT) && database.contains(SIMPLE_EVENT)
        );
    }

    /**
     * Testing multiple insertions.
     */
    @Test
    public void canAddMultiple() {
        final int expectedCount = SOME_EVENTS.size();
        assertTrue(
                //Multiple additions all return true and the final count is the expected value
                !database.addEvents(SOME_EVENTS).containsValue(Boolean.FALSE) &&
                        expectedCount == database.count()
        );
    }

    /**
     * Testing multiple insertions are actually correct, through
     * {@link SMSLogDatabase#getAllEvents()}.
     */
    @Test
    public void canAddMultipleAndTheyAreCorrect() {
        if (database.addEvents(SOME_EVENTS).containsValue(Boolean.FALSE)) fail();
        List<SMSLogEvent> containedEvents = database.getAllEvents();
        for (SMSLogEvent eachEvent : SOME_EVENTS) {
            if (!containedEvents.contains(eachEvent))
                fail();
        }
    }

    /**
     * Testing one removal.
     */
    @Test
    public void canRemoveOne() {
        final int expectedCount = 0;
        database.addEvent(SIMPLE_EVENT);
        database.removeEvent(SIMPLE_EVENT);
        assertEquals(expectedCount, database.count());
    }

    /**
     * Testing multiple removals.
     */
    @Test
    public void canRemoveMultiple() {
        final int expectedCount = 0;
        database.addEvents(SOME_EVENTS);
        database.removeEvents(SOME_EVENTS);
        assertEquals(expectedCount, database.count());
    }

}