package kz.iitu.cloudy.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import kz.iitu.cloudy.ui.fragment.AddPhotosFragment;
import kz.iitu.cloudy.ui.fragment.HashtagsFragment;
import kz.iitu.cloudy.ui.fragment.OrdersFragment;
import kz.iitu.cloudy.utils.PreferenceUtils;
import kz.iitu.cloudy.R;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_hashtags:
                    fragment = HashtagsFragment.getInstance();
                    changeTitle(R.string.title_home);
                    switchFragment(fragment);
                    return true;
                case R.id.navigation_add:
                    fragment = AddPhotosFragment.getInstance();
                    switchFragment(fragment);
                    changeTitle(R.string.title_dashboard);
                    return true;
                case R.id.navigation_requests:
                    fragment = OrdersFragment.getInstance();
                    changeTitle(R.string.title_notifications);
                    switchFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    public static void start(Context context) {
        Intent starter = new Intent(context, HomeActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        changeTitle(R.string.title_home);
        switchFragment(HashtagsFragment.getInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                PreferenceUtils.setCurrentUser(this, null);
                MainActivity.start(this);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);

        fragmentTransaction.commit();
    }

    private void changeTitle(int title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void goHome() {
        changeTitle(R.string.title_home);
        switchFragment(HashtagsFragment.getInstance());
    }
}
