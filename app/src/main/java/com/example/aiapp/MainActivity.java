package com.example.aiapp;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiapp.APIs.AIManager;
import com.example.aiapp.Adapters.ChatAdapter;
import com.example.aiapp.Models.ChatMessage;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AIManager.AIResponseListener {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private TextInputEditText editText;
    private ImageButton sendChatToAIBtn, CurrentChatMenuBtn, HamburgerSideBtn;
    private GenerativeModelFutures model;
    private List<Content> history;
    private AIManager aiManager;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView = findViewById(R.id.recycler_view_chat);
        editText = findViewById(R.id.edit_text_message);
        sendChatToAIBtn = findViewById(R.id.button_send);
        CurrentChatMenuBtn = findViewById(R.id.dropdown_menu_ib);
        HamburgerSideBtn = findViewById(R.id.side_menu_ib);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);











        // Initialize model and history
        String apiKey = "APIKEY";
        history = new ArrayList<>();
        aiManager = new AIManager(apiKey, history, this);

        // Initialize the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Set up ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Handle Navigation item selections
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.settings_menu_icon) {// Handle Home navigation
                Toast.makeText(MainActivity.this, "Home selected", Toast.LENGTH_SHORT).show();

                // Handle more items as needed
            }
            drawerLayout.closeDrawers();
            return true;
        });

        HamburgerSideBtn.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        // Send Chat to AI
        sendChatToAIBtn.setOnClickListener(v -> {
            String message = editText.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(MainActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessage(message);
            editText.setText("");

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        });

        // Current Chat Menu
        CurrentChatMenuBtn.setOnClickListener(this::showPopupMenu);

        // Handle keyboard visibility and RecyclerView scrolling
        final View rootView = drawerLayout;
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;
            if (keypadHeight > screenHeight * 0.15) {
                recyclerView.scrollToPosition(chatMessages.size() - 1);
            }
        });
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
        int id = item.getItemId();
        if (id == R.id.menu_settings) {

            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true; // Consume the event
        }
        return super.onOptionsItemSelected(item);
    }







    private void sendMessage(String message) {
        chatMessages.add(new ChatMessage(message, getCurrentTimestamp(), true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);

        Content userContent = new Content.Builder().addText(message).build();
        history.add(userContent);

        aiManager.sendMessageToAI(userContent);
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onAIResponse(String response) {
        runOnUiThread(() -> {
            chatMessages.add(new ChatMessage(response, getCurrentTimestamp(), false));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            recyclerView.scrollToPosition(chatMessages.size() - 1);

            Content aiContent = new Content.Builder().addText(response).build();
            history.add(aiContent);
        });
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(TAG, "onError: Error Getting Ai response", throwable);
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
