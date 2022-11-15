package com.tugrulkara.quotesadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText email,password;
    private MaterialRippleLayout materialRippleLayout;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        email=findViewById(R.id.edit_txt_email);
        password=findViewById(R.id.edit_txt_password);
        materialRippleLayout=findViewById(R.id.sign_in_lyt);

        materialRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn(v);
            }
        });

        if (firebaseUser!=null){
            Intent intent=new Intent(com.tugrulkara.quotesadmin.LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void SignIn(View view){

        if (email.getText().toString().trim().length() <=0 || password.getText().toString().trim().length() <=0){
            Toast.makeText(com.tugrulkara.quotesadmin.LoginActivity.this,"Lütfen email ve parolanızı girin!",Toast.LENGTH_SHORT).show();
        }else{
            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent=new Intent(com.tugrulkara.quotesadmin.LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(com.tugrulkara.quotesadmin.LoginActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}