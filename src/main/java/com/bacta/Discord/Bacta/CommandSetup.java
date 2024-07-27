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
        OptionData option2 = new OptionData(OptionType.STRING, "question", "The question to ask bacta bot.").setRequired(true);

        bot.upsertCommand("summarize", "summarizes the current conversation").queue();
        bot.upsertCommand("ping", "Pong!").queue();
        bot.upsertCommand("question", "ask bacta bot a question").addOptions(option2).queue();
        bot.upsertCommand("bacta", "bacta, or no bacta...").queue();
        bot.upsertCommand("olympics", "Olypmics gold medal leaderboard").queue();

        // print the name and IDS of all the commands
        bot.retrieveCommands().queue((commands) -> {
            commands.forEach((command) -> {
                System.out.println("Command Name: " + command.getName() + " Command ID: " + command.getId());
            });
        });

        // bot.deleteCommandById("1266755438230110389").queue();
        // bot.deleteCommandById("1240858500007985222").queue();
        // bot.deleteCommandById("1240858583432822824").queue();
        // bot.deleteCommandById("1240858588130443386").queue();

        return bot;
    }
}
