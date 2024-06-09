package com.example.aiapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiapp.APIs.AIManager;
import com.example.aiapp.Adapters.ChatAdapter;
import com.example.aiapp.Models.ChatMessage;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                replaceFragment(new ChatFragment());
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.menu_settings) {
                replaceFragment(new SettingsFragment());
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.menu_about) {
                Toast.makeText(MainActivity.this, "About selected", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                return true;
            }
            return false;
        });




        recyclerView = findViewById(R.id.recycler_view_chat);
        editText = findViewById(R.id.edit_text_message);
        startNewChat = findViewById(R.id.Start_new_chat_ib);
        CurrentChatMenuBtn = findViewById(R.id.dropdown_menu_ib);
        HamburgerSideBtn = findViewById(R.id.side_menu_ib);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);


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

        HamburgerSideBtn.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        CurrentChatMenuBtn.setOnClickListener(this::showPopupMenu);

        // Set menu_home as selected by default and show ChatFragment
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.menu_home);
            replaceFragment(new ChatFragment());
        }

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ChatFragment())
                    .commit();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }


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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
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
}
