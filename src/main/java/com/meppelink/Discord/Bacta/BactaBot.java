package com.meppelink.Discord.Bacta;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.NotNull;

import com.meppelink.ChatGPT.ChatGPT;
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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;


public class BactaBot extends ListenerAdapter {                                                                                                                                                                            

    private Queue<DiscordMessage> messages = new LinkedList<>();
    
    // @DONE refator this to use a hashmap of queues, with the key being the channel id.
    private HashMap<String, Queue<DiscordMessage>> messagesPerChannel = new HashMap<>();
    
    // @TODO make this a datastructure to keep track of char count per channel id.
    private HashMap<String, Integer> charCountPerChannel = new HashMap<>();

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
        bot.upsertCommand("display-messages", "Display messages stored inside Bacta  Bot.").setGuildOnly(true).queue();
        bot.upsertCommand("shutdown", "Shut down Bacta Bot.").setGuildOnly(true).queue();
        bot.upsertCommand("clear-messages", "clear the messages in Bacta Bot").setGuildOnly(true).queue();
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        switch(event.getName()) {
            case "send-message":
                sendAMessage(event.getOption("message").getAsString(), event.getGuild(), event.getChannel());
                break;
                
            case "clear-messages":
                messages.clear();
                event.reply("Messages cleared.").queue();
                break;

            case "display-messages":
                event.reply("Displaying messages...").queue();
                String messageString = "";

                if(messages.isEmpty()) {
                    event.getChannel().sendMessage("No messages stored.").queue();
                    return;
                }

                for (DiscordMessage message : messages) {
                    if(message.getGuild() == event.getGuild() && message.getChannel() == event.getChannel()) { 
                        messageString += "\n" + message.getUserName() + ": " + message.getMessage();
                    }
                }

                event.getChannel().sendMessage(messageString).queue();

                break;

            case "ping":
                event.reply("Pong!").queue();
                break;

            case "shutdown":
                event.reply("Shutting down...").queue();
                event.getJDA().shutdown();
                System.out.println("\n\nShutting down...\n\n");
                break;

            case "summarize":
                sendAMessage("Summarizing...", event.getGuild(), event.getChannel());
            
                ExecutorService executorService = Executors.newFixedThreadPool(1);
                
                try {
                    executorService.submit(() -> {
                        System.out.println(messages);
                        sendAMessage(ChatGPT.summarizeMessages(messages), event.getGuild(), event.getChannel());
                    });
                } finally {
                    executorService.shutdown();
                }
                break;

            default: 
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

        try {
            DiscordMessage message = new DiscordMessage(
                    event.getAuthor().getName(),
                    event.getMessage().getContentRaw(),
                    event.getMessage().getTimeCreated().toInstant(),
                    event.getGuild(),
                    event.getChannel());
                    
            // add the channel id to the hashmap if it doesn't exist, otherwise add the message length to the total char count
            if(charCountPerChannel.containsKey(event.getChannel().getId())) {
                charCountPerChannel.put(event.getChannel().getId(), charCountPerChannel.get(event.getChannel().getId()) + message.toString().length());
            } else {
                charCountPerChannel.put(event.getChannel().getId(), message.toString().length());
            }


            System.out.println(eventToString(event));
            
            // split message into 2 messages if it is too long
            if(message.toString().length() > 1800) {
                String messageString2 = "";
                String messageString3 = "";

                messageString2 = message.toString().substring(0, 1800);
                messageString3 = message.toString().substring(1800, message.toString().length());

                DiscordMessage discordMessage2 = createDiscordMessage(event, messageString2);
                DiscordMessage discordMessage3 = createDiscordMessage(event, messageString3);

                event.getChannel().sendMessage("```" + messageString2 + "```").queue();
                event.getChannel().sendMessage("```" + messageString3 + "```").queue();

                messages.add(discordMessage2);
                messages.add(discordMessage3);
                // @DONE add these to the hash queue, and not the original message
                messagesPerChannel.put(event.getChannel().getId(), messages);
                // add the total char count of both the split messages to the charCountPerChannel
                charCountPerChannel.put(event.getChannel().getId(), charCountPerChannel.get(event.getChannel().getId()) + messageString2.length() + messageString3.length());

            } else  {
                
            }
            
            // make a check to see if the char count is over the limit, and if so, remove messages until it is under the limit
            // @TODO make this a function


        } catch (Exception e) {
            System.out.println("\n\nError adding message to queue\n\n" + e);
        } finally {
            // remove messages until the total char count is under 7000
            removeMessagesUntilUnderLimit(event);
        }
        

    }

    // event object to string
    private String eventToString(MessageReceivedEvent event) {
        String formattedEvent = "";

        formattedEvent = "    NAME: " + event.getAuthor().getName() +
                "\n MESSAGE: " + event.getMessage().getContentRaw() +
                "\n    TIME: " + event.getMessage().getTimeCreated().toInstant() +
                "\n   GUILD: " + event.getGuild().getName() +
                "\n CHANNEL: " + event.getChannel().getName() +
                "\n STICKER: " + event.getMessage().getStickers().toString() +
                "\n CHARCNT: " + charCountPerChannel.get(event.getChannel().getId()) +
                "\n Channel: " + event.getChannel().getId();

        return formattedEvent;
    }

    // removes messages in the queue until the total char count is under 7000
    private void removeMessagesUntilUnderLimit(MessageReceivedEvent event) {
        while(charCountPerChannel.get(event.getChannel().getId()) > 7000) { // remove messages until the total char count is under 3000
                DiscordMessage smg = messages.remove();
                System.out.println(smg.toString().length());
                charCountPerChannel.put(event.getChannel().getId(), charCountPerChannel.get(event.getChannel().getId()) - smg.toString().length());
            }
    }

    // creates a discord message object
    private DiscordMessage createDiscordMessage(MessageReceivedEvent event, String messageString) {
        return new DiscordMessage(
                event.getAuthor().getName(),
                messageString,
                event.getMessage().getTimeCreated().toInstant(),
                event.getGuild(),
                event.getChannel());
    }

    // sends a message to the channel
    public void sendAMessage(String message, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(message).queue();
    }

    // sends a sticker to the channel
    // @TODO
    public void sendASticker(String sticker, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(sticker).queue();
    }

}

