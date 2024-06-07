package com.example.aiapp.Models;

public class ChatMessage {

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

}
