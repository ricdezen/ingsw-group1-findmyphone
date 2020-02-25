package ingsw.group1.findmyphone.contacts;

import android.widget.SearchView;

/**
 * Class implements {@link SearchView.OnQueryTextListener}.
 * This is a listener to activate the search on the contact list through a {@link SMSContactRecyclerAdapter}.
 *
 * @author Giorgia Bortoletti
 */
public class SMSContactSearchListener implements SearchView.OnQueryTextListener {

    private SMSContactRecyclerAdapter recyclerAdapter;

    public SMSContactSearchListener(SMSContactRecyclerAdapter recyclerAdapter) {
        this.recyclerAdapter = recyclerAdapter;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        recyclerAdapter.getFilter().filter(newText);
        return false;
    }
}