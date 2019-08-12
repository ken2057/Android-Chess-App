package com.example.chess.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbContext {
    SQLiteDatabase database;
    DbHelper helper;

    public DbContext(Context context){
        helper = new DbHelper(context);
        database = helper.getWritableDatabase();
    }

    public Cursor getAllData(){
        String[] column = {
                DbHelper.ACCOUNT_ID,
                DbHelper.ACCOUNT_PASSWORD,
                DbHelper.ACCOUNT_EMAIL
        };
        Cursor cursor = null;
        cursor = database.query(
                DbHelper.CHESS_COLUMN_ACCOUNT_NAME,
                column,
                null,
                null,
                null,
                null,
                DbHelper.ACCOUNT_ID+ " DESC"
        );

        return cursor;
    }

    public long add(Account account){
        /*
         * ContentValues là đối tượng lưu trữ dữ liệu, và
         *  SQLiteDatabase sẽ nhận dữ liệu thông qua đối tượng
         *  này để thực hiện các câu lệnh truy vấn.
         */
        ContentValues values = new ContentValues();
        values.put(
                DbHelper.ACCOUNT_ID,
                account.getAccountID()
        );
        values.put(
                DbHelper.ACCOUNT_PASSWORD,
                account.getAccountPassword()
        );
        values.put(
                DbHelper.ACCOUNT_EMAIL,
                account.getAccountEmail()
        );

        return database.insert(
                DbHelper.CHESS_COLUMN_ACCOUNT_NAME,
                null,
                values
        );
    }

    //Check Account Exist in DB
    public boolean check(Account account){
        String[] column = {
                DbHelper.ACCOUNT_ID,
                DbHelper.ACCOUNT_PASSWORD,
        };
        Cursor cursor;
        String whereClause = DbHelper.ACCOUNT_ID + " = ? AND " + DbHelper.ACCOUNT_PASSWORD +" = ?";
        String[] whereArgs = new String[] {
                account.getAccountID(),
                account.getAccountPassword()
        };
        cursor = database.query(
                DbHelper.CHESS_COLUMN_ACCOUNT_NAME,
                column,
                whereClause,
                whereArgs,
                null,
                null,
                DbHelper.ACCOUNT_ID+ " DESC"
        );

        return cursor.getCount() != 0;
    }
}
