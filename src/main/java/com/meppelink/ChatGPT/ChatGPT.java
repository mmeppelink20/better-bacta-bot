package com.meppelink.ChatGPT;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.net.*;
import java.util.Queue;

import com.meppelink.Discord.DiscordMessage;

public class ChatGPT {

    public static String summarizeMessages(Queue<DiscordMessage> messages) {
        String error = "";
        try {
            return chatGPT(escapeSpecialCharactersForJSON(messages.toString() +
             "just summarize this conversation in under 1000 characters. end your response with %^#!)(")); //             "can you summarize this conversation in under 1000 characters, also do your best to identify and seperate seperate conversations? end your response with %^#!)("));

        } catch (Exception e) {
            error = e.toString();
        }
        return "Error:" + error;
    }

    public static String chatGPT(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("CHATGPT_TOKEN");
        String model = "gpt-4";

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

            // Return the extracted contents of the response.
            return extractContentFromResponse(response.toString());

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