package ingsw.group1.findmyphone.contacts;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import static org.junit.Assert.*;

public class ContactDatabaseTest {

    private static final String CONTACT_VALID_ADDRESS = "+393478989890"; //for contact and peer
    private static final String CONTACT_VALID_NAME = "NewContact";

    private ContactDatabase contactDatabase;
    private Contact contact; //database's entity

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        contactDatabase = Room.inMemoryDatabaseBuilder(context, ContactDatabase.class).build();

        contact = new Contact(CONTACT_VALID_ADDRESS, CONTACT_VALID_NAME);
    }

    @After
    public void closeDatabase() throws IOException {
        contactDatabase.close();
    }

    //---------------------------- TESTS ----------------------------

    @Test
    public void getTableName(){
        assertEquals(ContactDao.DEFAULT_TABLE_NAME, contactDatabase.access().getTableName());
    }

    @Test
    public void insertContact(){
        contactDatabase.access().insert(contact);

        assertTrue(contactDatabase.access().getAll().contains(contact));
    }

    @Test
    public void deleteContact(){
        contactDatabase.access().insert(contact);
        contactDatabase.access().delete(contact);

        assertFalse(contactDatabase.access().getAll().contains(contact));
    }


}