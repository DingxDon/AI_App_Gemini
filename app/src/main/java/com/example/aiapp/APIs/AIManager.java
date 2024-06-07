package com.example.aiapp.APIs;

import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

public class AIManager {

    // Declarations
    private static final String TAG = "AIManager";
    private GenerativeModelFutures modelFutures;
    private List<Content> history;
    private AIResponseListener listener;

    // Interface for AiResponse
    public interface AIResponseListener {
        void onAIResponse(String response);
        void onError(Throwable throwable);
    }

    // Constructor
    public AIManager(String apiKey, List<Content> history, AIResponseListener listener) {
        this.history = history;
        this.listener = listener;
        GenerativeModel generativeModel = new GenerativeModel("gemini-1.5-flash", apiKey);
        modelFutures = GenerativeModelFutures.from(generativeModel);

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
            }
        });





    }

}
