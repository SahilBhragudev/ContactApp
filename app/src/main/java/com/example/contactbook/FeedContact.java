package com.example.contactbook;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Session2Command;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.contactbook.databinding.ActivitySavecontactBinding;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FeedContact extends AppCompatActivity {

    private ActivitySavecontactBinding binding;
    private static final String TAG = "CONTACT_TAG";
    private static final int WRITE_CONTACT_PERMISSION_CODE = 100;
    private static final int IMAGE_PICK_GALLERY_CODE = 100;
    private String[] contactPermissions;
    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySavecontactBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_savecontact);


        contactPermissions = new String[]{Manifest.permission.WRITE_CONTACTS};

        binding.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryIntent();
            }
        });

        binding.floatbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWriteContactPermissionEnabled()){
                    saveContact();
                }
                else{
                    requestWriteContactPermission();
                }
            }
        });
    }

    private void saveContact() {
        String firstName = binding.et1.getText().toString().trim();
        String lastName = binding.et2.getText().toString().trim();
        String phoneNumber = binding.et3.getText().toString().trim();
        String email = binding.et4.getText().toString().trim();
        String address = binding.et5.getText().toString().trim();

        ArrayList<ContentProviderOperation> cpo = new ArrayList<>();
        int rawContactid = cpo.size();
        cpo.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactid)
                .withValueBackReference(ContactsContract.RawContacts.Data.MIMETYPE, Integer.parseInt(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE))
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
                .build());


        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactid)
                .withValueBackReference(ContactsContract.RawContacts.Data.MIMETYPE, Integer.parseInt(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE))
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactid)
                .withValueBackReference(ContactsContract.RawContacts.Data.MIMETYPE, Integer.parseInt(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE))
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactid)
                .withValueBackReference(ContactsContract.RawContacts.Data.MIMETYPE, Integer.parseInt(ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE))
                .withValue(ContactsContract.CommonDataKinds.SipAddress.DATA, address)
                .withValue(ContactsContract.CommonDataKinds.SipAddress.TYPE, ContactsContract.CommonDataKinds.SipAddress.TYPE_WORK)
                .build());

        byte[] imageBytes = imageUriToBytes();
        if (imageBytes != null) {
            cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactid)
                    .withValueBackReference(ContactsContract.RawContacts.Data.MIMETYPE, Integer.parseInt(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE))
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, imageBytes)
                    .build());
        }
        else {

        }
        try {
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, cpo);
            Log.d(TAG, "saveContact: Saved...");
            Toast.makeText(this, "Saved...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "saveContact:" + e.getMessage());
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private byte[] imageUriToBytes() {
        Bitmap bitmap;
        ByteArrayOutputStream baos = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            Log.d(TAG, "imageUriToBytes:" + e.getMessage());
            return null;
        }
    }

    private void openGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean isWriteContactPermissionEnabled() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestWriteContactPermission() {
        ActivityCompat.requestPermissions(this, contactPermissions, WRITE_CONTACT_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            if (requestCode == WRITE_CONTACT_PERMISSION_CODE) {
                boolean haveWriteContactPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (haveWriteContactPermission) {
                    saveContact();
                }
                else{
                    Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                assert data != null;
                image_uri = data.getData();
                binding.imageview.setImageURI(image_uri);
            }
        } else {
            Toast.makeText(this, "Cancelled...", Toast.LENGTH_SHORT).show();
        }
    }
}