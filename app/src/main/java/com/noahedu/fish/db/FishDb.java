package com.noahedu.fish.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FishDb {
	private static final String DB_NAME = "FishDb";
	private static final int DB_VERSION = 1;
	private SQLiteDatabase mDb = null;
	private DatabaseHelper mDbHelper = null;
	private final Context ctx;

	public FishDb(Context ctx) {
		this.ctx = ctx;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context ctx) {
			super(ctx, DB_NAME, null, DB_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(ScoreTable.CREATE_FISH_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	/**
	 * 打开数据库
	 * 
	 * @return 数据库的实例
	 * @throws SQLException
	 */
	public FishDb open() throws SQLException {
		mDbHelper = new DatabaseHelper(ctx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		mDbHelper.close();
	}

	/**
	 * 增加一条记录
	 * 
	 * @param score
	 *            分数
	 */
	public void insertScore(int score) {
		ContentValues values = new ContentValues();
		values.put(ScoreTable.SCORE, score);
		mDb.insert(ScoreTable.FISH_TABLE, null, values);
	}

	/**
	 * 获取所有的分数记录
	 * 
	 * @return 所有分数列表
	 */
	public List<ScoreInfo> queryAllScores() {
		String[] columns = new String[] { ScoreTable.ID, ScoreTable.SCORE };
		Cursor mSCursor = mDb.query(ScoreTable.FISH_TABLE, columns, null, null,
				null, null, ScoreTable.SCORE + " desc");
		List<ScoreInfo> mScoreInfoList = new ArrayList<ScoreInfo>();
		if (mSCursor != null) {
			while (mSCursor.moveToNext()) {
				ScoreInfo mScoreInfo = new ScoreInfo();
				mScoreInfo.setId(mSCursor.getInt(0));
				mScoreInfo.setScore(mSCursor.getInt(1));
				mScoreInfoList.add(mScoreInfo);
			}
		}
		mSCursor.close();
		System.gc();
		return mScoreInfoList;
	}
}
