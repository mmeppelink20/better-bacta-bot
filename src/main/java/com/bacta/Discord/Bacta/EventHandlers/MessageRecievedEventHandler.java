package com.bacta.Discord.Bacta.EventHandlers;

import java.util.ArrayList;

import com.bacta.Discord.DataObjects.DiscordMessage;
import com.bacta.Discord.DataObjects.GuildMessageList;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
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

        if(event.getGuild() == null) {
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
