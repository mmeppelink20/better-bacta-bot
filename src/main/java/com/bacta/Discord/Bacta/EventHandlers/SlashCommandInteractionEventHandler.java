package com.bacta.Discord.Bacta.EventHandlers;

import com.bacta.ChatGPT.ChatGPT;
import com.bacta.Discord.DataObjects.GuildMessageList;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class SlashCommandInteractionEventHandler extends ListenerAdapter {

    private final GuildMessageList guildMessageList;
    private final String questionGPTModel = "gpt-4o";
    private final String summarizeGPTModel = "gpt-4o";

    private Button btnDM;
    private Button btnShare;
    private final ArrayList<String> devIDList = new ArrayList<>();

    public SlashCommandInteractionEventHandler(GuildMessageList guildMessageList) {
        this.guildMessageList = guildMessageList;
        devIDList.add("197944571844362240");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
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
                try {
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

                event.reply("").setEphemeral(true).addActionRow(btnDM, btnShare).queue(reply -> {
                    // Use CompletableFuture to handle async execution of the summarization
                    CompletableFuture.supplyAsync(() -> {
                        return ChatGPT.summarizeMessages(guildMessageList.getMessagesInChannel(event.getChannel().getId()), summarizeGPTModel);
                    }).thenAccept(summary -> {
                        // Enable buttons and edit the original message with the summary
                        btnDM = Button.primary("btnDM", "DM").asEnabled();
                        btnShare = Button.success("btnShare", "Share").asEnabled();
                        event.getHook().editOriginal(summary).setActionRow(btnDM, btnShare).queue();
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        event.getHook().editOriginal("An error occurred while processing the summary. Please try again later.").queue();
                        return null;
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
                    CompletableFuture.supplyAsync(() -> {
                        return ChatGPT.askQuestion(event.getOption("question").getAsString(), questionGPTModel);
                    }).thenAccept(answer -> {
                        // Enable buttons and edit the original message with the answer
                        btnDM = Button.primary("btnDM", "DM").asEnabled();
                        btnShare = Button.success("btnShare", "Share").asEnabled();
                        event.getHook().editOriginal(answer).setActionRow(btnDM, btnShare).queue();
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        event.getHook().editOriginal("An error occurred while processing your request. Please try again later.").queue();
                        return null;
                    });
                });
                break;

            case "vanish":
                // Handle vanish command here
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

    // Helper methods for sending messages
    private void sendAMessage(SlashCommandInteractionEvent event, String message, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(message).queue();
        System.out.println("DEBUG: \n" + event.getUser() + " sent: " + event.getOption("message").getAsString());
    }
}
