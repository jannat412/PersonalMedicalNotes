package ingeniumbd.jannatmostafiz.personalmedicalnotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class View_Note extends AppCompatActivity {
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    SQLiteDatabase db;
    DbHelper dbHelper;
    TextView Name, Email, Number, Speciality, MedicalName;
    Button ViewPrescription, AddPrescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        toolbar.setTitle("Medical Notes"); // set Title for Toolbar

        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), HomePage.class);
                startActivity(intent);
            }
        });

        final long id = getIntent().getExtras().getLong(getString(R.string.row_id));

        Name = (TextView) findViewById(R.id.docName);
        Email= (TextView) findViewById(R.id.docEmail);
        Number = (TextView) findViewById(R.id.docNumber);
        Speciality = (TextView) findViewById(R.id.docSpeciality);
        MedicalName = (TextView) findViewById(R.id.docMedicalname);
        ViewPrescription = (Button)findViewById(R.id.viewPrescription);
        AddPrescription = (Button)findViewById(R.id.addPrescription);
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(getApplicationContext(), LogIn.class));
                    finish();
                }
            }
        };

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from " + dbHelper.TABLE_NAME + " where " + dbHelper.C_ID + "=" + id, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Name.setText(cursor.getString(cursor.getColumnIndex(dbHelper.NAME)));
                Email.setText(cursor.getString(cursor.getColumnIndex(dbHelper.EMAIL)));
                Number.setText(cursor.getString(cursor.getColumnIndex(dbHelper.NUMBER)));
                Speciality.setText(cursor.getString(cursor.getColumnIndex(dbHelper.SPECIALITY)));
                MedicalName.setText(cursor.getString(cursor.getColumnIndex(dbHelper.MEDICALNAME)));

            }
            cursor.close();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(this, HomePage.class);
        startActivity(setIntent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final long id = getIntent().getExtras().getLong(getString(R.string.rowID));

        switch (item.getItemId()) {
            case R.id.action_logout:
                auth.signOut();
                return true;
            case R.id.exit:
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Exit Application?");
                alertDialogBuilder
                        .setMessage("Click yes to exit!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        moveTaskToBack(true);
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(1);
                                    }
                                })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            case R.id.action_edit:

                Intent openEditNote = new Intent(View_Note.this, Edit_Note.class);
                openEditNote.putExtra(getString(R.string.intent_row_id), id);
                startActivity(openEditNote);
                return true;

            case R.id.action_discard:
                AlertDialog.Builder builder = new AlertDialog.Builder(View_Note.this);
                builder
                        .setTitle(getString(R.string.delete_title))
                        .setMessage(getString(R.string.delete_message))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Cursor cursor =   db.rawQuery("delete from " + dbHelper.TABLE_NAME + " where " + dbHelper.C_ID + "=" + id, null);
                                if (cursor != null) {
                                    if (cursor.moveToFirst()) {
                                        Name.setText(cursor.getString(cursor.getColumnIndex(dbHelper.NAME)));
                                        Email.setText(cursor.getString(cursor.getColumnIndex(dbHelper.EMAIL)));
                                        Number.setText(cursor.getString(cursor.getColumnIndex(dbHelper.NUMBER)));
                                        Speciality.setText(cursor.getString(cursor.getColumnIndex(dbHelper.SPECIALITY)));
                                        MedicalName.setText(cursor.getString(cursor.getColumnIndex(dbHelper.MEDICALNAME)));

                                    }
                                    cursor.close();
                                }
                                db.close();
                                Intent openMainActivity = new Intent(View_Note.this, HomePage.class);
                                startActivity(openMainActivity);

                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)                        //Do nothing on no
                        .show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
