package com.meppelink.Discord;

import com.meppelink.data_access.DAO_MySQL;
import com.meppelink.data_access.DiscordDAO_MySQL;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.lang.reflect.Array;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class DiscordBot extends ListenerAdapter {
    private static JDA jda;

    public DiscordBot() {

    }
    public int addMessage(HashMap<String, String> message, ZonedDateTime zonedDateTime) {
        DiscordDAO_MySQL dao = new DiscordDAO_MySQL();
        return dao.addDiscordMessage(message, zonedDateTime);
    }
    public int addDiscordUser(HashMap<String, String> user) {
        DiscordDAO_MySQL dao = new DiscordDAO_MySQL();
        return dao.addDiscordUser(user);
    }
    public int selectDiscordUserByUserID(String userID) {
        DiscordDAO_MySQL dao = new DiscordDAO_MySQL();
        return dao.selectDiscordUserByUserID(userID);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if(event.getGuild() == null) {
            return; // don't want to store messages from DMs
        }

        if(event.getMessage().getContentRaw().contains("bacta")){
            event.getAuthor().openPrivateChannel().queue((channel) -> {
                channel.sendMessage("have a bacta").queue();
            });
        }

        HashMap<String, String> user = new HashMap<>();
        user.put("user_name", event.getAuthor().getName());
        user.put("user_id", event.getAuthor().getId());
        user.put("user_avatar", event.getAuthor().getAvatarUrl());
        user.put("user_discriminator", event.getAuthor().getDiscriminator());
        user.put("user_mention", event.getAuthor().getAsMention());
        user.put("is_bot", String.valueOf(event.getAuthor().isBot()));
        if(selectDiscordUserByUserID(user.get("user_id")) == 1) {
            System.out.println("User already exists");
        }
        else {
            if(addDiscordUser(user) > 0) {
                System.out.println("User added");
            }
        }

        String datetimeString = event.getMessage().getTimeCreated().toString();
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(datetimeString);
        ZoneOffset zoneOffset = offsetDateTime.getOffset();
        ZonedDateTime zonedDateTime = offsetDateTime.atZoneSameInstant(zoneOffset);

        HashMap<String, String> message = new HashMap<>();
        message.put("user_name", event.getAuthor().getName());
        message.put("user_id", event.getAuthor().getId());
        message.put("server_name", event.getGuild().getName());
        message.put("server_id", event.getGuild().getId());
        message.put("channel_name", event.getChannel().getName());
        message.put("channel_id", event.getChannel().getId());
        message.put("message", event.getMessage().getContentRaw());
        message.put("message_id", event.getMessageId());
        message.put("message_link", event.getMessage().getJumpUrl());
        if(addMessage(message, zonedDateTime) > 0) {
            System.out.println("Message added");
        }


        if (event.getMessage().getContentRaw().equals("!ping")) {
            event.getChannel().sendMessage("Pong!").queue();
        }

        System.out.println(message + "\n" + zonedDateTime);

    }

    public void sendAMessage(String message, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(message).queue();
    }

}
