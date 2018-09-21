package com.example.pc_asus.tinhnguyenvien;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    EditText edt_email,edt_password;
    Button btn_signIn, btn_signUp;
    TextView tv_forgotPw;
    private FirebaseAuth mAuth;
// ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        // Intent intent= new Intent(MainActivity.this,VideoChatViewActivity.class);
        // startActivity(intent);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        btn_signIn = findViewById(R.id.btn_dangNhap);
        btn_signUp = findViewById(R.id.btn_dangKi);
        tv_forgotPw = findViewById(R.id.tv_forgotPw);
        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getBundleExtra("Bundle");
        if(bundle != null){
            edt_email.setText(bundle.getString("Email"));
            edt_password.setText(bundle.getString("Password"));
            Log.e("bundle thanh cong","Gia tri");
        }


        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangNhap();
            }
        });
        tv_forgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPW();
            }
        });
    }

    private void forgotPW() {
        final String userEmail = edt_email.getText().toString().trim();

        if (userEmail.equals(""))
        {
            Toast.makeText(SignInActivity.this,"Vui lòng nhập địa chỉ Email",Toast.LENGTH_SHORT).show();

        }else {
            mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SignInActivity.this,"Email đặt lại mật khẩu đã được gửi", Toast.LENGTH_LONG).show();
                        edt_email.setText("");
                        edt_email.setHint(userEmail);
                    }
                    else Toast.makeText(SignInActivity.this,"Yêu cầu đặt lại mật khẩu không thành công",Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.e("abc","trang thai dang nhap "+currentUser);
        if(currentUser!=null) {

          //  Intent intent= new Intent(SignInActivity.this,VideoCallActivity.class);
              Intent intent= new Intent(SignInActivity.this,MainActivity.class);

            startActivity(intent);
            finish();
        }
    }

    private void  dangNhap() {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("loading...");

        String email = edt_email.getText().toString().trim();
        final String password = edt_password.getText().toString().trim();
        Log.e("abc",email);
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignInActivity.this, "Đăng Nhập Thất Bại", Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                checkEmailValification();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(SignInActivity.this, "Đăng Nhập Thất Bại", Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });

        }
    }
    private void checkEmailValification(){
        FirebaseUser firebaseUser = mAuth.getInstance().getCurrentUser();
        Boolean emailFlag = firebaseUser.isEmailVerified();
        if(emailFlag){
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            Bundle bundle = getIntent().getBundleExtra("Bundle");
            if (bundle != null){
                User user= new User(bundle.getString("Name"),bundle.getString("Email"), bundle.getString("Phone"),"https://firebasestorage.googleapis.com/v0/b/map-82eb0.appspot.com/o/generic-user-purple.png?alt=media&token=21815e8a-2bcd-477a-bf37-f6b382f0c409");
                FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
                String uid=currentUser.getUid();
                DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("TinhNguyenVien").child("Users").child(uid);
                mDatabase.setValue(user);
                Status status= new Status("1","1","0");
                DatabaseReference mDatabase2= FirebaseDatabase.getInstance().getReference().child("TinhNguyenVien").child("Status").child(uid);
                mDatabase2.setValue(status);
            }
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }else{
            Toast.makeText(this,"Xác nhận email của bạn",Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }

}


