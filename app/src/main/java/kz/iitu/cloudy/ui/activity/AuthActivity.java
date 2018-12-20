package kz.iitu.cloudy.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import kz.iitu.cloudy.utils.PreferenceUtils;
import kz.iitu.cloudy.R;
import kz.iitu.cloudy.model.User;
import kz.iitu.cloudy.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MODE_LOGIN = 0;
    private static final int MODE_SIGNUP = 1;

    private static final String COLLECTION_USERS = "users";

    private ActivityAuthBinding mActivityAuthBinding;
    private FirebaseFirestore mFirestore;

    private int mCurrentMode = MODE_LOGIN;

    public static void start(Context context) {
        Intent starter = new Intent(context, AuthActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAuthBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_auth);

        mFirestore = FirebaseFirestore.getInstance();

        mActivityAuthBinding.loginButton.setOnClickListener(this);
        mActivityAuthBinding.signUp.setOnClickListener(this);
        mActivityAuthBinding.backIcon.setOnClickListener(this);
    }

    private void validateAndLogin() {

        String username = mActivityAuthBinding.usernameInput.getText().toString();
        String password = mActivityAuthBinding.passwordInput.getText().toString();

        final User user = new User(username, password);

        DocumentReference reference = mFirestore.collection(COLLECTION_USERS).document(user.getUsername());

        mActivityAuthBinding.shimmerLayout.startShimmerAnimation();

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mActivityAuthBinding.shimmerLayout.stopShimmerAnimation();

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User fetchedUser = document.toObject(User.class);

                        if (fetchedUser.getPassword().equals(user.getPassword())) {
                            Toast.makeText(AuthActivity.this, "Успешно", Toast.LENGTH_SHORT).show();
                            PreferenceUtils.setCurrentUser(AuthActivity.this, user);
                            HomeActivity.start(AuthActivity.this);
                        } else {
                            Toast.makeText(AuthActivity.this, "Неправильный пароль", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AuthActivity.this, "Такого пользователя не существует", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void signUp() {
        mActivityAuthBinding.shimmerLayout.startShimmerAnimation();

        final String username = mActivityAuthBinding.usernameInput.getText().toString();
        final String password = mActivityAuthBinding.passwordInput.getText().toString();

        final User user = new User(username, password);

        DocumentReference reference = mFirestore.collection(COLLECTION_USERS).document(user.getUsername());

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mActivityAuthBinding.shimmerLayout.stopShimmerAnimation();
                        Toast.makeText(AuthActivity.this, "Такой пользователь уже существует", Toast.LENGTH_SHORT).show();
                    } else {
                        mFirestore.collection(COLLECTION_USERS).document(username).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mActivityAuthBinding.shimmerLayout.stopShimmerAnimation();

                                Toast.makeText(AuthActivity.this, "Регистрация произошла успешно", Toast.LENGTH_SHORT).show();
                                switchMode();
                            }
                        });
                    }
                }
            }
        });
    }

    private void switchMode() {
        mCurrentMode = mCurrentMode == MODE_LOGIN ? MODE_SIGNUP : MODE_LOGIN;

        String title = mCurrentMode == MODE_LOGIN ? "Вход" : "Регистрация";
        String buttonTitle = mCurrentMode == MODE_LOGIN ? "Войти" : "Зарегистрироваться";
        String switcherTitle = mCurrentMode == MODE_LOGIN ? "У Вас нет аккаунта?" : "У меня есть аккаунт";

        mActivityAuthBinding.titleView.setText(title);
        mActivityAuthBinding.loginButton.setText(buttonTitle);
        mActivityAuthBinding.signUp.setText(switcherTitle);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_icon:
                finish();
                break;
            case R.id.login_button:
                if (mCurrentMode == MODE_LOGIN) {
                    validateAndLogin();
                } else {
                    signUp();
                }
                break;
            case R.id.sign_up:
                switchMode();
        }
    }
}
