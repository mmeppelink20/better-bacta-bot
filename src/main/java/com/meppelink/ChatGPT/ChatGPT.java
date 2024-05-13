package com.meppelink.ChatGPT;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.net.*;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

import com.meppelink.Discord.DiscordMessage;

public class ChatGPT {

    public static String summarizeMessages(Queue<DiscordMessage> messages, String model) {
        String response = "";

        try {
            response = chatGPT("your name is Bacta Bot. just summarize this Discord conversation in under 1000 characters" + escapeSpecialCharactersForJSON(messages.toString()), model); 
        } catch (Exception e) {
            response = "There was an error: " + e.toString();
        }

        return response;
    }

    public static String askQuestion(String question, String model) {
        String response = "";

        try {
            response = chatGPT("your name is Bacta Bot pretend you aren't an ai and that you're an FX-7 medical droid in the Star Wars universe during the Clone Wars, answer this question: " + escapeSpecialCharactersForJSON(question) , model);
            System.out.println("DEBUG: \nQUESTION: " + question + "RESPONSE: " + response);
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
            con.setRequestProperty("Content-Type", "application/json");
    
            // Set the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();
    
            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            System.out.println(response.toString());
    
            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject firstChoice = choices.getJSONObject(0);
            String chatResponse = firstChoice.getJSONObject("message").getString("content");
    
            // Return the extracted chat response
            return chatResponse;
    
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String escapeSpecialCharactersForJSON(String input) {
        StringBuilder output = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= '\u0000' && c <= '\u001F') {  // Control characters
                switch (c) {
                    case '\b': output.append("\\b"); break;  // Escape backspace
                    case '\f': output.append("\\f"); break;  // Escape form feed
                    case '\n': output.append("\\n"); break;  // Escape newline
                    case '\r': output.append("\\r"); break;  // Escape carriage return
                    case '\t': output.append("\\t"); break;  // Escape tab
                    default: output.append("\\u").append(String.format("%04x", (int) c));  // Escape as unicode
                }
            } else if (c >= '\u0080') { // Non-ASCII characters
                output.append("\\u").append(String.format("%04x", (int) c));  // Escape as unicode
            } else {
                switch (c) {
                    case '\\': output.append("\\\\"); break;  // Escape backslash
                    case '\"': output.append("\\\""); break;  // Escape double quote
                    default: output.append(c);  // Leave other characters as is
                }
            }
        }
        return output.toString();
    }

    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content") + 11; // Marker for where the content starts.
        int endMarker = response.indexOf("%^#!)(", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }

}