package com.example.contactbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recycler;
    ArrayList<ContactApp> arrayList = new ArrayList<ContactApp>();
    MainAdapter adapter;
    FloatingActionButton floatButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recycler);
        checkPermission();

        floatButton = findViewById(R.id.floatButton);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });



    }

    private void openNewActivity() {
        Intent intent = new Intent(this, FeedContact.class);
        startActivity(intent);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContactList();
        }
    }

    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC";
        Cursor cursor = getContentResolver().query(
                uri, null, null, null, sort
        );
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";

                Cursor phoneCursor = getContentResolver().query(
                        uriPhone, null, selection,
                        new String[]{id}, null
                );
                if (phoneCursor.moveToNext()) {
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));
                    ContactApp model = new ContactApp();
                    model.setName(name);
                    model.setNumber(number);
                    arrayList.add(model);
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainAdapter(this, arrayList);
        recycler.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {
            getContactList();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }

}