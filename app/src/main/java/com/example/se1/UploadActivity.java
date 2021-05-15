package com.example.se1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Spinner spinner;
    private TextView itemNameTV;
    private TextView itemCategoryTV;
    private TextView quantityTV;
    private TextView descriptionTV;
    private TextView changeTV;
    private EditText itemNameET;
    private EditText quantityET;
    private EditText descriptionET;
    private ImageView imageView;
    private ImageButton chooseImgBtn;
    private ImageButton plusBtn;
    private ImageButton minusBtn;
    private FloatingActionButton uploadBtn;

    private Uri imageUri;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private StorageTask uploadTask;
    boolean categoryBool = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        itemNameET = findViewById(R.id.editTextTextPersonName2);
        quantityET = findViewById(R.id.editTextNumber);
        descriptionET = findViewById(R.id.editTextTextPersonName4);
        imageView = findViewById(R.id.image_view);
        chooseImgBtn = findViewById(R.id.chooseImage_Btn);
        uploadBtn = findViewById(R.id.upload_Btn);
        plusBtn = findViewById(R.id.plusBtn);
        minusBtn = findViewById(R.id.minusBtn);
        changeTV = findViewById(R.id.textView10);
        changeTV.setVisibility(View.GONE);

        chooseImgBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        plusBtn.setOnClickListener(this);
        minusBtn.setOnClickListener(this);
        changeTV.setOnClickListener(this);
        quantityET.setText("1");

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.item_category, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");

    }

    @Override //spinner listener
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("Select item's category")){
            Toast.makeText(this, "Please select item category!", Toast.LENGTH_SHORT).show();
        }else {
            String category = parent.getItemAtPosition(position).toString();
            Toast.makeText(this, category, Toast.LENGTH_SHORT).show();
            categoryBool = true;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override //button click listener
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseImage_Btn:
            case R.id.textView10:
                chooseImage();
                break;
            case R.id.upload_Btn: uploadImage();
                break;
            case R.id.plusBtn:
                quantity('+');
                break;
            case R.id.minusBtn:
                quantity('-');
                break;

        }

    }

    private void quantity(char c) {
        int newQuantity = Integer.parseInt(quantityET.getText().toString());
        if(c == '+'){
            //newQuantity = newQuantity+1;
            quantityET.setText(String.valueOf(newQuantity+1));
        }else if(newQuantity>1 && c == '-'){
            quantityET.setText(String.valueOf(newQuantity-1));
        }
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        //startActivity will passed int requestCode which is PICK_IMAGE to onActivity after the activity end
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
            chooseImgBtn.setVisibility(View.GONE);
            changeTV.setVisibility(View.VISIBLE);
        }
    }

    private String getFileExtension(Uri uri){ //to get image file extension (jpg/jpeg/png)
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private boolean check(){
        boolean check = false;
        if(!itemNameET.getText().toString().isEmpty() && categoryBool &&
                !quantityET.getText().toString().isEmpty()){
            check = true;
        }
        return check;
    }

    private void uploadImage() {
        if(imageUri != null){
            if(check()){
                String itemName = itemNameET.getText().toString().trim();
                String itemQuantity = quantityET.getText().toString().trim();
                String itemDescription = descriptionET.getText().toString().trim();
                StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "."
                        + getFileExtension(imageUri));
                uploadTask = fileRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(UploadActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        upload uploads = new upload(uri.toString(),
                                                itemName,
                                                itemQuantity,
                                                itemDescription);
                                        databaseRef.child("upload1.0.0").setValue(uploads);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }else{
                Toast.makeText(this, "Please fill all the requirement", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
        }
    }
}