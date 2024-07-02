package com.bacta.Discord.Bacta;

import java.util.ArrayList;

import com.bacta.Discord.Bacta.EventHandlers.ButtonEventHandler;
import com.bacta.Discord.Bacta.EventHandlers.MessageRecievedEventHandler;
import com.bacta.Discord.Bacta.EventHandlers.SlashCommandInteractionEventHandler;
import com.bacta.Discord.DataObjects.GuildMessageList;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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

        new CommandSetup(bot).setupCommands();

        devIDList.add("197944571844362240");
    }

}
