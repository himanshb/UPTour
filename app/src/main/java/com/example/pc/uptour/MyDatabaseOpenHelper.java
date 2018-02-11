package com.example.pc.uptour;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by PC on 1/17/2018.
 */

public class MyDatabaseOpenHelper extends SQLiteAssetHelper {
    private static String DB_NAME="upst_db";
    private static int VERSION=1;

    public MyDatabaseOpenHelper(Context context) {
        super(context, DB_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, VERSION);
        // super(context, DB_NAME, null, VERSION);
    }
}
