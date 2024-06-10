package com.example.gochat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gochat.fragment.AccountFragment;
import com.example.gochat.fragment.FindUserFragment;
import com.example.gochat.fragment.MainPageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainPage extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView bnvView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        loadFragment(new MainPageFragment());
        bnvView = findViewById(R.id.bottomNavigationView);
        bnvView.setOnNavigationItemSelectedListener(this);

    } //end of main body bracket

    public boolean loadFragment(Fragment fragment) {

        if (fragment!=null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout,fragment)
                    .commit();
        }

        return true;

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.messagesId) {
            fragment = new MainPageFragment();
        } else if (itemId == R.id.contactsId) {
            fragment = new FindUserFragment();
        } else {
            fragment = new AccountFragment();
        }

        return loadFragment(fragment);

    }
} //end bracket