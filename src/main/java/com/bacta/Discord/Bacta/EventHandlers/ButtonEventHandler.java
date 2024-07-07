package com.bacta.Discord.Bacta.EventHandlers;

import org.jetbrains.annotations.NotNull;

import com.bacta.Discord.DataObjects.DeveloperIDList;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ButtonEventHandler extends ListenerAdapter {


    // button click event handler
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        // switch statement to handle the button click event
        switch(event.getComponentId()) {
            
            case "btnDM":
                // if btnDM is not disabled, disable it and enable btnShare
                // if btnShare is in a disabled state
                if (!event.getMessage().getButtonById("btnDM").isDisabled() && event.getMessage().getButtonById("btnShare").isDisabled()) {
                    // disable btnDM and btnShare
                    event.deferEdit().setActionRow(Button.primary("btnDM", "DM").asDisabled(), Button.success("btnShare", "Share").asDisabled()).queue();
                    // DM the message to the user
                    event.getUser().openPrivateChannel().queue((channel) -> {
                        channel.sendMessage(event.getMessage().getContentRaw()).queue();
                    });
                } else {
                    // disable btnDM and enable btnShare
                    event.deferEdit().setActionRow(Button.primary("btnDM", "DM").asDisabled(), Button.success("btnShare", "Share")).queue();
                    // DM the message to the user
                    event.getUser().openPrivateChannel().queue((channel) -> {
                        channel.sendMessage(event.getMessage().getContentRaw()).queue();
                    });
                }

                // [DEBUG] dm Developer list that someone requested a DM
                analyticsToDevs(event, "**DM**");
            break;

            case "btnShare":
                // if btnShare is not disabled, disable it and enable btnDM
                // if btnDM is in a disabled state
                if (!event.getMessage().getButtonById("btnShare").isDisabled() && event.getMessage().getButtonById("btnDM").isDisabled()) {
                    // disable btnShare and btnDM and post the message to the channel
                    event.deferEdit().setActionRow(Button.primary("btnDM", "DM").asDisabled(), Button.success("btnShare", "Share").asDisabled()).queue();
                    // send the message to the channel
                    event.getChannel().sendMessage(event.getMessage().getContentRaw()).queue();
                } else {
                    // disable btnShare and enable btnDM
                    event.deferEdit().setActionRow(Button.primary("btnDM", "DM"), Button.success("btnShare", "Share").asDisabled()).queue();
                    // send the message to the channel
                    event.getChannel().sendMessage(event.getMessage().getContentRaw() + "\n\n" + event.getUser().getAsMention() + " requested this.").queue();
                }

                // [DEBUG] dm Developer list that someone requested to share the message
                analyticsToDevs(event, "**share**");
            break;
            
            default:
                event.reply("I don't know that button.").queue();
            break;
            
        }
    }
    
    private void analyticsToDevs(@NotNull ButtonInteractionEvent event, String type) {
        // dm all the users in the DeveloperIDList that someone requested to share the message
        for (String id : DeveloperIDList.GetDevIDList()) {
            event.getJDA().getUserById(id).openPrivateChannel().queue((channel) -> {
                channel.sendMessage("User " + event.getUser().getAsMention() + " requested to " + type + " a message: https://discord.com/channels/" + event.getGuild().getId() + "/" + event.getChannel().getId() + "/" + event.getMessageId()).queue();
            });
        }
    }
}
