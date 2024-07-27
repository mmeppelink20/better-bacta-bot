package com.bacta.Discord.Bacta.EventHandlers;

import java.util.ArrayList;

import com.bacta.Discord.DataObjects.DeveloperIDList;
import com.bacta.Discord.DataObjects.DiscordMessage;
import com.bacta.Discord.DataObjects.GuildMessageList;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageRecievedEventHandler extends ListenerAdapter {

    private final GuildMessageList guildMessageList;
    private final int charLimit;

    

    public MessageRecievedEventHandler(GuildMessageList guildMessageList, int charLimit) {
        this.guildMessageList = guildMessageList;
        this.charLimit = charLimit;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) {
            return;
        }

        // check if the event is from a guild
        if(event.isFromGuild()) {
            System.out.println("DEBUG: *** Message from guild: " + event.getGuild().getName() + " ***");
        } else {
            System.out.println("DEBUG: *** Message from private channel ***" + event.getAuthor().getAsMention() + " ***");
            return;
        }
    
        // if user is equal to "197944571844362240" then shut down the bot and send a dm to the user and the message is equal to "!shutdown" send a dm to the user with what guild and channel the bot is shutting down from
        for (String id : DeveloperIDList.GetDevIDList()) {
            if (id.equals(event.getAuthor().getId()) && event.getMessage().getContentRaw().equals("!shutdown")) {
                event.getAuthor().openPrivateChannel().queue((channel) -> {
                    channel.sendMessage(" Shutting down from " + event.getGuild().getName() + " in " + event.getChannel().getAsMention() + " requested by: " + event.getAuthor().getAsMention()).queue();
                });
                event.getJDA().shutdown();
                return;
            }
        }

        // if the user is equal to "197944571844362240" then clear the messages in the channel and send a dm to the user with what guild and channel the messages were cleared from
        if(DeveloperIDList.GetDevIDList().stream().anyMatch(id -> id.equals(event.getAuthor().getId())) && event.getMessage().getContentRaw().equals("!clear")) {
            guildMessageList.clearMessages(event);
            event.getAuthor().openPrivateChannel().queue((channel) -> {
                channel.sendMessage("Messages cleared from " + event.getGuild() + " in " + event.getChannel().getAsMention() + " requested by: " + event.getAuthor().getAsMention()).queue();
            });
            return;
        }

        if(!guildMessageList.guildInMap(event.getGuild().getId())) {
            guildMessageList.addGuildToMap(event.getGuild().getId());
            System.out.println("DEBUG: *** Added guild to map: " + event.getGuild().getId() + " ***");
        }

        if(!guildMessageList.getGuildChannelMap().get(event.getGuild().getId()).contains(event.getChannel().getId())) {
            guildMessageList.addChannelToGuild(event.getGuild().getId(), event.getChannel().getId());
            System.out.println("DEBUG: *** Added channel to guild: " + event.getChannel().getId() + " ***");
        }

        if(!guildMessageList.channelInMap(event.getChannel().getId())) {
            guildMessageList.addChannelToMap(event.getChannel().getId());
            System.out.println("DEBUG: *** Added channel to guild channel map: " + event.getChannel().getId() + " ***");
        }

        try {
            DiscordMessage message = new DiscordMessage(
                    event.getAuthor().getName(),
                    event.getMessage().getContentRaw(),
                    event.getMessage().getTimeCreated().toInstant(),
                    event.getGuild(),
                    event.getChannel());

            int maxSingleMessageLength = 1000; // this can't be changed due to limitations in Discord's API
            if(message.toString().length() > maxSingleMessageLength) {
                ArrayList<DiscordMessage> splitMessages = splitMessage(event);
                guildMessageList.addMessageToChannel(event.getChannel().getId(), splitMessages.get(0));
                guildMessageList.addMessageToChannel(event.getChannel().getId(), splitMessages.get(1));
            } else {
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

    private ArrayList<DiscordMessage> splitMessage(MessageReceivedEvent event) {
        ArrayList<DiscordMessage> splitMessages = new ArrayList<>();

        String messageString2 = event.getMessage().getContentRaw().substring(0, event.getMessage().getContentRaw().length() / 2);
        String messageString3 = event.getMessage().getContentRaw().substring(event.getMessage().getContentRaw().length() / 2);

        DiscordMessage discordMessage2 = new DiscordMessage(event.getAuthor().getName(), "[1/2] " + messageString2, event.getMessage().getTimeCreated().toInstant(), event.getGuild(), event.getChannel());
        DiscordMessage discordMessage3 = new DiscordMessage(event.getAuthor().getName(), "[2/2] " + messageString3, event.getMessage().getTimeCreated().toInstant(), event.getGuild(), event.getChannel());

        System.out.println("DEBUG: \n" + discordMessage2.toString().length() + "\n");
        System.out.println("DEBUG: \n" + discordMessage3.toString().length() + "\n");

        splitMessages.add(discordMessage2);
        splitMessages.add(discordMessage3);

        return splitMessages;
    }

    public String eventToString(MessageReceivedEvent event) {
        String formattedTime = DiscordMessage.formatTime(event.getMessage().getTimeCreated().toInstant());

        String formattedEvent = "    NAME: " + event.getAuthor().getName() +
                "\n MESSAGE: " + event.getMessage().getContentRaw() +
                "\n    TIME: " + formattedTime +
                "\n   GUILD: " + event.getGuild().getName() + " ID: " + event.getGuild().getId() +
                "\n CHANNEL: " + event.getChannel().getName() + " ID: " + event.getChannel().getId() +
                (event.getMessage().getStickers().isEmpty() ? "" : "\n STICKER: " + event.getMessage().getStickers().get(0).getName()) +
                "\n CHARCNT: " + guildMessageList.getCharCountPerChannel(event.getChannel().getId());

        return formattedEvent;
    }

    private void removeMessagesUntilUnderLimit(MessageReceivedEvent event, int charLimit) {
        while(guildMessageList.getCharCountPerChannel(event.getChannel().getId()) > charLimit) {
            guildMessageList.removeMessageFromChannel(event);
        }
    }

    public static void sendAMessage(String message, Guild guild, Channel channel) {
        guild.getTextChannelById(channel.getId()).sendMessage(message).queue();
    }

}
