package com.meppelink;

import com.meppelink.Discord.DiscordBot;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyServletContextListener implements ServletContextListener {
    private DiscordBot bot;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("*****************************\n*****************************\n*****************************\n         Starting bot\n*****************************\n*****************************\n*****************************\n");
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");
        JDA bot = JDABuilder.createDefault(token).setActivity(Activity.playing("in the bacta pod")).enableIntents(GatewayIntent.MESSAGE_CONTENT).build();
        bot.addEventListener(new DiscordBot());
        sce.getServletContext().setAttribute("discordBot", bot);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("*****************************\n*****************************\n*****************************\n     Shutting down bot\n*****************************\n*****************************\n*****************************\n");
        JDA bot = (JDA) sce.getServletContext().getAttribute("discordBot");
        System.out.println(bot);
        if (bot != null) {
            bot.shutdownNow();
        }
    }
}