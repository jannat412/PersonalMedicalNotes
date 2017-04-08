package ingeniumbd.jannatmostafiz.personalmedicalnotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jannat Mostafiz on 4/8/2017.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "doctor";
    public static final String TABLE_NAME = "doctor_table";
    public static final String C_ID = "_id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String NUMBER = "number";
    public static final String SPECIALITY = "speciality";
    public static final String MEDICALNAME = "medicalname";
    public static final int VERSION = 2;

    private final String createDB = "create table if not exists " + TABLE_NAME + " ( "
            + C_ID + " integer primary key autoincrement, "
            + NAME + " text, "
            + EMAIL + " text, "
            + NUMBER + " text, "
            + MEDICALNAME + " text, "
            + SPECIALITY + " text)";

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
    }
    public void removeSingleContact(int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + C_ID + "= '" + id + "'");

        database.close();
    }
}
