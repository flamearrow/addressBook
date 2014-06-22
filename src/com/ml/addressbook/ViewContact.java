package com.ml.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.ml.gb.addressbook.R;

public class ViewContact extends Activity {
	private long rowID;
	private TextView nameTextView;
	private TextView phoneTextView;
	private TextView emailTextView;
	private TextView streetTextView;
	private TextView cityTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_contact);

		nameTextView = (TextView) findViewById(R.id.nameTextView);
		phoneTextView = (TextView) findViewById(R.id.phoneTextView);
		emailTextView = (TextView) findViewById(R.id.emailTextView);
		streetTextView = (TextView) findViewById(R.id.streetTextView);
		cityTextView = (TextView) findViewById(R.id.cityTextView);

		// take the param passed from AddressBook
		Bundle extras = getIntent().getExtras();
		rowID = extras.getLong(AddressBookConstants.ROW_ID);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new LoadContactTask().execute(rowID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_contact_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// when create edit from an view, create a new intent for AddEditContact
		// preset the values to what's set here
		case R.id.editItem:
			Intent addEditContact = new Intent(this, AddEditContact.class);
			addEditContact.putExtra(AddressBookConstants.ROW_ID, rowID);
			addEditContact.putExtra(AddressBookConstants.NAME,
					nameTextView.getText());
			addEditContact.putExtra(AddressBookConstants.PHONE,
					phoneTextView.getText());
			addEditContact.putExtra(AddressBookConstants.EMAIL,
					emailTextView.getText());
			addEditContact.putExtra(AddressBookConstants.STREET,
					streetTextView.getText());
			addEditContact.putExtra(AddressBookConstants.CITY,
					cityTextView.getText());
			startActivity(addEditContact);
			return true;
		case R.id.deleteItem:
			deleteContact();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void deleteContact() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ViewContact.this);
		builder.setTitle(R.string.confirmTitle);
		builder.setMessage(R.string.confirmMessage);

		builder.setPositiveButton(R.string.button_delete,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final DatabaseConnector dbc = new DatabaseConnector(
								ViewContact.this);
						AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {

							@Override
							protected Object doInBackground(Long... params) {
								dbc.deleteContact(params[0]);
								return null;
							}

							@Override
							protected void onPostExecute(Object result) {
								finish();
							}
						};
						deleteTask.execute(new Long[] { rowID });
					}
				});

		builder.setNegativeButton(R.string.button_cancel, null);

	}

	private class LoadContactTask extends AsyncTask<Long, Object, Cursor> {
		DatabaseConnector dbc = new DatabaseConnector(ViewContact.this);

		@Override
		protected Cursor doInBackground(Long... params) {
			dbc.open();
			return dbc.getOneContact(params[0]);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);
			result.moveToFirst();
			nameTextView.setText(result.getString(result
					.getColumnIndex(AddressBookConstants.NAME)));
			phoneTextView.setText(result.getString(result
					.getColumnIndex(AddressBookConstants.PHONE)));
			emailTextView.setText(result.getString(result
					.getColumnIndex(AddressBookConstants.EMAIL)));
			streetTextView.setText(result.getString(result
					.getColumnIndex(AddressBookConstants.STREET)));
			cityTextView.setText(result.getString(result
					.getColumnIndex(AddressBookConstants.CITY)));
			result.close();
		}
	}
}
