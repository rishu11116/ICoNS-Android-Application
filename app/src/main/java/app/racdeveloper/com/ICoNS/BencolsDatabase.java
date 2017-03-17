package app.racdeveloper.com.ICoNS;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 08-08-2016.
 */
public class BencolsDatabase extends SQLiteOpenHelper {
    public BencolsDatabase(Context context) {
        super(context, Constants.DB_Name, null, Constants.DB_NewVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, Constants.DB_NewVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<1){
            db.execSQL("CREATE TABLE collegeId (_id INTEGER PRIMARY KEY AUTOINCREMENT, CollegeId TEXT unique);");
        }
    }

    private void insertId(SQLiteDatabase db, String Table_Name, String CollegeId) {
        ContentValues id= new ContentValues();
        id.put("CollegeId", CollegeId);
        db.insert(Table_Name, null, id);
    }
}
