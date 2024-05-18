package com.bacta.Discord.Bacta;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandSetup {

    public CommandSetup(JDA bot) {
        OptionData option1 = new OptionData(OptionType.STRING, "message", "The message to send.").setRequired(true);
        OptionData option2 = new OptionData(OptionType.STRING, "question", "The question to ask bacta bot.").setRequired(true);
        OptionData option3 = new OptionData(OptionType.STRING, "messagestats", "The message to count characters, spaces, and words.").setRequired(true);

        bot.upsertCommand("summarize", "Send a message to be summarized").setGuildOnly(true).queue();
        bot.upsertCommand("send-message", "Send a message to the channel.").addOptions(option1).setGuildOnly(true).queue();
        bot.upsertCommand("ping", "Pong!").setGuildOnly(true).queue();
        bot.upsertCommand("shutdown", "Shut down Bacta Bot.").setGuildOnly(true).queue();
        bot.upsertCommand("clear-messages", "clear the messages in Bacta Bot").setGuildOnly(true).queue();
        bot.upsertCommand("question", "ask bacta bot a question").addOptions(option2).setGuildOnly(true).queue();
        bot.upsertCommand("bacta", "bacta, or no bacta...").setGuildOnly(true).queue();
        bot.upsertCommand("vanish", "vanish...").setGuildOnly(true).queue();
        bot.upsertCommand("charactercounter", "supply a message to see how many spaces, characters, and words it has").addOptions(option3).setGuildOnly(true).queue();
    }
    
}
