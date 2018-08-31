package com.example.pc_asus.tinhnguyenvien;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    EditText edt_email,edt_password,edt_hoTen,edt_sdt;
    Button  btn_dangKy;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edt_email = findViewById(R.id.edt_email2);
        edt_password = findViewById(R.id.edt_pass2);
        edt_hoTen = findViewById(R.id.edt_hoTen);
        edt_sdt = findViewById(R.id.edt_sdt);

        btn_dangKy= findViewById(R.id.btn_dangKy2);
        mAuth = FirebaseAuth.getInstance();
        btn_dangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangKy();
            }
        });
    }

    private void dangKy() {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("loading...");

        String email = edt_email.getText().toString().trim();
        final String password = edt_password.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Đăng Ký Thất Bại", Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Đăng Ký Thành Công", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                dialog.dismiss();
                                if (password.length() < 6) {
                                    Toast.makeText(SignUpActivity.this, "    Đăng Ký Thất Bại\nMật Khẩu Không Được Dưới 6 Ký Tự", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(SignUpActivity.this, "Đăng Ký Thất Bại", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }
}
