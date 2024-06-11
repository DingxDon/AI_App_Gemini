package com.example.aiapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

public class ChatMessage implements Parcelable {
    @PropertyName("text_message")
    private String textMessage;

    @PropertyName("text_timestamp")
    private String textTimestamp;

    @PropertyName("is_sent_by_user")
    private boolean isSentByUser;

    private String documentId; // Add document ID field

    // Default constructor required for Firestore
    public ChatMessage() {
    }

    public ChatMessage(String textMessage, String textTimestamp, boolean isSentByUser) {
        this.textMessage = textMessage;
        this.textTimestamp = textTimestamp;
        this.isSentByUser = isSentByUser;
    }

    protected ChatMessage(Parcel in) {
        textMessage = in.readString();
        textTimestamp = in.readString();
        isSentByUser = in.readByte() != 0;
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    @PropertyName("text_message")
    public String getTextMessage() {
        return textMessage;
    }

    @PropertyName("text_message")
    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    @PropertyName("text_timestamp")
    public String getTextTimestamp() {
        return textTimestamp;
    }

    @PropertyName("text_timestamp")
    public void setTextTimestamp(String textTimestamp) {
        this.textTimestamp = textTimestamp;
    }

    @PropertyName("is_sent_by_user")
    public boolean isSentByUser() {
        return isSentByUser;
    }

    @PropertyName("is_sent_by_user")
    public void setSentByUser(boolean sentByUser) {
        isSentByUser = sentByUser;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // Convert ChatMessage object to a Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("text_message", textMessage);
        map.put("text_timestamp", textTimestamp);
        map.put("is_sent_by_user", isSentByUser);
        return map;
    }

    // Convert Map to ChatMessage object
    public static ChatMessage fromMap(Map<String, Object> map) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setTextMessage((String) map.get("text_message"));
        chatMessage.setTextTimestamp((String) map.get("text_timestamp"));
        chatMessage.setSentByUser((boolean) map.get("is_sent_by_user"));
        return chatMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(textMessage);
        dest.writeString(textTimestamp);
        dest.writeByte((byte) (isSentByUser ? 1 : 0));
    }
}
