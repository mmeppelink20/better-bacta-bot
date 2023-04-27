package com.meppelink.Discord;

public class DiscordMessage {
    private String userName;
    private String userId;
    private String serverName;
    private String serverId;
    private String channelName;
    private String channelId;
    private String message;
    private String messageId;
    private String messageTime;
    private String messageLink;

    public DiscordMessage(String userName, String userId, String serverName, String serverId, String channelName, String channelId, String message, String messageId, String messageTime, String messageLink) {
        this.userName = userName;
        this.userId = userId;
        this.serverName = serverName;
        this.serverId = serverId;
        this.channelName = channelName;
        this.channelId = channelId;
        this.message = message;
        this.messageId = messageId;
        this.messageTime = messageTime;
        this.messageLink = messageLink;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerId() {
        return serverId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public String getMessageLink() {
        return messageLink;
    }
}
