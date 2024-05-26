package com.bacta.Discord.Bacta;

import java.util.ArrayList;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.NotNull;

import com.bacta.ChatGPT.ChatGPT;
import com.bacta.Discord.Bacta.EventHandlers.ButtonEventHandler;
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
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;


public class BactaBot extends ListenerAdapter {                                                                                                                                                                            

    private GuildMessageList guildMessageList = new GuildMessageList();

    private final String questionGPTModel = "gpt-4o";
    private final String summarizeGPTModel = "gpt-4o";

    private ExecutorService summarizerThread;
    private ExecutorService questionThread;

    private ArrayList<String> devIDList = new ArrayList<String>();

    private Button btnDM;
    private Button btnShare;

    private int charLimit = 10000;


    // Constructs the bot and sets up the commands
    BactaBot() { 
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");
        JDABuilder jdaBuilder = JDABuilder.createDefault(token);
        JDA bot = jdaBuilder
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .setActivity(Activity.playing("In The Bacta Pod"))
                .addEventListeners(this, new ButtonEventHandler())
                .build();

        @SuppressWarnings("unused")
        CommandSetup commandSetup = new CommandSetup(bot);

        summarizerThread = Executors.newFixedThreadPool(10);
        questionThread = Executors.newFixedThreadPool(10);

        devIDList.add("197944571844362240");
    }


    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch(event.getName()) {
            case "send-message":
                if(devIDList.contains(event.getUser().getId())) {
                    //silently acknowledge the command and send the message

                    sendAMessage(event, event.getOption("message").getAsString(), event.getGuild(), event.getChannel());

                    return;
                }
                // reply with an ephemeral message if the user doesn't have permission
                event.reply("You don't have permission to do that.").setEphemeral(true).queue();
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
                    summarizerThread.shutdown();
                    questionThread.shutdown();
                    event.reply("Shutting down...").queue();
                    event.getJDA().shutdown();
                    System.out.println("\n\nShutting down...\n\n");
                } else {
                    event.reply("You don't have permission to do that.").queue();
                }
                
            break;
            
            case "summarize":
                // print out the user who requested the summary
                System.out.println("\nDEBUG: " + event.getUser() + " requested a summary.\n");
                try {
                    // Check if there are messages to summarize
                    if (guildMessageList.getMessagesInChannel(event.getChannel().getId()).isEmpty()) {
                        event.reply("There isn't a conversation to summarize...").queue();
                        return;
                    }
                } catch (Exception e) {
                    event.reply("There isn't a conversation to summarize...").queue();
                    return;
                }
            
                // Disable buttons initially
                btnDM = Button.primary("btnDM", "DM").asDisabled();
                btnShare = Button.success("btnShare", "Share").asDisabled();
            
                // Send initial ephemeral reply with interaction buttons
                event.reply("").setEphemeral(true).addActionRow(btnDM, btnShare).queue(reply -> {
                    summarizerThread.submit(() -> {
                        // Perform summarization
                        String summary = ChatGPT.summarizeMessages(guildMessageList.getMessagesInChannel(event.getChannel().getId()), summarizeGPTModel);
            
                        // Enable buttons and edit the original message with the summary
                        btnDM = Button.primary("btnDM", "DM").asEnabled();
                        btnShare = Button.success("btnShare", "Share").asEnabled();
                        event.getHook().editOriginal(summary).setActionRow(btnDM, btnShare).queue();
                    });
                });

                
            break;

            
            case "question":
                // print out the user who requested the question
                System.out.println("\nDEBUG: " + event.getUser() + " requested a question.\n");

                btnDM = Button.primary("btnDM", "DM").asDisabled();
                btnShare = Button.success("btnShare", "Share").asDisabled();

                // Send initial ephemeral reply with an interaction buttons
                event.reply("").setEphemeral(true).addActionRow(btnDM, btnShare).queue(reply -> {
                    questionThread.submit(() -> {
                        // Perform question answering
                        String answer = ChatGPT.askQuestion(event.getOption("question").getAsString(), questionGPTModel);
            
                        // Enable buttons and edit the original message with the answer
                        btnDM = Button.primary("btnDM", "DM").asEnabled();
                        btnShare = Button.success("btnShare", "Share").asEnabled();
                        event.getHook().editOriginal(answer).setActionRow(btnDM, btnShare).queue();
                    });
                });

            break;

            case "vanish":
            
            break;

            case "bacta":
                Random rand = new Random();
                int n = rand.nextInt(10);
                System.out.println(n);
                event.reply(n <= 2 ? "bacta" : "no bacta").queue();
            break;

            // takes user input and calculates the total number of characters, words, and spaces in the message
            case "charactercounter":
                String message = event.getOption("message").getAsString();
                int charCount = message.length();
                int wordCount = message.split("\\s+").length;
                int spaceCount = message.length() - message.replace(" ", "").length();
                event.reply("Character count: " + charCount + "\nWord count: " + wordCount + "\nSpace count: " + spaceCount).queue();
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

        // if the message is from the bot, ignore it
        if(event.getAuthor().isBot()) {
            return;
        }

        // if message occurs in a DM, ignore it
        if(event.getGuild() == null) {
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
                removeMessagesUntilUnderLimit(event, charLimit);
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

