package com.example.aiapp.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiapp.Models.ChatMessage;
import com.example.aiapp.R;

import java.util.List;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.tables.TablePlugin;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    private List<ChatMessage> chatMessages;


    @NonNull
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {
        ChatMessage chatMessage= chatMessages.get(position);
        holder.bind(chatMessage);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        private TextView messageTextView, timeStampTextView, textFromWho;
        private Markwon markwon;
        ImageButton copyButton;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTextView = itemView.findViewById(R.id.text_message);
            timeStampTextView = itemView.findViewById(R.id.text_timestamp);
            copyButton = itemView.findViewById(R.id.copy_button);
            textFromWho = itemView.findViewById(R.id.text_chat_from_who);

            //Markdown Markwon
            //markwon = Markwon.create(itemView.getContext());

            markwon = Markwon.builder(itemView.getContext())
                    .usePlugin(TablePlugin.create(itemView.getContext())).build();

        }

        public void bind(ChatMessage chatMessage){
            markwon.setMarkdown(messageTextView, chatMessage.getTextMessage());
            //messageTextView.setText(chatMessage.getText_message());
            timeStampTextView.setText(chatMessage.getTextTimestamp());

            if(chatMessage.isSentByUser()) {
                textFromWho.setText("You");
                //textFromWho.setTextColor(itemView.getContext().getResources().getColor(R.color.SpotifyGreen));
            } else {
                textFromWho.setText("AI");
                //textFromWho.setTextColor(itemView.getContext().getResources().getColor(R.color.SpotifyGreen));

            }

            copyButton.setOnClickListener(v ->
            {
                Context context = itemView.getContext();
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copied Text", chatMessage.getTextMessage());
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(context, "Message copied to clipboard", Toast.LENGTH_SHORT).show();
            });

            if(chatMessage.isSentByUser()) {
                messageTextView.setBackgroundResource(R.drawable.bg_user_message);
            } else {
                messageTextView.setBackgroundResource(R.drawable.bg_user_message);
            }
        }
    }
}
