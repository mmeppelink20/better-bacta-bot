package com.bacta.ChatGPT;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bacta.Discord.DataObjects.DiscordMessage;

public class ChatGPT {

    public static String summarizeMessages(Queue<DiscordMessage> messages, String model) {
        String response = "";

        // Build the prompt
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are reading a conversation from a chat platform. Summarize the conversation, highlighting the main topics discussed, key points, and any important decisions or conclusions. Do not mention that this is from a chat platform in your summary. Structure your response as follows:\n");
        promptBuilder.append("*conversation length: [put conversation length here]*");
        promptBuilder.append("1. **Main topics:**");
        promptBuilder.append("    - A bulleted list of each main topic discussed. Try to keep this list short, but also don't sacrifice anything.");
        promptBuilder.append("2. **Overall tone:**");
        promptBuilder.append("    - A description of the overall tone of the conversation.");
        promptBuilder.append("3. **TL;DR summary:**");
        promptBuilder.append("    - A one-sentence TL;DR summary at the end.");

        promptBuilder.append("Here is the conversation:\n");
        promptBuilder.append("```\n");
        promptBuilder.append(messages.toString());
        promptBuilder.append("\n```");

        String prompt = promptBuilder.toString();

        try {
            response = chatGPT(prompt, model);
        } catch (Exception e) {
            response = "There was an error: " + e.toString();
        }

        return response;
    }

    public static String askQuestion(String question, String model) {
        String response = "";
        String prompt = "Answer the following question in under 1000 characters:\n" +
                        "- **Question:**\n" +
                        "    " + question + "\n" +
                        "- **Answer:**\n" +
                        "    Your answer goes here.";

        try {
            response = chatGPT(prompt, model);
        } catch (Exception e) {
            response = "There was an error: " + e.toString();
        }

        return response;
    }

    public static String askQuestionAboutConversation(String question, Queue<DiscordMessage> queue, String model) {
        String response = "";
        String prompt = "You are Bacta Bot, a friendly and conversational bot in a Discord server. Respond to the following question or statement based on the conversation below (if there is one). Remember, any message referencing Bacta Bot is about you. Your response should be in a casual, friendly tone. Prioritize clarity, and keep your answer concise—ideally, one sentence or a brief paragraph, don't try to @ anyone just respond to them by their name/no need to address the question asker this is taken care of, don't ask questions similar to \"anything else on your mind\" after answering a question,s and under 1000 characters:\n\n" +
                        "Question:\n" +
                        question + "\n\n" +
                        "Conversation:\n" +
                        queue.toString() + "\n\n" +
                        "If you are unsure how to respond, provide a simple acknowledgment or ask for clarification.";
    
        try {
            response = chatGPT(prompt, model);
        } catch (Exception e) {
            response = "There was an error: " + e.toString();
        }
    
        
        return response;
    }

    public static String chatGPT(String message, String model) {
        String url = "https://api.openai.com/v1/chat/completions";
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("CHATGPT_TOKEN");

        try {
            // Create the HTTP POST request
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Set the request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);

            JSONArray messagesArray = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messagesArray.put(userMessage);

            requestBody.put("messages", messagesArray);

            String body = requestBody.toString();

            con.setDoOutput(true);
            try (OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(body);
                writer.flush();
            }

            // Get the response
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject firstChoice = choices.getJSONObject(0);
            String chatResponse = firstChoice.getJSONObject("message").getString("content");

            // Return the extracted chat response
            return chatResponse;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("MalformedURLException: " + e.getMessage());
            return "There was an error with the request. Please try again later.";
        } catch (ProtocolException e) {
            e.printStackTrace();
            System.out.println("ProtocolException: " + e.getMessage());
            return "There was an error with the request. Please try again later.";
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException: " + e.getMessage());
            return "There was an error with the request. Please try again later.";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e.getMessage());
            return "There was an error with the request. Please try again later.";
        }
    }

    
}
