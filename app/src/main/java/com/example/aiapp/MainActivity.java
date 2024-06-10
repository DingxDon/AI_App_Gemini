package com.example.aiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiapp.APIs.AIManager;
import com.example.aiapp.Accounts.Fragments.LoginFragment;
import com.example.aiapp.Adapters.ChatAdapter;
import com.example.aiapp.Models.ChatMessage;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import Fragments.AboutFragment;
import Fragments.ChatFragment;
import Fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private TextInputEditText editText;
    private ImageButton startNewChat, CurrentChatMenuBtn, HamburgerSideBtn;
    private AIManager aiManager;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;

    private Button testFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyThemeFromPreference();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        mAuth = FirebaseAuth.getInstance();
        /* Check if the user is already logged in */

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not logged in, show LoginFragment
            replaceFragment(new LoginFragment(), true);
        } else {
            // User is logged in, show ChatFragment
            replaceFragment(new ChatFragment(), false);
        }

        // Set up the navigation drawer and its functionality
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                replaceFragment(new ChatFragment(), false);
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.menu_settings) {
                replaceFragment(new SettingsFragment(), false);
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.menu_about) {
                replaceFragment(new AboutFragment(), false);
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.menu_logout) {
                logout();
                return true;
            }
            return false;
        });



        // Initialize the Views and Other stuff
        recyclerView = findViewById(R.id.recycler_view_chat);
        editText = findViewById(R.id.edit_text_message);
        startNewChat = findViewById(R.id.Start_new_chat_ib);
        CurrentChatMenuBtn = findViewById(R.id.dropdown_menu_ib);
        HamburgerSideBtn = findViewById(R.id.side_menu_ib);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);


        // Starts a new Chat
        startNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatFragment chatFragment = new ChatFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, chatFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Makes the sideMenu appear without sliding the drawer
        HamburgerSideBtn.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        CurrentChatMenuBtn.setOnClickListener(this::showPopupMenu);

        // Set menu_home as selected by default and show ChatFragment
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.menu_home);
            replaceFragment(new ChatFragment(), false);
        }

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ChatFragment())
                    .commit();
        }

        // Check if the user is already logged in
        if (mAuth != null) {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser == null) {
                // User is not logged in, show LoginFragment
                replaceFragment(new LoginFragment(), false);
            } else {
                // User is logged in, show ChatFragment
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new ChatFragment())
                            .commit();
                }
            }
        }




    }

    private void applyThemeFromPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themeValue = sharedPreferences.getString("theme_preference", "system");
        switch (themeValue) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void recreateActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    // Updates the visibility of the NavBar
    public void updateNavBarVisibility(boolean isChatFragment) {
        TextView currentChatNameTextView = findViewById(R.id.Current_Chat_name_tv);
        ImageButton startNewChatImageButton = findViewById(R.id.Start_new_chat_ib);
        ImageButton dropdownMenuImageButton = findViewById(R.id.dropdown_menu_ib);

        if (isChatFragment) {
            currentChatNameTextView.setVisibility(View.VISIBLE);
            startNewChatImageButton.setVisibility(View.VISIBLE);
            dropdownMenuImageButton.setVisibility(View.VISIBLE);
        } else {
            currentChatNameTextView.setVisibility(View.GONE);
            startNewChatImageButton.setVisibility(View.GONE);
            dropdownMenuImageButton.setVisibility(View.GONE);
        }
    }



    // Only for testing new Fragments
    private void openTestFragment() {
        LoginFragment loginFragment = new LoginFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, loginFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
    // Replace the current fragment with the new one
    private void replaceFragment(Fragment fragment, boolean fullScreen) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        int containerId = fullScreen ? R.id.full_screen_container : R.id.container;

        // Hide the nav bar include if LoginFragment is being displayed
        if (fragment instanceof LoginFragment) {
            findViewById(R.id.nav_bar_include).setVisibility(View.GONE);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            findViewById(R.id.nav_bar_include).setVisibility(View.VISIBLE);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            findViewById(R.id.full_screen_container).setVisibility(View.GONE);
        }

        transaction.replace(containerId, fragment);
        transaction.commit();
    }


    // Deletes the current chat
    private void deleteCurrentChat() {
        chatMessages.clear();
        chatAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_theme) {
            // Handle theme selection menu item click
            Toast.makeText(this, "Theme Selection Clicked", Toast.LENGTH_SHORT).show();
            // You can open a dialog to let the user select the theme here
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.delete_btn) {
                deleteCurrentChat();
                Toast.makeText(MainActivity.this, "Deleted The Current Chat", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        replaceFragment(new LoginFragment(), true);
    }
}
