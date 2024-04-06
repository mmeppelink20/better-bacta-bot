package com.meppelink.Discord.Bacta;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.NotNull;

import com.meppelink.ChatGPT.ChatGPT;
import com.meppelink.Discord.DiscordMessage;
import com.meppelink.Discord.GuildMessageList;

import io.github.cdimascio.dotenv.Dotenv;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;


public class BactaBot extends ListenerAdapter {                                                                                                                                                                            

    private GuildMessageList guildMessageList = new GuildMessageList();

    // Constructs the bot and sets up the commands
    BactaBot() { 
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");
        JDABuilder jdaBuilder = JDABuilder.createDefault(token);
        JDA bot = jdaBuilder
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .setActivity(Activity.playing("In The Bacta Pod"))
                .addEventListeners(this)
                .build();
        OptionData option1 = new OptionData(OptionType.STRING, "message", "The message to send.").setRequired(true);
        //OptionData messageCount = new OptionData(OptionType.INTEGER, "message-count", "The number of messages to summarize").setRequired(true);
        bot.upsertCommand("summarize", "Send a message to be summarized").setGuildOnly(true).queue();
        bot.upsertCommand("send-message", "Send a message to the channel.").addOptions(option1).setGuildOnly(true).queue();
        bot.upsertCommand("ping", "Pong!").setGuildOnly(true).queue();
        bot.upsertCommand("shutdown", "Shut down Bacta Bot.").setGuildOnly(true).queue();
        bot.upsertCommand("clear-messages", "clear the messages in Bacta Bot").setGuildOnly(true).queue();
        bot.upsertCommand("question", "ask bacta bot a question").setGuildOnly(true).queue();
        bot.upsertCommand("bacta", "bacta, or no bacta...").setGuildOnly(true).queue();
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        switch(event.getName()) {
            case "send-message":
                sendAMessage(event, event.getOption("message").getAsString(), event.getGuild(), event.getChannel());
            break;
                
            case "clear-messages":
                if (event.getUser().getId().equals("197944571844362240")) {
                    try {
                        if(guildMessageList.getMessagesInChannel(event.getChannel().getId()).isEmpty()) {
                            event.reply("There are no messages.").queue();
                            return;
                        }
                    } catch(Exception e) {
                        event.reply("No messages stored.").queue();
                        return;
                    }
                    guildMessageList.clearMessages(event);
                    event.reply("Messages cleared.").queue();
                } else {
                    event.reply("You don't have permission to do that.").queue();
                }
            break;

            case "ping":
                event.reply("Pong!").queue();
                //display the message queue in the console(for debugging purposes).
                try{
                    if(guildMessageList.getMessagesInChannel(event.getChannel().getId()).isEmpty()) {
                        System.out.println("DEBUG: No messages stored.");
                    } else {
                        System.out.println("DEBUG: Displaying char count..." + guildMessageList.getCharCountPerChannel(event.getChannel().getId()));
                        System.out.println("DEBUG: Displaying message queue...");
                    }
                    guildMessageList.getMessagesInChannel(event.getChannel().getId()).forEach(s -> System.out.println(s));
                } catch(Exception e) {
                    System.out.println("DEBUG: No messages stored.");
                }

            break;

            case "shutdown":
                if (event.getUser().getId().equals("197944571844362240")) {
                    event.reply("Shutting down...").queue();
                    event.getJDA().shutdown();
                    System.out.println("\n\nShutting down...\n\n");
                } else {
                    event.reply("You don't have permission to do that.").queue();
                }
                
            break;

            case "summarize":
                try {
                    if(guildMessageList.getMessagesInChannel(event.getChannel().getId()).isEmpty()) {
                        event.reply("There isn't a conversation to summarize...").queue();
                        return;
                    }
                } catch (Exception e) {
                    event.reply("There isn't a conversation to summarize...").queue();
                    return;
                }

                event.reply("Summarizing...").queue();

                ExecutorService summarizerThread = Executors.newFixedThreadPool(3);
                
                try {
                    summarizerThread.submit(() -> {
                        System.out.println(guildMessageList.getMessagesInChannel(event.getChannel().getId()));
                        sendAMessage(ChatGPT.summarizeMessages(guildMessageList.getMessagesInChannel(event.getChannel().getId())), event.getGuild(), event.getChannel());
                    });
                } finally {
                    summarizerThread.shutdown();
                }
            break;
            
            case "question":
                event.reply("I'm sorry, I can't do that yet.").queue();
                //@TODO
                // ExecutorService questinThread = Executors.newFixedThreadPool(3);
                // try {
                //     questinThread.submit(() -> {
                //         System.out.println("DEBUG: ");
                //         System.out.println(guildMessageList.getMessagesInChannel(event.getChannel().getId()));
                //         sendAMessage(ChatGPT.summarizeMessages(guildMessageList.getMessagesInChannel(event.getChannel().getId())), event.getGuild(), event.getChannel());
                //     });
                // } finally {
                //     questinThread.shutdown();
                // }
            break;

            case "bacta":
                Random rand = new Random();
                int n = rand.nextInt(2);
                System.out.println(n);
                event.reply(n % 2 == 0 ? "bacta" : "no bacta").queue();
            break;

            default:
                event.reply("I don't know that command.").queue();
            break;
                
        }
    }

    // recieves message events and adds them to the queue, and splits them into multiple messages if they are too long.
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getGuild() == null || event.getAuthor().isBot()) {
            return;
        }

        if (event.getMessage().getContentRaw().startsWith("/")) {
            return;
        }

        // if the guild isn't in the guildMessageList, add it
        if(!guildMessageList.guildInMap(event.getGuild().getId())) {
            guildMessageList.addGuildToMap(event.getGuild().getId());
            System.out.println("DEBUG: *** Added guild to map: " + event.getGuild().getId() + " ***");
        }

        // add the channel name to the guild map's hashset if it doesn't exist
        if(!guildMessageList.getGuildChannelMap().get(event.getGuild().getId()).contains(event.getChannel().getId())) {
            guildMessageList.addChannelToGuild(event.getGuild().getId(), event.getChannel().getId());
            System.out.println("DEBUG: *** Added channel to guild: " + event.getChannel().getId() + " ***");
        }

        // // add the channel to the channelMessages hashmap if it doesn't exist
        // try {
        //     guildMessageList.addChannelToMap(event.getChannel().getId());
        //     System.out.println(guildMessageList.getMessagesInChannel(event.getChannel().getId()));
        // } catch (Exception e) {
        //     System.out.println("\n\nError adding channel to map\n\n" + e);
        // }
        

        try {
            // add the channel to the channelMessages hashmap if it doesn't exist
            if(!guildMessageList.channelInMap(event.getChannel().getId())) {
                guildMessageList.addChannelToMap(event.getChannel().getId());
                System.out.println(guildMessageList.getMessagesInChannel(event.getChannel().getId()));
            }
        } catch (Exception e) {
            System.out.println("\n\nError adding channel to map\n\n" + e);
        }
        
        
        // create the DiscordMessage object to be added to the queue
        try {
            DiscordMessage message = new DiscordMessage(
                    event.getAuthor().getName(),
                    event.getMessage().getContentRaw(),
                    event.getMessage().getTimeCreated().toInstant(),
                    event.getGuild(),
                    event.getChannel());

    
            //@TODO FIX THIS, BRUH

            // split message into 2 messages if it is too long
            int maxSingleMessageLength = 1000;
            if(message.toString().length() > maxSingleMessageLength) {

                ArrayList<DiscordMessage> splitMessages = splitMessage(event);
                
                DiscordMessage discordMessage1 = splitMessages.get(0);
                DiscordMessage discordmessage2 = splitMessages.get(1);

                // if(guildMessageList.getCharCountPerChannel(event.getChannel().getId()) == null) {
                //     guildMessageList.setCharCountPerChannel(event.getChannel().getId(), 0);
                // }
                
                guildMessageList.addMessageToChannel(event.getChannel().getId(), discordMessage1);
                guildMessageList.addMessageToChannel(event.getChannel().getId(), discordmessage2);
            } else  {
                guildMessageList.addMessageToChannel(event.getChannel().getId(), message);
            }
        } catch (Exception e) {
            System.out.println("\n\nError adding message to queue\n\n" + e);
        } finally {
            System.out.println("DEBUG: \n" + eventToString(event) + "\n");
            try {
                if(guildMessageList.getCharCountPerChannel(event.getChannel().getId()) > 7000) {
                    removeMessagesUntilUnderLimit(event, 7000);
                }
            } catch (Exception e) {
                System.out.println("DEBUG: \n" + e + "\n");
            } finally {
                System.out.println("DEBUG: \n" + guildMessageList.getCharCountPerChannel(event.getChannel().getId()) + "\n");
            }
            
            // remove messages until the total char count is under 7000
            
        }
    }

    // @TODO refactor this to split a message into a specified quantity of messages
    // splits a message into 2 messages
    private ArrayList<DiscordMessage> splitMessage(MessageReceivedEvent event) {
        ArrayList<DiscordMessage> splitMessages = new ArrayList<DiscordMessage>();

        String messageString2 = "";
        String messageString3 = "";

        // split message into 2, where its size is half of the original message
        messageString2 = event.getMessage().getContentRaw().substring(0, event.getMessage().getContentRaw().length() / 2);
        messageString3 = event.getMessage().getContentRaw().substring(event.getMessage().getContentRaw().length() / 2, event.getMessage().getContentRaw().length());

        DiscordMessage discordMessage2 = new DiscordMessage(event.getAuthor().getName(), "[1/2] " + messageString2, event.getMessage().getTimeCreated().toInstant(), event.getGuild(), event.getChannel());
        DiscordMessage discordMessage3 = new DiscordMessage(event.getAuthor().getName(), "[2/2] " + messageString3, event.getMessage().getTimeCreated().toInstant(), event.getGuild(), event.getChannel());

        System.out.println("DEBUG: \n" + discordMessage2.toString().length() + "\n");
        System.out.println("DEBUG: \n" + discordMessage3.toString().length() + "\n");

        splitMessages.add(discordMessage2);
        splitMessages.add(discordMessage3);

        return splitMessages;
    }

    // event object to string
    public String eventToString(MessageReceivedEvent event) {
        // Format the time using the desired pattern
        String formattedTime = DiscordMessage.formatTime(event.getMessage().getTimeCreated().toInstant());

        // Format the event data
        String formattedEvent = "    NAME: " + event.getAuthor().getName() +
                "\n MESSAGE: " + event.getMessage().getContentRaw() +
                "\n    TIME: " + formattedTime +
                "\n   GUILD: " + event.getGuild().getName() + " ID: " + event.getGuild().getId() +
                "\n CHANNEL: " + event.getChannel().getName() + " ID: " + event.getChannel().getId() +
                // ternary operator to check if message contains a sticker
                (event.getMessage().getStickers().isEmpty() ? "" : "\n STICKER: " + event.getMessage().getStickers().get(0).getName()) +
                "\n CHARCNT: " + guildMessageList.getCharCountPerChannel(event.getChannel().getId());

        return formattedEvent;
    }

    // removes messages in the queue until the total char count is under 7000
    private void removeMessagesUntilUnderLimit(MessageReceivedEvent event, int charLimit) {
        while(guildMessageList.getCharCountPerChannel(event.getChannel().getId()) > charLimit) {
            // @TODO implement a removecharsfromcharcount method
            guildMessageList.removeMessageFromChannel(event);
        }
    }

    // sends a message to the channel
    public void sendAMessage(String message, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(message).queue();
    }

    // sends a mesaage to the channel, with event object for Logging.
    public void sendAMessage(@NotNull SlashCommandInteractionEvent event, String message, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(message).queue();
        System.out.println("DEBUG: \n" + event.getUser() + " sent: " + event.getOption("message").getAsString());
    }

    // sends a sticker to the channel
    // @TODO
    public void sendASticker(String sticker, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(sticker).queue();
    }

    // @TODO
    // write a method to send a message to every channel in the guild list
    public void sendToAllChannels(String message, Guild guild) {
        for (String channelID : guildMessageList.getGuildChannelMap().get(guild.getId())) {
            sendAMessage(message, guild, guild.getTextChannelById(channelID));
        }
    }

}

