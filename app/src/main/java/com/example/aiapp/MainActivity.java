package com.example.aiapp;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiapp.APIs.AIManager;
import com.example.aiapp.Adapters.ChatAdapter;
import com.example.aiapp.Models.ChatMessage;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AIManager.AIResponseListener{

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private TextInputEditText editText;
    private ImageButton imageButton;
    private GenerativeModelFutures model;
    private List<Content> history;
    private AIManager aiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE );
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_view_chat);
        editText = findViewById(R.id.edit_text_message);
        imageButton = findViewById(R.id.button_send);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Initialize model and history
        String apiKey = "API_KEY";
        history = new ArrayList<>();

        aiManager = new AIManager(apiKey, history, this);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    sendMessage(message);
                    editText.setText("");
                }
            }
        });

        final View rootView = findViewById(R.id.main);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if(keypadHeight > screenHeight * 0.15) {
                    recyclerView.scrollToPosition(chatMessages.size() - 1);
                }
            }
        });
    }




    private void sendMessage(String message) {
        // Add user's message
        chatMessages.add(new ChatMessage(message, getCurrentTimestamp(), true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);


        // Add message to history
        Content userContent = new Content.Builder()

                .addText(message)
                .build();
        history.add(userContent);

        // Get AI response
        aiManager.sendMessageToAI(userContent);
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }

    public void onAIResponse(String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatMessages.add(new ChatMessage(response,getCurrentTimestamp(),false));
                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                recyclerView.scrollToPosition(chatMessages.size() - 1);

                Content aiContent = new Content.Builder()
                        .addText(response)
                        .build();
                history.add(aiContent);
            }
        });
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(TAG, "onError: Error Getting Ai response", throwable );
    }
}
