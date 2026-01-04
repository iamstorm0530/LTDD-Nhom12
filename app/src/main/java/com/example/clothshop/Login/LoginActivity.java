package com.example.clothshop.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.clothshop.Home.HomeActivity;
import com.example.clothshop.R;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import androidx.credentials.CredentialManagerCallback;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin, btnGoogle;
    TextView tvSignUp;

    private CredentialManager credentialManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView imgWave = findViewById(R.id.imgWave);
        imgWave.bringToFront();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvSignUp = findViewById(R.id.tvSignUp);

        credentialManager = CredentialManager.create(this);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // ✅ Email/Password login
        btnLogin.setOnClickListener(v -> signInWithEmailPassword());

        // ✅ Google login
        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, com.example.clothshop.Register.RegisterActivity.class))
        );
    }

    private void signInWithEmailPassword() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Nhập email và password", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this,
                                "Login failed: " + (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) return;

                    // ✅ chặn chưa verify email
                    if (!user.isEmailVerified()) {
                        Toast.makeText(this, "Email chưa verify. Vui lòng verify trước.", Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        return;
                    }

                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                });
    }

    private void signInWithGoogle() {
        // Nếu bạn đã có default_web_client_id thì cứ dùng cái này
        String serverClientId = getString(R.string.default_web_client_id);

        GetSignInWithGoogleOption googleOption =
                new GetSignInWithGoogleOption.Builder(serverClientId).build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build();

        CancellationSignal cancellationSignal = new CancellationSignal();

        credentialManager.getCredentialAsync(
                this,
                request,
                cancellationSignal,
                ContextCompat.getMainExecutor(this),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleGoogleResult(result);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        handleGoogleError(e);
                    }
                }
        );
    }

    private void handleGoogleResult(GetCredentialResponse result) {
        Credential credential = result.getCredential();

        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;

            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())
                    || GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_SIWG_CREDENTIAL.equals(customCredential.getType())) {

                GoogleIdTokenCredential googleCred =
                        GoogleIdTokenCredential.createFrom(customCredential.getData());

                String idToken = googleCred.getIdToken();

                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);

                firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this, task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(this,
                                        "Firebase auth failed: " + (task.getException()!=null?task.getException().getMessage():""),
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user == null) return;

                            // ✅ nếu chưa có profile Firestore thì tạo
                            ensureUserProfileForGoogle(user);

                            startActivity(new Intent(this, HomeActivity.class));
                            finish();
                        });

                return;
            }
        }

        Toast.makeText(this, "Google credential không hợp lệ", Toast.LENGTH_SHORT).show();
    }

    private void ensureUserProfileForGoogle(FirebaseUser user) {
        String uid = user.getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) return;

                    Map<String, Object> profile = new HashMap<>();
                    profile.put("id", uid);
                    profile.put("name", user.getDisplayName() != null ? user.getDisplayName() : "User");
                    profile.put("email", user.getEmail());
                    profile.put("gender", "other");
                    profile.put("avatar", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "default");
                    profile.put("role", "user");
                    profile.put("status", "active");

                    db.collection("users").document(uid).set(profile);
                });
    }

    private void handleGoogleError(GetCredentialException e) {
        if (e instanceof GetCredentialCancellationException) return;

        if (e instanceof NoCredentialException) {
            Toast.makeText(this, "Không có tài khoản Google khả dụng để đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Google sign-in lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
