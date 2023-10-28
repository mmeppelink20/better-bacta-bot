package com.meppelink.Discord;

import java.time.Instant;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;

public class DiscordMessage {
    private String userName;
    private String message;
    private Instant messageTime;
    private Guild guild;
    private Channel channel;


    public DiscordMessage(String userName, String message, Instant messageTime, Guild guild, Channel channel) {
        this.userName = userName;
        this.message = message;
        this.messageTime = messageTime;
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

    public Instant getMessageTime() {
        return messageTime;
    }

    public String toString() {
        return userName + ": " + message;
    }

}
