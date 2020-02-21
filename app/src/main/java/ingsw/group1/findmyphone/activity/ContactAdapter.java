package ingsw.group1.findmyphone.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ingsw.group1.findmyphone.R;
import ingsw.group1.findmyphone.contacts.SMSContact;
import ingsw.group1.findmyphone.contacts.SMSContactManager;

/**
 * Class adapter
 * from a list of {@link SMSContact}
 * and its graphic representation in a {@link RecyclerView} in {@link ContactListActivity}.
 * This class implements {@link Filterable} using a {@link ContactFilter}
 * that takes care of filtering the contacts to show.
 *
 * @author Giorgia Bortoletti
 */
class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {

    private List<SMSContact> contacts; //contacts filtered
    private SMSContactManager contactManager;
    private Filter filter; //filter used in the searchView to filter contacts by name and address

    //---------------------------- CONSTRUCTOR ----------------------------

    /**
     * Constructor
     *
     * @param contacts       to show in the {@link RecyclerView}
     * @param contactManager {@link SMSContactManager} used to manage contacts after a user's request
     */
    public ContactAdapter(final List<SMSContact> contacts, SMSContactManager contactManager) {
        this.contacts = contacts;
        this.contactManager = contactManager;

        this.filter = new ContactFilter(this, contacts);
    }

    //---------------------------- OPERATIONS ON VIEW HOLDER ----------------------------

    /**
     * Called when RecyclerView needs a new {@link ContactViewHolder} of the given type
     * to represent an item.
     *
     * @param parent
     * @param viewType
     * @return new {@link ContactViewHolder}
     */
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_contact_item,
                parent, false);
        return new ContactViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link ContactViewHolder#itemView} to reflect the item at
     * the given position.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.contactName.setText(contacts.get(position).getName());
        holder.contactAddress.setText(contacts.get(position).getAddress());
    }

    //---------------------------- SEARCH FILTER ----------------------------

    /**
     * Return {@link ContactFilter} used to filter contacts name.
     *
     * @return {@link ContactFilter} used to filter contacts name
     */
    @Override
    public Filter getFilter() {
        return filter;
    }

    //---------------------------- OPERATIONS ON CONTACTS ----------------------------

    /**
     * Return numbers on contacts in the contacts list.
     *
     * @return numbers on contacts in the contacts list
     */
    @Override
    public int getItemCount() {
        return contacts.size();
    }

    /**
     * Return {@link SMSContact} to the given position.
     *
     * @return {@link SMSContact} to the given position
     */
    public SMSContact getItem(int position) {
        return contacts.get(position);
    }

    /**
     * Add a {@link SMSContact} to the given position of contacts list.
     *
     * @param position     where insert contact in the list of contacts
     * @param contactToAdd {@link SMSContact} to add
     */
    public void addItem(int position, SMSContact contactToAdd) {
        contacts.add(position, contactToAdd);
        contactManager.addContact(contactToAdd);
        notifyItemInserted(position);
    }

    /**
     * Remove a {@link SMSContact} to the given position in the contacts list.
     *
     * @param position of {@link SMSContact} to delete from contacts list
     */
    public void deleteItem(int position) {
        SMSContact contactToRemove = contacts.get(position);
        contactManager.removeContact(contactToRemove);
        contacts.remove(position);
        notifyItemRemoved(position);
    }


    //---------------------------- ContactViewHolder ----------------------------

    /**
     * Class to represent a contact viewed in a row of RecycleView.
     *
     * @author Giorgia Bortoletti
     */
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName;
        public TextView contactAddress;

        /**
         * Constructor
         *
         * @param itemView
         */
        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            contactAddress = itemView.findViewById(R.id.contact_address);
        }

    }
}
