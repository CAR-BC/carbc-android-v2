package SQLiteDBActivities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "carbc.db";
    public static final String DB_LOCATION = "/data/data/com.example.madhushika.carbc_android_v3/databases/";

    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDatabase() {
        String dbpath = context.getDatabasePath(DATABASE_NAME).getPath();
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            return;
        }
        sqLiteDatabase = SQLiteDatabase.openDatabase(DB_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);
    }


    public void closeDB() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
    }

}
