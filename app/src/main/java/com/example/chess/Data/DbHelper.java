package com.example.chess.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    //Declaration
    private static final String NAME_DB = "ChessDb";
    public static final String CHESS_COLUMN_ACCOUNT_NAME = "Account";
    public static final String ACCOUNT_ID = "AccountID";
    public static final String ACCOUNT_PASSWORD = "AccountPassword";
    public static final String ACCOUNT_EMAIL = "AccountEmail";

    //Create Table Account Query
    private static final String CREATE_TABLE_ACCOUNT = ""
            + "create table " + CHESS_COLUMN_ACCOUNT_NAME + " ( "
            + ACCOUNT_ID +" string primary key ,"
            + ACCOUNT_PASSWORD + " text not null, "
            + ACCOUNT_EMAIL + " text not null );"
            ;

    /*
     context: dùng để mở hoặc tạo cơ sở dữ liệu
     TEN_DATABASE: tên file cơ sở dữ liệu.
     null: dùng để tạo đối tượng Cursor, hoặc null thì mặc định.
     1: phiên bản của cơ sở dữ liệu, nếu là cơ sở dữ liệu cũ thì nó sẽ vào phương thức “onUpgrade” ngược lại thì vào “onDowngrade”.
     */
    public DbHelper(Context context) {
        super(context, NAME_DB, null ,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_ACCOUNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
