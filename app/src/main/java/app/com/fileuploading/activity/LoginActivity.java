package app.com.fileuploading.activity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.com.fileuploading.R;
import app.com.fileuploading.model.SignupModel;
import app.com.fileuploading.utils.Constants;


public class LoginActivity extends AppCompatActivity {
    private EditText etEmailid;
    private EditText etPassword;
    DatabaseReference mDatabaseReference;
    private Button btnLogin;
    private TextView txtSignup;

    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    SharedPreferences prefs;
    private String TAG="TAG";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_user_login);

        initView();
    }

    private void initView() {

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        progressDialog = new ProgressDialog(this);

        etEmailid = (EditText) findViewById(R.id.edt_emailid);

        etPassword = (EditText) findViewById(R.id.edt_password);

        btnLogin = (Button) findViewById(R.id.btn_login_with_student);

        txtSignup = (TextView) findViewById(R.id.txt_signup);

        setListener();
    }

    private void setListener() {
        btnLogin.setOnClickListener(new LoginButtonClickListner());
        txtSignup.setOnClickListener(new SignupClickListner());
    }

    private class LoginButtonClickListner implements View.OnClickListener {
        @Override
        public void onClick(View view) {
           /* Intent intent = new Intent(LoginActivity.this, StudentMenuActivity.class);
            startActivity(intent);*/
            loginUser();
        }
    }

    private void loginUser() {

        if (TextUtils.isEmpty(etEmailid.getText().toString().trim())) {
            Toast.makeText(this, getString(R.string.please_enter_your_emailid), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            Toast.makeText(this, getString(R.string.please_enter_your_password), Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();


        //authenticate user
        auth.signInWithEmailAndPassword(etEmailid.getText().toString().trim(), etPassword.getText().toString().trim())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i(TAG, "onComplete: "+task.getException());
                            Toast.makeText(LoginActivity.this, getString(R.string.invalid_login), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        } else {
                            prefs.edit().putBoolean("studentlogin", true).commit();
                            prefs.edit().putString(Constants.USER_NAME,task.getResult().getUser().getDisplayName()).commit();
                            Log.i(TAG, "onComplete: "+task.getResult().getUser().getUid());
                            saveUserData(task.getResult().getUser().getUid());
                        }
                    }
                });
    }

    private void saveUserData(final String userId){
        mDatabaseReference= FirebaseDatabase.getInstance().getReference(Constants.REGISTER_USER_PATH);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // mDialog.dismiss();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    SignupModel model=snapshot.getValue(SignupModel.class);

                    if(userId.equalsIgnoreCase(model.getUserid())){
                        Gson gson = new Gson();
                        String json = gson.toJson(model);
                        prefs.edit().putString(Constants.USER_MODEL,json).commit();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }


                  //  mUploadedDocList.add(model);
                }
             //   mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private class SignupClickListner implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LoginActivity.this, UserSignupActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        etEmailid.setText("test@gmail.com");
//        etPassword.setText("123456");
    }
}
