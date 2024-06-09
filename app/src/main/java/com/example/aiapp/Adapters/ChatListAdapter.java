package com.example.aiapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiapp.R;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    List<String> chatlist;
    private OnChatItemClickListener  listener;

    public ChatListAdapter(List<String> chatlist, OnChatItemClickListener listener) {
        this.chatlist = chatlist;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ViewHolder holder, int position) {
        String chatList = chatlist.get(position);
        holder.Chat_Name.setText(chatList);
    }

    @Override
    public int getItemCount() {
        return chatlist.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{

        TextView Chat_Name;
        ImageButton Delete_chat;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Chat_Name = itemView.findViewById(R.id.text_view_chat_name);
            Delete_chat = itemView.findViewById(R.id.button_delete_chat);

            Delete_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}
