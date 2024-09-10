package com.bacta.Discord.Bacta.EventHandlers;

import com.bacta.ChatGPT.ChatGPT;
import com.bacta.Discord.DataObjects.GuildMessageList;
import com.bacta.Olympics.Olympics;
import com.bacta.Olympics.DataObjects.OlympicsNationMedalRecord;
import com.bacta.Discord.DataObjects.BactaData;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import java.awt.Color;

public class SlashCommandInteractionEventHandler extends ListenerAdapter {

    private final GuildMessageList guildMessageList;
    private final String questionGPTModel = "gpt-4o";
    private final String summarizeGPTModel = "gpt-4o";

    private Button btnDM;
    private Button btnShare;

    public SlashCommandInteractionEventHandler(GuildMessageList guildMessageList) {
        this.guildMessageList = guildMessageList;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch(event.getName()) {
            case "ping":
                event.reply("Pong!").setEphemeral(true).queue();
                //display the message queue in the console(for debugging purposes).
                try {
                    if(guildMessageList.getMessagesInChannel(event.getChannel().getId()).isEmpty()) {
                        System.out.println("DEBUG: No messages stored.");
                    } else {
                        System.out.println("DEBUG: Displaying char count..." + guildMessageList.getCharCountPerChannel(event.getChannel().getId()));
                        System.out.println("DEBUG: Displaying message queue...");
                    }
                    System.out.println(guildMessageList.getMessagesInChannel(event.getChannel().getId()).toString());
                } catch(Exception e) {
                    System.out.println("DEBUG: No messages stored.");
                }
                break;

            case "summarize":
                // print out the user who requested the summary
                try {
                    // Check if there are messages to summarize
                    if (guildMessageList.getMessagesInChannel(event.getChannel().getId()).isEmpty()) {
                        event.reply("There isn't a conversation to summarize...").setEphemeral(true).queue();
                        return;
                    }
                } catch (Exception e) {
                    event.reply("There isn't a conversation to summarize...").setEphemeral(true).queue();
                    return;
                }

                // Disable buttons initially
                btnDM = Button.primary("btnDM", "DM").asDisabled();
                btnShare = Button.success("btnShare", "Share").asDisabled();

                // start a timer to see how long the api call takes
                long startTime = System.currentTimeMillis();

                event.reply("").setEphemeral(true).addActionRow(btnDM, btnShare).queue(reply -> {
                    // Use CompletableFuture to handle async execution of the summarization
                    CompletableFuture.supplyAsync(() -> {
                        return ChatGPT.summarizeMessages(guildMessageList.getMessagesInChannel(event.getChannel().getId()), summarizeGPTModel);
                    }).thenAccept(summary -> {
                        // Enable buttons and edit the original message with the summary
                        btnDM = Button.primary("btnDM", "DM").asEnabled();
                        btnShare = Button.success("btnShare", "Share").asEnabled();
                        event.getHook().editOriginal(summary).setActionRow(btnDM, btnShare).queue();

                        // dm users on the DevIDList the summary and who requested it
                        BactaData.GetDevIDList().forEach(devID -> {
                            event.getJDA().retrieveUserById(devID).queue(user -> {
                                user.openPrivateChannel().queue(channel -> {
                                    // end the timer here
                                    long endTime = System.currentTimeMillis() - startTime;
                                    channel.sendMessage("**Summary requested by **" + event.getUser().getAsMention() + " in " + event.getChannel().getAsMention() + "\nRuntime for command: " + endTime + "ms"
                                    + "\n\nSummary: \n" + summary).queue();
                                });
                            });
                        });

                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        event.getHook().editOriginal("An error occurred while processing the summary. Please try again later.").queue();
                        return null;
                    });
                });

                
                
                break;

            case "question":
                // print out the user who requested the question
                String question = event.getOption("question").getAsString();

                btnDM = Button.primary("btnDM", "DM").asDisabled();
                btnShare = Button.success("btnShare", "Share").asDisabled();

                // start a timer to see how long the api call takes
                startTime = System.currentTimeMillis();

                // Send initial ephemeral reply with an interaction buttons
                event.reply("").setEphemeral(true).addActionRow(btnDM, btnShare).queue(reply -> {
                    CompletableFuture.supplyAsync(() -> {
                        return ChatGPT.askQuestion(question, questionGPTModel);
                    }).thenAccept(answer -> {
                        // Enable buttons and edit the original message with the answer
                        btnDM = Button.primary("btnDM", "DM").asEnabled();
                        btnShare = Button.success("btnShare", "Share").asEnabled();
                        event.getHook().editOriginal(answer).setActionRow(btnDM, btnShare).queue();

                        // dm users on the DevIDList the summary and who requested it
                        BactaData.GetDevIDList().forEach(devID -> {
                            event.getJDA().retrieveUserById(devID).queue(user -> {
                                user.openPrivateChannel().queue(channel -> {
                                    // end the timer here
                                    long endTime = System.currentTimeMillis() - startTime;
                                    channel.sendMessage("**Question requested by **" + event.getUser().getAsMention() + " in " + event.getChannel().getAsMention() + "\nRuntime for command: " + endTime + "ms"
                                    + "\n\nQuestion: \n" + question
                                    + "\n\nAnswer: \n" + answer).queue();
                                });
                            });
                        });
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        event.getHook().editOriginal("An error occurred while processing your request. Please try again later.").queue();
                        return null;
                    });
                });
                break;
            case "bacta":
                Random rand = new Random();
                int n = rand.nextInt(10);
                System.out.println(n);
                event.reply(n <= 2 ? "bacta" : "no bacta").queue();
                break;
            case "olympics":
                try {
                    ArrayList<OlympicsNationMedalRecord> medalLeaderBoard = Olympics.GetMedalLeaderBoard();
                    
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setThumbnail("https://gstatic.olympics.com/s1/f_auto/static/srm/paris-2024/topic-assets/paris-2024/emblem-oly.svg");
                    eb.setTitle("Olympic Medal Leaderboard");
                    eb.setColor(Color.GREEN);
                
                    boolean containsUS = false;
                    for(int i = 0; i < 5; i++) {
                        containsUS = medalLeaderBoard.get(i).getCountryName().equals("United States of America") ? true : containsUS;
                    
                        eb.addField(i + 1 + ". "
                            + medalLeaderBoard.get(i).getCountryName()
                            + " (" + medalLeaderBoard.get(i).getPoints()
                            + ")", ":first_place:" + medalLeaderBoard.get(i).getGold()
                            + "\n:second_place:" + medalLeaderBoard.get(i).getSilver()
                            + "\n:third_place:" + medalLeaderBoard.get(i).getBronze(), false);
                    }
                
                    if(!containsUS) {
                        eb.addField("...", "", false);
                        int USIndex = Olympics.findCountryIndex(medalLeaderBoard, "United States of America");
                        OlympicsNationMedalRecord US = medalLeaderBoard.get(USIndex);
                        eb.addField(USIndex + 1 + ". "
                         + "United States of America ("
                         + US.getPoints() +")", ":first_place:"
                         + US.getGold() + "\n:second_place:"
                         + US.getSilver() + "\n:third_place:"
                         + US.getBronze() + "\n\n", false);
                    }
                
                    eb.setFooter("https://olympics.com/en/paris-2024/medals");
                    event.reply("").addEmbeds(eb.build()).queue();
                } catch (Exception e) {
                    event.reply("There was an error retrieving the Olympic Medal Leaderboard. Please try again later.").setEphemeral(true).queue();
                }

                break;
            
            default:
                event.reply("I don't know that command.").setEphemeral(true).queue();
                break;
        }
    }

}
