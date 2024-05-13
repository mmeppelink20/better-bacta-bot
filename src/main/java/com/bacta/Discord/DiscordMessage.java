package com.bacta.Discord;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;


public class DiscordMessage {
    private String userName;
    private String message;
    private String messageTime;
    private Guild guild;
    private Channel channel;

    public final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss");

    public DiscordMessage(String message) {
        this.message = message;
    }

    public DiscordMessage(String userName, String message, Instant messageTime) {
        // Convert Instant to ZonedDateTime using a specific time zone
        ZonedDateTime zonedDateTime = messageTime.atZone(ZoneId.systemDefault());
        this.userName = userName;
        this.message = message;
        this.messageTime = formatter.format(zonedDateTime);
    }

    public DiscordMessage(String userName, String message, Instant messageTime, Guild guild, Channel channel) {
        // Convert Instant to ZonedDateTime using a specific time zone
        ZonedDateTime zonedDateTime = messageTime.atZone(ZoneId.systemDefault());
        
        // Format ZonedDateTime using formatter
        this.userName = userName;
        this.message = message;
        this.messageTime = formatter.format(zonedDateTime);
        this.guild = guild;
        this.channel = channel;
    }

    public Guild getGuild() {
        return guild;
    }

    public Channel getChannel() {
        return channel;
    }
    
    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public String toString() {
        return messageTime + ": " + userName + ": " + message;
    }

    public static String formatTime(Instant instant) {
        // Convert Instant to ZonedDateTime using a specific time zone
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        
        // Format ZonedDateTime using formatter
        return formatter.format(zonedDateTime);
    }

}
