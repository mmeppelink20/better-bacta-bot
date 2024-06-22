package com.bacta.Discord.Bacta;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import com.bacta.Discord.Bacta.EventHandlers.ButtonEventHandler;
import com.bacta.Discord.Bacta.EventHandlers.MessageRecievedEventHandler;
import com.bacta.Discord.Bacta.EventHandlers.SlashCommandInteractionEventHandler;
import com.bacta.Discord.DataObjects.GuildMessageList;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class BactaBot extends ListenerAdapter {                                                                                                                                                                            

    private GuildMessageList guildMessageList = new GuildMessageList();
    private ArrayList<String> devIDList = new ArrayList<>();
    private int charLimit = 10000;

    // Constructs the bot and sets up the commands
    BactaBot() { 
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");
        JDABuilder jdaBuilder = JDABuilder.createDefault(token);

        JDA bot = jdaBuilder
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .setActivity(Activity.playing("In The Bacta Pod"))
                .addEventListeners(this, new ButtonEventHandler(), new SlashCommandInteractionEventHandler(guildMessageList), new MessageRecievedEventHandler(guildMessageList, charLimit))
                .build();

        // Chain the CommandSetup after building the JDA instance
        new CommandSetup(bot).setupCommands();

        devIDList.add("197944571844362240");
    }

    // Sends a message to the channel
    public static void sendAMessage(String message, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(message).queue();
    }

    // Sends a message to the channel, with event object for logging.
    public void sendAMessage(@NotNull SlashCommandInteractionEvent event, String message, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(message).queue();
        System.out.println("DEBUG: \n" + event.getUser() + " sent: " + event.getOption("message").getAsString());
    }

    // Sends a sticker to the channel
    // @TODO
    public void sendASticker(String sticker, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(sticker).queue();
    }

    // @TODO
    // Write a method to send a message to every channel in the guild list
    public void sendToAllChannels(String message, Guild guild) {
        for (String channelID : guildMessageList.getGuildChannelMap().get(guild.getId())) {
            sendAMessage(message, guild, guild.getTextChannelById(channelID));
        }
    }
}
