package com.example.contentproviderexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClickAddStudent(View view) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyContentProvider.FIRSTNAME, ((EditText) findViewById(R.id.firstName)).getText().toString());
        contentValues.put(MyContentProvider.LASTNAME, ((EditText) findViewById(R.id.lastName)).getText().toString());
        contentValues.put(MyContentProvider.GENDER, ((EditText) findViewById(R.id.gender)).getText().toString());
        contentValues.put(MyContentProvider.PLACE, ((EditText) findViewById(R.id.place)).getText().toString());
        contentValues.put(MyContentProvider.SCHOLARSHIP, ((EditText) findViewById(R.id.scholarship)).getText().toString());

        Uri uri = getContentResolver().insert(MyContentProvider.CONTENT_URI, contentValues);
        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
    }

    public void onClickLoadStudent(View view) {
        String URL = "content://com.example.contentproviderexample.MyContentProvider";
        Uri students = Uri.parse(URL);
        Cursor cursor = getContentResolver().query(students, null, null, null, null);
        String myStudents = null;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            myStudents = myStudents + cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider._ID)) + ", " +
                    cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.FIRSTNAME)) + ", " +
                    cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.LASTNAME)) + ", " +
                    cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.GENDER)) + ", " +
                    cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.PLACE)) + ", " +
                    cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.SCHOLARSHIP));
        }

        Toast.makeText(this, myStudents, Toast.LENGTH_SHORT).show();
    }
}