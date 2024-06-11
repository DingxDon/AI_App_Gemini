package com.example.aiapp.APIs;

import android.util.Log;

import com.example.aiapp.Models.ChatMessage;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

public class AIManager {

    // Declarations
    private static final String TAG = "AIManager";
    private GenerativeModelFutures modelFutures;
    private List<Content> history;
    private AIResponseListener listener;
    private FirebaseFirestore db;
    private CollectionReference chatCollection;


    // Interface for AiResponse
    public interface AIResponseListener {
        void onAIResponse(String response);
        void onError(Throwable throwable);
    }

    public interface ChatHistoryLoadListener {
        void onChatHistoryLoaded(List<ChatMessage> loadedChatMessages);
        void onError(Exception e);
    }

    // Constructor
    public AIManager(String apiKey, List<Content> history, AIResponseListener listener) {
        this.history = history;
        this.listener = listener;
        GenerativeModel generativeModel = new GenerativeModel("gemini-1.5-flash", apiKey);
        modelFutures = GenerativeModelFutures.from(generativeModel);
        db = FirebaseFirestore.getInstance();
        chatCollection = db.collection("chatMessages");
    }

    public void sendMessageToAI(Content userContent) {

        ChatFutures chatFutures = modelFutures.startChat(history);

        Publisher<GenerateContentResponse> streamingResponse = chatFutures.sendMessageStream(userContent);

        final String[] fullResponse = {""};

        streamingResponse.subscribe(new Subscriber<GenerateContentResponse>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
                Log.d(TAG, "onSubscribe: Started");
            }

            @Override
            public void onNext(GenerateContentResponse generateContentResponse) {
                String chunk = generateContentResponse.getText();
                fullResponse[0] += chunk;
                Log.d(TAG, "onNext: Received Chunk" + chunk);
            }


            @Override
            public void onError(Throwable t) {
                Log.d(TAG, "onError: Error While Getting response from the AI", t);
                listener.onError(t);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: Response Complete");
                listener.onAIResponse(fullResponse[0]);

                ChatMessage aiMessage = new ChatMessage(fullResponse[0],
                        String.valueOf(System.currentTimeMillis()), false);
                //saveMessageToFirestore(aiMessage);
            }
        });





    }

    public void saveMessageToFirestore(ChatMessage message) {
        chatCollection.add(message)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Message saved successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving message", e));
    }



    public void loadChatHistory(ChatHistoryLoadListener loadListener) {
        chatCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ChatMessage> loadedMessages = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ChatMessage message = document.toObject(ChatMessage.class);
                    loadedMessages.add(message);
                }
                loadListener.onChatHistoryLoaded(loadedMessages);
            } else {
                loadListener.onError(task.getException());
            }
        });
    }



}
