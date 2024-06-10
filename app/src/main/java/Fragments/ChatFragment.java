package Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiapp.APIs.AIManager;
import com.example.aiapp.APIs.ApiKeyManager;
import com.example.aiapp.Adapters.ChatAdapter;
import com.example.aiapp.MainActivity;
import com.example.aiapp.Models.ChatMessage;
import com.example.aiapp.R;
import com.google.ai.client.generativeai.type.Content;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatFragment extends Fragment implements AIManager.AIResponseListener {

    private static final String TAG = "ChatFragment";
    private static final String KEY_CHAT_MESSAGES = "chat_messages";
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private TextInputEditText editText;
    private ImageButton sendChatToAIBtn;
    private AIManager aiManager;
    private List<Content> history;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_chat);
        editText = view.findViewById(R.id.edit_text_message);
        sendChatToAIBtn = view.findViewById(R.id.button_send);

        if (savedInstanceState != null) {
            chatMessages = savedInstanceState.getParcelableArrayList(KEY_CHAT_MESSAGES);
        } else {
            chatMessages = new ArrayList<>();
        }

        chatAdapter = new ChatAdapter(chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(chatAdapter);

        // Initialize AIManager and history
        String apiKey = ApiKeyManager.getApiKey(requireContext());
        history = new ArrayList<>();
        aiManager = new AIManager(apiKey, history, this);

        // Set click listener for send button
        sendChatToAIBtn.setOnClickListener(v -> sendMessage());

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(KEY_CHAT_MESSAGES, new ArrayList<>(chatMessages));
        super.onSaveInstanceState(outState);
    }

    private void sendMessage() {
        String message = editText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getActivity(), "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        editText.setText("");

        chatMessages.add(new ChatMessage(message, getCurrentTimestamp(), true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);

        Content userContent = new Content.Builder().addText(message).build();
        history.add(userContent);

        aiManager.sendMessageToAI(userContent);

        // Hide keyboard after sending the message
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onAIResponse(String response) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            chatMessages.add(new ChatMessage(response, getCurrentTimestamp(), false));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            recyclerView.scrollToPosition(chatMessages.size() - 1);

            Content aiContent = new Content.Builder().addText(response).build();
            history.add(aiContent);
        });
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(TAG, "Error Getting AI response", throwable);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).updateNavBarVisibility(true); // For ChatFragment
    }
}
