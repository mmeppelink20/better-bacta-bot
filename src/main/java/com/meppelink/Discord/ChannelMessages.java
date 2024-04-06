package com.meppelink.Discord;

import java.util.LinkedList;
import java.util.Queue;

public class ChannelMessages {
    private Queue<DiscordMessage> channelMessages;
    private Integer charCount;

    // default constructor
    public ChannelMessages() {
        this.channelMessages = new LinkedList<DiscordMessage>();
        this.charCount = 0;
    }

    // parametized constructor
    public ChannelMessages(Queue<DiscordMessage> channelMessages, Integer charCount) {
        this.channelMessages = channelMessages;
        this.charCount = charCount;
    }

    // add a message to the channel
    public void addMessageToChannel(DiscordMessage message) {
        channelMessages.add(message);
        // add the message's length to the char count
        System.out.println(message.toString());
        addCharsToCharCount(message.toString().length());
    }

    // remove a message from the message queue
    public void removeMessageFromChannel() {
        Integer messageLength = channelMessages.peek().toString().length();
        channelMessages.remove();
        
        // remove the message's length from the char count
        removeCharsFromCharCount(messageLength);
    }

    public void removeAllMessages() {
        channelMessages.clear();
        charCount = 0;
    }

    // print the queue; for debugging.
    public void printQueue() {
        System.out.println("DEBUG:");
        for (DiscordMessage message : channelMessages) {
            System.out.println(message);
        }
        System.out.println(charCount);
        System.out.println("END DEBUG");
    }
    
    // get the channel messages
    public Queue<DiscordMessage> getChannelMessages() {
        return channelMessages;
    }

    // get the char count
    public Integer getCharCount() {
        return charCount;
    }

    // set the char count
    public void setCharCount(Integer charCount) {
        this.charCount = charCount;
    }

    // add characters to the char count
    public void addCharsToCharCount(Integer charCount) {
        this.charCount += charCount;
    }

    // remove characters from the char count
    public void removeCharsFromCharCount(Integer charCount) {
        this.charCount -= charCount;
    }

    

}