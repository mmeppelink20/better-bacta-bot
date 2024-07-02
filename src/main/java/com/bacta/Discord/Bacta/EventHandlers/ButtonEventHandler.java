package com.bacta.Discord.Bacta.EventHandlers;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ButtonEventHandler extends ListenerAdapter {


    // button click event handler
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        // switch statement to handle the button click event
        System.out.println(event.getMessage().getButtonById("btnDM").isDisabled());

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
                System.out.println(event.getUser().getName() + " requested a DM.");
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
                System.out.println(event.getUser().getName() + " requested to share the message.");
            break;
            
            default:
                event.reply("I don't know that button.").queue();
            break;
            
        }
    }
    
}
