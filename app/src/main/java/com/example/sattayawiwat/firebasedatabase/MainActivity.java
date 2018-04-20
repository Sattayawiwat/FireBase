package com.example.sattayawiwat.firebasedatabase;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    EditText editTextName, editTextEmail;
    Button btnSubmit,btnChoose;
    ImageView imageView;
    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStoreageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextName = (EditText) findViewById(R.id.et_name);
        editTextEmail = (EditText) findViewById(R.id.et_email);
        btnSubmit = (Button)findViewById(R.id.btn_submit);
        btnChoose = (Button)findViewById(R.id.btn_choosPic);
        imageView = (ImageView) findViewById(R.id.image_view);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        database = FirebaseDatabase.getInstance();
        mStoreageRef = FirebaseStorage.getInstance().getReference("uploads");

        myRef = database.getReference("User");
        //++++++++++++++++++ Onclick method ++++++++++
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFileChooser();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPersonData();

            }
        });
    }

    private void OpenFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode ==RESULT_OK && data!=null && data.getData()!=null){
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(imageView);
        }
    }

    private void addPersonData(){
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        if(!TextUtils.isEmpty(name) && imageView !=null){
     /*       String id = myRef.push().getKey();
            Person user = new Person(name,email,id);
            myRef.child(id).setValue(user);*/
            // Start Code Storage upload
            StorageReference fileRef = mStoreageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            fileRef.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    },5000);
                    Toast.makeText(MainActivity.this,"Upload Successful!",Toast.LENGTH_SHORT).show();
                    String id = myRef.push().getKey();
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    Person user = new Person(name,email,id,downloadUrl);
                    myRef.child(id).setValue(user);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progressUpload = (100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progressUpload );

                }
            });
            //++++++++End code Storeage upload
            Toast.makeText(this,"User submitted!",Toast.LENGTH_SHORT).show();


        }else {
            Toast.makeText(this,"You should enter all data!",Toast.LENGTH_SHORT).show();

        }
    }
}
