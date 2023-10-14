package com.meppelink.Discord.Bacta;

import java.util.LinkedList;
import java.util.Queue;

import org.jetbrains.annotations.NotNull;

import com.meppelink.Discord.DiscordMessage;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;


public class BactaBot extends ListenerAdapter {

    private Queue<DiscordMessage> messages = new LinkedList<>();

    BactaBot() {
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");
        JDABuilder jdaBuilder = JDABuilder.createDefault(token);
        JDA bot = jdaBuilder
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .setActivity(Activity.playing("In The Bacta Pod"))
                .addEventListeners(this)
                .build();

        bot.upsertCommand("send-message", "Send a message from Bacta Bot").setGuildOnly(true).queue();
        bot.upsertCommand("ping", "Pong!").setGuildOnly(true).queue();
        bot.upsertCommand("display-messages", "Display messages stored inside Bacta  Bot.").setGuildOnly(true).queue();
        bot.upsertCommand("shutdown", "Shut down Bacta Bot.").setGuildOnly(true).queue();
        
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("send-message")) {
            event.reply("Sending message...").queue();
            sendAMessage(event.getOption("message").getAsString(), event.getGuild(), event.getChannel());
        }

        if (event.getName().equals("display-messages")) {
            event.reply("Displaying messages...").queue();
            String messageString = "";

            for (DiscordMessage message : messages) {
                if(message.getGuild() == event.getGuild() && message.getChannel() == event.getChannel()) { 
                    messageString += "\n" + message.getMessage();
                }
            }

            event.getChannel().sendMessage(messageString).queue();
        }

        if (event.getName().equals("ping")) {
            event.reply("Pong!").queue();
        }

        if(event.getName().equals("shutdown")) {
            event.reply("Shutting down...").queue();
            event.getJDA().shutdown();
            System.out.println("\n\nShutting down...\n\n");
        }

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getGuild() == null || event.getAuthor().isBot()) {
            return;
        }
        
        testing(event);

        if (event.getMessage().getContentRaw().startsWith("/")) {
            return;
        }

        String messageString = "   NAME: " + event.getAuthor().getName() +
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
                    event.getChannel());

            messages.add(message);

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
                        "\nCHANNEL: " + event.getChannel().getName());
    }

    public void sendAMessage(String message, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(message).queue();
    }

}

