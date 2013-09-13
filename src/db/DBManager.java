package db;

import java.util.ArrayList;
import java.util.List;

import com.shixforever.reader.data.BookFile;
import com.shixforever.reader.module.BookMark;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;
	private static final String DATABASE_NAME = "wow.db";
	private static final int DATABASE_VERSION = 1;

	public DBManager(Context context) {
		helper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		db = helper.getWritableDatabase();
	}

	/**
	 * 
	 * @param persons
	 */
	public void add(List<BookFile> persons) {
		db.beginTransaction();
		try {
			for (BookFile person : persons) {
				db.execSQL("INSERT INTO wowskill VALUES(null, ?, ?)",
						new Object[] { person.name, person.file });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void addMarks(BookMark mark) {
		db.beginTransaction();
		try {
			db.execSQL("INSERT INTO marks VALUES(null, ?, ?,?)", new Object[] {
					mark.begin, mark.word, mark.name });
			db.setTransactionSuccessful();
		} finally {

			db.endTransaction();
		}
	}

	/**
	 * update book
	 * 
	 * @param person
	 */
	public void updateAge(BookFile person) {
		ContentValues cv = new ContentValues();
		cv.put("age", person.name);
		db.update("wowskill", cv, "name = ?", new String[] { person.name });
	}

	/**
	 * delete book 
	 * 
	 * @param person
	 */
	public void deleteBook(BookFile person) {
		db.delete("story", "name = ?",
				new String[] { String.valueOf(person.name) });
	}

	/**
	 * 
	 * @return List<Person>
	 */
	public ArrayList<BookFile> queryPro(String name) {
		// List<Profession> professions = new ArrayList<Profession>();
		ArrayList<BookFile> books = new ArrayList<BookFile>();
		Cursor c = queryTheCursor("story", name);
		while (c.moveToNext()) {
			BookFile book = new BookFile();
			book.id = c.getInt(c.getColumnIndex("_id"));
			book.name = c.getString(c.getColumnIndex("name"));
			book.file = c.getBlob(c.getColumnIndex("introduce"));
			books.add(book);
		}
		c.close();
		return books;
	}

	public ArrayList<BookFile> queryPro() {
		// List<Profession> professions = new ArrayList<Profession>();
		ArrayList<BookFile> books = new ArrayList<BookFile>();
		Cursor c = queryTheCursor("story");
		while (c.moveToNext()) {
			BookFile book = new BookFile();
			book.id = c.getInt(c.getColumnIndex("id"));
			book.name = c.getString(c.getColumnIndex("name"));
			book.file = c.getBlob(c.getColumnIndex("file"));
			book.cover = c.getString(c.getColumnIndex("cover"));
			book.flag= c.getString(c.getColumnIndex("flag"));
			books.add(book);
		}
		c.close();
		return books;
	}

	public ArrayList<BookMark> queryMarks(String name) {
		// List<Profession> professions = new ArrayList<Profession>();
		ArrayList<BookMark> books = new ArrayList<BookMark>();
		Cursor c = queryTheCursor("marks", name);
		while (c.moveToNext()) {
			BookMark book = new BookMark();
			book.id = c.getInt(c.getColumnIndex("id")) + "";
			book.begin = c.getInt(c.getColumnIndex("begin"));
			book.word = c.getString(c.getColumnIndex("word"));
			books.add(book);
		}
		c.close();
		return books;
	}

	/**
	 * 
	 * 
	 * @return Cursor
	 */
	public Cursor queryTheCursor(String table, String name) {
		Cursor c = db.rawQuery("SELECT * FROM " + table + " where name ='"
				+ name + "'", null);
		return c;
	}

	public Cursor queryTheCursor(String table) {
		Cursor c = db.rawQuery("SELECT * FROM " + table, null);
		return c;
	}

	/**
	 * close database
	 */
	public void closeDB() {
		db.close();
	}

}
