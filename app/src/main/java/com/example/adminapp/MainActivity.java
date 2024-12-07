package com.example.adminapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.adminapp.faculty.UpdateFaculty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView uploadNotice,addGalareyImage,addEbook,faculty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadNotice=findViewById(R.id.addNotice);
        addGalareyImage=findViewById(R.id.addGalaryImage);
        addEbook=findViewById(R.id.addEbook);
        faculty=findViewById(R.id.faculty);

        uploadNotice.setOnClickListener(this);
        faculty.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addNotice) {
            Intent intent = new Intent(MainActivity.this, UploadNotice.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.addGalaryImage){
            Intent intent = new Intent(MainActivity.this, UploadImage.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.addEbook){
            Intent intent = new Intent(MainActivity.this, UploadPdfActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.faculty){
            Intent intent = new Intent(MainActivity.this, UpdateFaculty.class);
            startActivity(intent);
        }
    }
}