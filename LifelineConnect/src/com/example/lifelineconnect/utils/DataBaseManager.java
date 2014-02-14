package com.example.lifelineconnect.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.example.lifelineconnect.activities.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseManager extends SQLiteOpenHelper {

	// private static String DB_PATH = "";
	private static File DB_PATH;
	private static final String DB_NAME = "LifeLineConnect";

	private static SQLiteDatabase myDataBase;
	private final Context myContext;

	private static DataBaseManager mDBConnection;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	private DataBaseManager(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		// DB_PATH = "/data/data/"+myContext.getPackageName()+"/databases/";
		try {
			createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// The Android's default system path of your application database is
		// "/data/data/mypackagename/databases/"
		openDataBase();
	}

	/**
	 * getting Instance
	 * 
	 * @param context
	 * @return DBAdapter
	 */

	public static synchronized DataBaseManager getDBAdapterInstance(
			Context context) {
		if (mDBConnection == null) {
			mDBConnection = new DataBaseManager(context);
		}
		return mDBConnection;
	}

	/**
	 * Creates an empty database on the system and rewrites it with your own
	 * database.
	 **/
	public void createDataBase() throws IOException {
		// Log.d("database", "Create Database");

		DB_PATH = myContext.getExternalCacheDir();
		DB_PATH.mkdirs();
		File db = new File(DB_PATH, DB_NAME);
		if (!db.exists()) {
			db.createNewFile();
			try {

				// copyDataBase();
				copyFromZipFile();

			} catch (IOException e) {

				throw new Error("Error copying database", e);

			}
		}

	}

	private void copyFromZipFile() throws IOException {
		InputStream is = myContext.getResources().openRawResource(R.raw.lifelineconnect);
		// Path to the just created empty db
		File outFile = new File(DB_PATH, DB_NAME);
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFile.getAbsolutePath());
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
		try {
			@SuppressWarnings("unused")
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int count;
				while ((count = zis.read(buffer)) != -1) {
					baos.write(buffer, 0, count);
					// Log.d("", buffer.toString());
				}
				baos.writeTo(myOutput);

			}
		} finally {
			zis.close();
			myOutput.flush();
			myOutput.close();
			is.close();
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	@SuppressWarnings("unused")
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	@SuppressWarnings("unused")
	private void copyDataBase() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;

		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Open the database
	 * 
	 * @throws SQLException
	 */
	/*
	 * public void openDataBase() throws SQLException { String myPath = DB_PATH
	 * + DB_NAME; myDataBase = SQLiteDatabase.openDatabase(myPath, null,
	 * SQLiteDatabase.OPEN_READWRITE); }
	 */
	public SQLiteDatabase openDataBase() throws SQLException {

		File DB_PATH = myContext.getExternalCacheDir();
		File dbFile = new File(DB_PATH, DB_NAME);
		myDataBase = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(),
				null, SQLiteDatabase.OPEN_READWRITE);
		Log.d("database path:", myDataBase.getPath());

		return myDataBase;
	}

	/**
	 * Close the database if exist
	 */
	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	/**
	 * Call on creating data base for example for creating tables at run time
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	/**
	 * can used for drop tables then call onCreate(db) function to create tables
	 * again - upgrade
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// ----------------------- CRUD Functions ------------------------------

	/**
	 * This function used to select the records from DB.
	 * 
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return A Cursor object, which is positioned before the first entry.
	 */
	public Cursor selectRecordsFromDB(String tableName, String[] tableColumns,
			String whereClase, String whereArgs[], String groupBy,
			String having, String orderBy) {
		return myDataBase.query(tableName, tableColumns, whereClase, whereArgs,
				groupBy, having, orderBy);
	}

	/**
	 * select records from db and return in list
	 * 
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return ArrayList<ArrayList<String>>
	 */

	/**
	 * This function used to insert the Record in DB.
	 * 
	 * @param tableName
	 * @param nullColumnHack
	 * @param initialValues
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	/*
	 * public long insertRecordsInDB(String tableName, ContentValues
	 * initialValues) {
	 * 
	 * return myDataBase.insert(tableName, null, initialValues); }
	 */
	public long insert(String table, ContentValues values) {

		return myDataBase.insert(table, null, values);
	}

	/**
	 * This function used to update the Record in DB.
	 * 
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return true / false on updating one or more records
	 */
	public boolean updateRecordInDB(String tableName,
			ContentValues initialValues, String whereClause, String whereArgs[]) {
		return myDataBase.update(tableName, initialValues, whereClause,
				whereArgs) > 0;
	}

	/**
	 * This function used to update the Record in DB.
	 * 
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are updated
	 */
	/*
	 * public int updateRecordsInDB(String tableName,ContentValues
	 * initialValues, String whereClause, String whereArgs[]) { return
	 * myDataBase.update(tableName, initialValues, whereClause, whereArgs); }
	 */
	public void update(String table, ContentValues values, String where) {

		myDataBase.update(table, values, where, null);

	}

	/**
	 * This function used to delete the Record in DB.
	 * 
	 * @param tableName
	 * 
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are deleted.
	 */
	public int deleteRecordInDB(String tableName, String whereClause,
			String[] whereArgs) {
		return myDataBase.delete(tableName, whereClause, whereArgs);
	}

	// --------------------- Select Raw Query Functions ---------------------

	/**
	 * apply raw Query
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return Cursor
	 */
	/*
	 * public Cursor selectRecordsFromDB(String query, String[] selectionArgs) {
	 * return myDataBase.rawQuery(query, selectionArgs); }
	 */

	public Cursor rawQuery(String query) {

		return myDataBase.rawQuery(query, null);
	}

	/**
	 * apply raw query and return result in list
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return ArrayList<ArrayList<String>>
	 */

	
}
