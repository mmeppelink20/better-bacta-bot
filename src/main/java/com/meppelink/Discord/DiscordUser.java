package com.meppelink.Discord;

public class DiscordUser {

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserDiscriminator() {
        return userDiscriminator;
    }

    public void setUserDiscriminator(String userDiscriminator) {
        this.userDiscriminator = userDiscriminator;
    }

    public String getUserMention() {
        return userMention;
    }

    public void setUserMention(String userMention) {
        this.userMention = userMention;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    private String userName;
    private String userID;
    private String userAvatar;
    private String userDiscriminator;
    private String userMention;
    private boolean isBot;

    public DiscordUser(String userName, String userID, String userAvatar, String userDiscriminator, String userMention, boolean isBot) {
        this.userName = userName;
        this.userID = userID;
        this.userAvatar = userAvatar;
        this.userDiscriminator = userDiscriminator;
        this.userMention = userMention;
        this.isBot = isBot;
    }
}
