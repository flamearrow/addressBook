package com.ml.gb.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ml.gb.addressbook.R;

public class AddEditContact extends Activity {
	private long rowID; // db row id?

	private EditText nameEditText;
	private EditText phoneEditText;
	private EditText emailEditText;
	private EditText streetEditText;
	private EditText cityEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contact);

		nameEditText = (EditText) findViewById(R.id.nameEditText);
		phoneEditText = (EditText) findViewById(R.id.phoneEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
		streetEditText = (EditText) findViewById(R.id.streetEditText);
		cityEditText = (EditText) findViewById(R.id.cityEditText);

		// get parameters from the activity that creates this activity
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			rowID = extras.getLong(AddressBookConstants.ROW_ID);
			nameEditText.setText(extras.getString(AddressBookConstants.NAME));
			phoneEditText.setText(extras.getString(AddressBookConstants.PHONE));
			emailEditText.setText(extras.getString(AddressBookConstants.EMAIL));
			streetEditText.setText(extras
					.getString(AddressBookConstants.STREET));
			cityEditText.setText(extras.getString(AddressBookConstants.CITY));
		}

		Button saveContactButton = (Button) findViewById(R.id.saveContactButton);
		saveContactButton.setOnClickListener(saveContactButtonClicked);
	}

	OnClickListener saveContactButtonClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// when clicked, create an asyncTask to connect to db and save
			// contact
			if (nameEditText.getText().length() != 0) {
				AsyncTask<Object, Object, Object> saveContactTask = new AsyncTask<Object, Object, Object>() {

					@Override
					protected Object doInBackground(Object... params) {
						// save contact to db
						saveContact();
						return null;
					}

					protected void onPostExecute(Object result) {
						// !!kill this activity and return to the previous
						// activity
						finish();
					}
				};
				saveContactTask.execute((Object[]) null);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						AddEditContact.this);
				builder.setTitle(R.string.errorTitle);
				builder.setMessage(R.string.errorMessage);
				builder.setPositiveButton(R.string.errorButton, null);
				builder.show();
			}
		}
	};

	private void saveContact() {
		DatabaseConnector dbc = new DatabaseConnector(this);
		// add
		if (getIntent().getExtras() == null) {
			dbc.insertContact(nameEditText.getText().toString(), emailEditText
					.getText().toString(), phoneEditText.getText().toString(),
					streetEditText.getText().toString(), cityEditText.getText()
							.toString());
		}
		// edit
		else {
			dbc.udpateContact(rowID, nameEditText.getText().toString(),
					emailEditText.getText().toString(), phoneEditText.getText()
							.toString(), streetEditText.getText().toString(),
					cityEditText.getText().toString());
		}
	}

}
