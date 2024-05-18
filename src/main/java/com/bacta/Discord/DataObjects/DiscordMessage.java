package com.bacta.Discord.DataObjects;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;

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

    public ZonedDateTime getMessageZonedDateTime() {
        return ZonedDateTime.parse(messageTime, formatter);
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

    public static String calculateConversationDuration(Queue<DiscordMessage> messages) {
        // Initialize variables for earliest and latest message times
        ZonedDateTime earliestTime = null;
        ZonedDateTime latestTime = null;

        // Find earliest and latest message times
        for (DiscordMessage message : messages) {
            ZonedDateTime messageTime = message.getMessageZonedDateTime();
            if (earliestTime == null || messageTime.isBefore(earliestTime)) {
                earliestTime = messageTime;
            }
            if (latestTime == null || messageTime.isAfter(latestTime)) {
                latestTime = messageTime;
            }
        }

        // Calculate duration between earliest and latest times
        Duration duration = Duration.between(earliestTime, latestTime);
        long seconds = duration.getSeconds();

        // Format the duration
        if (seconds < 60) {
            return seconds + " seconds";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        } else {
            long hours = seconds / 3600;
            long remainingMinutes = (seconds % 3600) / 60;
            StringBuilder formattedDuration = new StringBuilder();
            formattedDuration.append(hours).append(" hour").append(hours > 1 ? "s" : "");
            if (remainingMinutes > 0) {
                formattedDuration.append(" ").append(remainingMinutes).append(" minute").append(remainingMinutes > 1 ? "s" : "");
            }
            return formattedDuration.toString();
        }
    }

}
