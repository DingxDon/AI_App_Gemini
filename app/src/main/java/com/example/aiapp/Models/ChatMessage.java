package com.example.aiapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ChatMessage implements Parcelable {

    protected ChatMessage(Parcel in) {
        text_message = in.readString();
        text_timestamp = in.readString();
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

    public String getText_message() {
        return text_message;
    }

    public void setText_message(String text_message) {
        this.text_message = text_message;
    }

    public String getText_timestamp() {
        return text_timestamp;
    }

    public void setText_timestamp(String text_timestamp) {
        this.text_timestamp = text_timestamp;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public void setSentByUser(boolean sentByUser) {
        isSentByUser = sentByUser;
    }

    public ChatMessage(String text_message, String text_timestamp, boolean isSentByUser) {
        this.text_message = text_message;
        this.text_timestamp = text_timestamp;
        this.isSentByUser = isSentByUser;
    }

    private String text_message, text_timestamp;
    private boolean isSentByUser;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(text_message);
        dest.writeString(text_timestamp);
        dest.writeByte((byte) (isSentByUser ? 1 : 0));
    }
}
