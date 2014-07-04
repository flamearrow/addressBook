package com.ml.gb.addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

// handles sqlite db actions
// all databases are stored at /data/data/<package_name>/databases
public class DatabaseConnector {
	private static final String DB_NAME = "UserContacts";
	private SQLiteDatabase db;
	private DBOpenHelper dbOpenHelper;

	private class DBOpenHelper extends SQLiteOpenHelper {
		public DBOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// execute sql by create a stirng
			String createQuery = "CREATE TABLE "
					+ AddressBookConstants.TABLE_NAME
					+ " (_id INTEGER primary key autoincrement, "
					+ "name TEXT, email TEXT, phone TEXT, street TEXT, city TEXT);";
			db.execSQL(createQuery);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// nothing
		}

	};

	public DatabaseConnector(Context context) {
		dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, 1);
	}

	public void open() {
		// create a R/W db
		db = dbOpenHelper.getWritableDatabase();
	}

	public void close() {
		if (db != null)
			db.close();
	}

	// SELECT _id, name FROM contacts ORDERBY name
	public Cursor getAllContacts() {
		// get All is only used for displaying in the listActivity, so we only
		// need two columns
		return db.query(AddressBookConstants.TABLE_NAME, new String[] { "_id",
				"name" }, null, null, null, null, "name");
	}

	// SELECT * from contacts WHERE _id=id
	public Cursor getOneContact(long id) {
		return db.query(AddressBookConstants.TABLE_NAME, null, "_id=" + id,
				null, null, null, null);
	}

	public void deleteContact(long id) {
		open();
		// DELETE FROM contacts
		// WHERE _id=id
		db.delete(AddressBookConstants.TABLE_NAME, "_id=" + id, null);
		close();
	}

	// use a ContentValues object to accomplish insert, no need to write another
	// insert SQL, can use execSQL() as well
	public void insertContact(String name, String email, String phone,
			String street, String city) {
		ContentValues newContact = new ContentValues();
		newContact.put(AddressBookConstants.NAME, name);
		newContact.put(AddressBookConstants.EMAIL, email);
		newContact.put(AddressBookConstants.PHONE, phone);
		newContact.put(AddressBookConstants.STREET, street);
		newContact.put(AddressBookConstants.CITY, city);

		// need to open() and close() db each time
		open();
		// INSERT INTO contacts
		// VALUES(name, email, phone, street)
		db.insert(AddressBookConstants.TABLE_NAME, null, newContact);
		close();
	}

	public void udpateContact(long rowID, String name, String email,
			String phone, String street, String city) {
		ContentValues editContact = new ContentValues();
		editContact.put(AddressBookConstants.NAME, name);
		editContact.put(AddressBookConstants.EMAIL, email);
		editContact.put(AddressBookConstants.PHONE, phone);
		editContact.put(AddressBookConstants.STREET, street);
		editContact.put(AddressBookConstants.CITY, city);

		open();
		// UPDATE contacts
		// SET name=name, email=email, phone=phone, street=street, city=city
		// WHERE _id=id
		db.update(AddressBookConstants.TABLE_NAME, editContact, "_id=" + rowID,
				null);
		close();
	}

}
