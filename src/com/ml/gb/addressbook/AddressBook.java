package com.ml.gb.addressbook;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.ml.gb.addressbook.R;

// ListActivity is nothing but a sub class of Activity that supports handling for list actions
// it's calling setContentView(com.android.internal.R.layout.list_content_simple) inside its class
public class AddressBook extends ListActivity {
	private ListView contactListView;
	// Cusor is used for db
	private CursorAdapter contactAdapter;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contactListView = getListView();
		contactListView.setOnItemClickListener(viewContactListener);
		String[] from = new String[] { "name" };
		int[] to = new int[] { R.id.contactTextView };
		// cursor represents db result, use an adaptor to apply it to list
		contactAdapter = new SimpleCursorAdapter(AddressBook.this,
				R.layout.contatct_list_item, null, from, to);
		setListAdapter(contactAdapter);
	}

	// each time app is brought to foreground, create an AsyncTask and execute
	// it queries the db and inserts all entries into list in a separate thread
	@Override
	protected void onResume() {
		super.onResume();
		new GetContatsTask().execute((Object[]) null);
	}

	@Override
	protected void onStop() {
		Cursor cursor = contactAdapter.getCursor();
		if (cursor != null) {
			// TODO close should work as well since we're nullify it?
			// cursor.deactivate();
			cursor.close();
		}
		contactAdapter.changeCursor(null);
		super.onStop();
	}

	// touch an contact name, create a new Intent to launch the ViewContact
	// Activity
	OnItemClickListener viewContactListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent viewContact = new Intent(AddressBook.this, ViewContact.class);
			// param
			viewContact.putExtra(AddressBookConstants.ROW_ID, id);
			startActivity(viewContact);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.addressbook_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent addNewContact = new Intent(AddressBook.this,
				AddEditContact.class);
		startActivity(addNewContact);
		return super.onOptionsItemSelected(item);
	}

	// AsyncTask allows to execute short(several seconds) tasks and publish the
	// Result to UI thread
	private class GetContatsTask extends AsyncTask<Object, Object, Cursor> {
		DatabaseConnector dbc = new DatabaseConnector(AddressBook.this);

		// a Cursor represents contacts
		@Override
		protected Cursor doInBackground(Object... params) {
			dbc.open();
			return dbc.getAllContacts();
		}

		// call back to handle result which is registered in class defination
		@Override
		protected void onPostExecute(Cursor result) {
			// assign the returned cursor to contactAdapter
			contactAdapter.changeCursor(result);
			dbc.close();
		}

	}
}
