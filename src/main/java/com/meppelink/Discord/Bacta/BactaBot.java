package com.meppelink.Discord.Bacta;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.meppelink.Discord.DiscordMessage;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import okhttp3.internal.ws.RealWebSocket.Message;

public class BactaBot extends ListenerAdapter {

    private Queue<HashMap<Channel, DiscordMessage>> messages = new LinkedList<>();

    BactaBot() {
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");
        JDA bot = JDABuilder.createDefault(token).setActivity(Activity.playing("in the bacta pod"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT).build();
        bot.addEventListener(this);
    }

    private void commands(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("/ping")) {
            event.getChannel().sendMessage("Pong!").queue();
        }

        if (event.getMessage().getContentRaw().equals("/shutdown")) {
            event.getChannel().sendMessage("Shutting down...").queue();
            event.getJDA().shutdown();
            System.out.println("\n\nShutting down...\n\n");
        }

        if (event.getMessage().getContentRaw().equals("/printMessages")) {
            event.getChannel().sendMessage("Printing Messages").queue();

            String messageString = "";

            for (HashMap<Channel, DiscordMessage> message : messages) {
                for (Channel channel : message.keySet()) {
                    if(channel.equals(event.getChannel()))
                    messageString += "\n" + message.get(channel).getMessage();
                }
            }

            event.getChannel().sendMessage(messageString).queue();
        }

        

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getGuild() == null || event.getAuthor().isBot()) {
            return;
        }

        commands(event);
        testing(event);
        
        if(event.getMessage().getContentRaw().startsWith("/")) {
            return;
        }

        String messageString = 
            "   NAME: " + event.getAuthor().getName() + 
            "\nMESSAGE: " + event.getMessage().getContentRaw() + 
            "\n   TIME: " + event.getMessage().getTimeCreated().toInstant() +
            "\n  GUILD: " + event.getGuild().getName() +
            "\n CHANNEL: " + event.getChannel().getName();
        event.getChannel().sendMessage("```" + messageString + "```").queue();
        try {
            DiscordMessage message = new DiscordMessage(
                    event.getAuthor().getName(),
                    event.getMessage().getContentRaw(),
                    event.getMessage().getTimeCreated().toInstant(),
                    event.getGuild(),
                    event.getChannel()
                );
            HashMap<Channel, DiscordMessage> messageMap = new HashMap<>();
            messageMap.put(event.getChannel(), message);
            messages.add(messageMap);
        } catch (Exception e) {
            System.out.println("\n\nError adding message to queue\n\n" + e);
        }

    }

    private void testing(MessageReceivedEvent event) {
        System.out.println(
            "NAME: " + event.getAuthor().getName() + 
            "\n MESSAGE: " + event.getMessage().toString() +
            "\nTIME: " + event.getMessage().getTimeCreated().toInstant() +
            "\nGUILD: " + event.getGuild().getName() +
            "\nCHANNEL: " + event.getChannel().getName()
        );
    }

    public void sendAMessage(String message, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(message).queue();
    }

    public void summarizeMessages(String message, Guild guild, Channel channel) {
        String messageString = "";

        // for (DiscordMessage discordMessage : messages) {
        //     messageString += "\n" + discordMessage.getMessage();
        // }

        guild.getTextChannelById(channel.getId()).sendMessage(messageString).queue();
    }
}
