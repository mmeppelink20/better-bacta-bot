package com.bacta.Discord.Bacta;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.NotNull;

import com.bacta.ChatGPT.ChatGPT;
import com.bacta.Discord.DataObjects.DiscordMessage;
import com.bacta.Discord.DataObjects.GuildMessageList;

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
        OptionData option2 = new OptionData(OptionType.STRING, "question", "The question to ask bacta bot.").setRequired(true);
        OptionData option3 = new OptionData(OptionType.STRING, "messagestats", "The message to count characters, spaces, and words.").setRequired(true);

        bot.upsertCommand("summarize", "Send a message to be summarized").setGuildOnly(true).queue();
        bot.upsertCommand("send-message", "Send a message to the channel.").addOptions(option1).setGuildOnly(true).queue();
        bot.upsertCommand("ping", "Pong!").setGuildOnly(true).queue();
        bot.upsertCommand("shutdown", "Shut down Bacta Bot.").setGuildOnly(true).queue();
        bot.upsertCommand("clear-messages", "clear the messages in Bacta Bot").setGuildOnly(true).queue();
        bot.upsertCommand("question", "ask bacta bot a question").addOptions(option2).setGuildOnly(true).queue();
        bot.upsertCommand("bacta", "bacta, or no bacta...").setGuildOnly(true).queue();
        bot.upsertCommand("vanish", "vanish...").setGuildOnly(true).queue();
        bot.upsertCommand("charactercounter", "supply a message to see how many spaces, characters, and words it has").addOptions(option3).setGuildOnly(true).queue();
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
                        sendAMessage(ChatGPT.summarizeMessages(guildMessageList.getMessagesInChannel(event.getChannel().getId()), "gpt-4"), event.getGuild(), event.getChannel());
                    });
                } finally {
                    summarizerThread.shutdown();
                }
            break;
            
            case "question":
            
                ExecutorService questionThread = Executors.newFixedThreadPool(3);
                try {
                    questionThread.submit(() -> {
                        System.out.println("DEBUG: " + event.getOption("question").getAsString());
                    
                        event.reply("<@" + event.getUser().getId() + "> asked: " + event.getOption("question").getAsString()).queue();
                        sendAMessage(ChatGPT.askQuestion(event.getOption("question").getAsString(), "gpt-4"), event.getGuild(), event.getChannel());
                    });
                } finally {
                    questionThread.shutdown();
                }
            break;

            case "vanish":

            break;

            case "bacta":
                Random rand = new Random();
                int n = rand.nextInt(10);
                System.out.println(n);
                event.reply(n <= 2 ? "bacta" : "no bacta").queue();
            break;

            case "charactercounter":
                event.reply(event.getOption("messagestats") + "\n" + "Character count: " + event.getOption("message").getAsString().length() + "\n" +
                            "Word count: " + event.getOption("message").getAsString().split("\\s+").length + "\n" +
                            "Space count: " + event.getOption("message").getAsString().split(" ").length).queue();
            break;
            
            default:
                event.reply("I don't know that command.").queue();
            break;
                
        }
    }

    // recieves message events and adds them to the queue, and splits them into multiple messages if they are too long.
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // if the message is mentioning user <@1095791282531610655> then reply to the message with the @mention
        // if(event.getMessage().getContentRaw().contains("<@109579128253161065>")) {
        //     ExecutorService questionThread = Executors.newFixedThreadPool(3);
        //         try {
        //             questionThread.submit(() -> {
        //                 // reply the the message with the @ here
        //                 sendAMessage("<@" + event.getAuthor().getId() + ">", event.getGuild(), event.getChannel());
                        
        //             });
        //         } finally {
        //             questionThread.shutdown();
        //         }
        //     sendAMessage("You mentioned me!", event.getGuild(), event.getChannel());
        // }

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

        // if the channel isn't in the channelMessageList, add it
        if(!guildMessageList.channelInMap(event.getChannel().getId())) {
            guildMessageList.addChannelToMap(event.getChannel().getId());
            System.out.println("DEBUG: *** Added channel to guild channel map: " + event.getChannel().getId() + " ***");
        }

        // create the DiscordMessage object to be added to the queue
        try {
            DiscordMessage message = new DiscordMessage(
                    event.getAuthor().getName(),
                    event.getMessage().getContentRaw(),
                    event.getMessage().getTimeCreated().toInstant(),
                    event.getGuild(),
                    event.getChannel());

            // split message into 2 messages if it is too long
            int maxSingleMessageLength = 1000;
            if(message.toString().length() > maxSingleMessageLength) {

                ArrayList<DiscordMessage> splitMessages = splitMessage(event);
                
                DiscordMessage discordMessage1 = splitMessages.get(0);
                DiscordMessage discordmessage2 = splitMessages.get(1);
                
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
            
        }
    }

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

    // event object toString
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

