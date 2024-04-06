package com.meppelink.Discord;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class GuildMessageList  {

    private HashMap<String, HashSet<String>> guildChannelMap;
    private HashMap<String, Queue<DiscordMessage>> channelMessages;
    private HashMap<String, Integer> charCountPerChannel;
    

    // default constructor
    public GuildMessageList() {
        guildChannelMap = new HashMap<String, HashSet<String>>();
        channelMessages = new HashMap<String, Queue<DiscordMessage>>();
        charCountPerChannel = new HashMap<String, Integer>();
    }

    // parametized constructor
    public GuildMessageList(HashMap<String, HashSet<String>> guildChannelMap, HashMap<String, Queue<DiscordMessage>> channelMessages, HashMap<String, Integer> charCountPerChannel) {
        this.guildChannelMap = guildChannelMap;
        this.channelMessages = channelMessages;
        this.charCountPerChannel = charCountPerChannel;
    }

    // get the channel messages
    public Queue<DiscordMessage> getChannelMessages(String channelID) {
        return channelMessages.get(channelID);
    }

    // get the char count per channel
    public Integer getCharCountPerChannel(String channelID) {
        return charCountPerChannel.get(channelID);
    }

    // set the char count per channel
    public void setCharCountPerChannel(String channelID, Integer charCount) {
        charCountPerChannel.put(channelID, charCount);
    }

    // add characters to the char count
    public void addCharsToCharCount(String channelID, Integer charCount) {
        charCountPerChannel.put(channelID, charCountPerChannel.get(channelID) + charCount);
    }

    // add a message to the channel
    public void addMessageToChannel(String channelID, DiscordMessage message) {
        channelMessages.get(channelID).add(message);

        // print the queue; for debugging.
        // System.out.println("DEBUG:");
        // System.out.println("*** ADDED MESSAGE TO THE QUEUE ***: ");
        // channelMessages.get(channelID).forEach(s -> System.out.println(s));
    }

    // add a guild to the guild map
    public void addGuildToMap(String guildID) {
        guildChannelMap.put(guildID, new HashSet<String>());
    }

    // check if a guild is in the map
    public boolean guildInMap(String guildID) {
        return guildChannelMap.containsKey(guildID);
    }

    // add channel to a guild
    public void addChannelToGuild(String guildID, String channelID) {
        guildChannelMap.get(guildID).add(channelID);
    }

    // check if a channel is in a guild
    public boolean channelInGuild(String guildID, String channelID) {
        return guildChannelMap.get(guildID).contains(channelID);
    }

    // get guild channel map
    public HashMap<String, HashSet<String>> getGuildChannelMap() {
        return guildChannelMap;
    }

    public void clearMessages(@NotNull SlashCommandInteractionEvent event) {
        String channelID = event.getChannel().getId();
        channelMessages.get(channelID).clear();
        charCountPerChannel.put(channelID, 0);
    }

    // get the messages in a channel
    public Queue<DiscordMessage> getMessagesInChannel(String channelID) {
        return channelMessages.get(channelID);
    }

    // remove a message from a channel
    public void removeMessageFromChannel(MessageReceivedEvent event) {
        String channelID = event.getChannel().getId();
        channelMessages.get(channelID).remove();
    }

    // add a channel to the channel map
    public void addChannelToMap(String channelID) {
        channelMessages.put(channelID, new LinkedList<DiscordMessage>());
    }



}
