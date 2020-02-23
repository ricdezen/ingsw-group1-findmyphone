package ingsw.group1.findmyphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ingsw.group1.findmyphone.R;
import ingsw.group1.findmyphone.contacts.ContactSwipeCallback;
import ingsw.group1.findmyphone.contacts.SMSContact;
import ingsw.group1.findmyphone.contacts.SMSContactManager;
import ingsw.group1.msglibrary.SMSPeer;

/**
 * Activity for add a new contact
 *
 * @author Giorgia Bortoletti
 */
public class ModifyContactActivity extends AppCompatActivity {

    private static String contactAddress;

    private EditText contactNameField;
    private TextView contactPhoneField;

    private SMSContactManager contactManager;
    private String contactName;

    /**
     * Method invoked when activity is created
     *
     * @param savedInstanceStatus
     */
    @Override
    public void onCreate(Bundle savedInstanceStatus) {
        super.onCreate(savedInstanceStatus);
        setContentView(R.layout.activity_modify_contact);

        contactNameField = findViewById(R.id.modify_contact_name);
        contactPhoneField = findViewById(R.id.modify_contact_address);

        contactManager = new SMSContactManager(getApplicationContext());

        SMSContact contact = contactManager.getContactForPeer(new SMSPeer(contactAddress));
        contactName = contact.getName();

        contactNameField.setText(contactName);
        contactPhoneField.setText(contactAddress);

    }

    /**
     * Called when the user touches the button for modifying an existing contact
     *
     * @param view {@link View} where this method is invoked
     */
    public void modifyContact(View view) {
        String newContactName = contactNameField.getText().toString();

        if (newContactName.equals(contactName)) {
            Toast.makeText(getApplicationContext(), ActivityConstantsUtils.EQUALS_CONTACT_NAME, Toast.LENGTH_LONG).show();
        } else {
            contactManager.modifyContactName(new SMSPeer(contactAddress), newContactName);
            Toast.makeText(getApplicationContext(), ActivityConstantsUtils.CONTACT_UPDATED, Toast.LENGTH_LONG).show();
        }
        startActivity(new Intent(ModifyContactActivity.this, ContactListActivity.class));
    }

    /**
     * Setter for the contact address that cannot be changed.
     * It is invoked by {@link ContactSwipeCallback}.
     */
    public static void setContactAddress(String contactAddress) {
        ModifyContactActivity.contactAddress = contactAddress;
    }

}