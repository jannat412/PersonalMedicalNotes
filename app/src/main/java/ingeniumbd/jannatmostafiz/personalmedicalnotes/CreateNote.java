package ingeniumbd.jannatmostafiz.personalmedicalnotes;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateNote extends AppCompatActivity {
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    SQLiteDatabase db;
    DbHelper mDbHelper;
    EditText mNameText, Email;
    EditText Number;
    EditText Speciality, MedicalName;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

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
        mDbHelper = new DbHelper(this);
        db = mDbHelper.getWritableDatabase();

        mNameText = (EditText) findViewById(R.id.doctorName);
        Email = (EditText) findViewById(R.id.doctorEmail);
        Number = (EditText) findViewById(R.id.doctorNumber);
        Speciality = (EditText) findViewById(R.id.doctorspeciality);
        MedicalName = (EditText) findViewById(R.id.medicalName);
        btnSave = (Button) findViewById(R.id.submit);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameText.getText().toString();
                String email = Email.getText().toString();
                String number = Number.getText().toString();
                String speciality = Speciality.getText().toString();
                String medicalName =  MedicalName.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put(mDbHelper.NAME, name);
                cv.put(mDbHelper.EMAIL, email);
                cv.put(mDbHelper.NUMBER, number);
                cv.put(mDbHelper.SPECIALITY, speciality);
                cv.put(mDbHelper.MEDICALNAME, medicalName);
                db.insert(mDbHelper.TABLE_NAME, null, cv);

                Intent openMainScreen = new Intent(CreateNote.this, HomePage.class);
                openMainScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(openMainScreen);
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            auth.signOut();

            return true;
        }else if (id == R.id.action_save){
            String name = mNameText.getText().toString();
            String email = Email.getText().toString();
            String number = Number.getText().toString();
            String speciality = Speciality.getText().toString();
            String medicalName =  MedicalName.getText().toString();
            ContentValues cv = new ContentValues();
            cv.put(mDbHelper.NAME, name);
            cv.put(mDbHelper.EMAIL, email);
            cv.put(mDbHelper.NUMBER, number);
            cv.put(mDbHelper.SPECIALITY, speciality);
            cv.put(mDbHelper.MEDICALNAME, medicalName);
            db.insert(mDbHelper.TABLE_NAME, null, cv);

            Intent openMainScreen = new Intent(this, HomePage.class);
            openMainScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(openMainScreen);

            return true;
        }else if (id == R.id.exit) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
