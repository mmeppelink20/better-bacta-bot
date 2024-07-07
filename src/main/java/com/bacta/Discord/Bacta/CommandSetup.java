package com.bacta.Discord.Bacta;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandSetup {

    private final JDA bot;

    public CommandSetup(JDA bot) {
        this.bot = bot;
    }

    public JDA setupCommands() {
        OptionData option1 = new OptionData(OptionType.STRING, "message", "The message to send.").setRequired(true);
        OptionData option2 = new OptionData(OptionType.STRING, "question", "The question to ask bacta bot.").setRequired(true);
        OptionData option3 = new OptionData(OptionType.STRING, "messagestats", "The message to count characters, spaces, and words.").setRequired(true);

        bot.upsertCommand("summarize", "summarizes the current conversation").setGuildOnly(true).queue();
        // bot.upsertCommand("send-message", "Send a message to the channel.").addOptions(option1).setGuildOnly(true).queue();
        bot.upsertCommand("ping", "Pong!").setGuildOnly(true).queue();
        // bot.upsertCommand("shutdown", "Shut down Bacta Bot.").setGuildOnly(true).queue();
        // bot.upsertCommand("clear-messages", "clear the messages in Bacta Bot").setGuildOnly(true).queue();
        bot.upsertCommand("question", "ask bacta bot a question").addOptions(option2).setGuildOnly(true).queue();
        bot.upsertCommand("bacta", "bacta, or no bacta...").setGuildOnly(true).queue();
        // bot.upsertCommand("vanish", "vanish...").setGuildOnly(true).queue();
        // bot.upsertCommand("charactercounter", "supply a message to see how many spaces, characters, and words it has").addOptions(option3).setGuildOnly(true).queue();

        // print the name and IDS of all the commands
        bot.retrieveCommands().queue((commands) -> {
            commands.forEach((command) -> {
                System.out.println("Command Name: " + command.getName() + " Command ID: " + command.getId());
            });
        });

        // bot.deleteCommandById("1257756950498639892").queue();
        // bot.deleteCommandById("1257757025975144481").queue();
        // bot.deleteCommandById("1257757028085010466").queue();
        // bot.deleteCommandById("1162531134937256028").queue();
        // bot.deleteCommandById("1225991886549024799").queue();

        return bot;
    }
}
