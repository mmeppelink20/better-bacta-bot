# java3-final-project | Bacta Bot (Discord Bot)
Final project for Java 3

# Introduction

This is a Java Discord bot program created using [JDA](https://github.com/DV8FromTheWorld/JDA), and is complete with a JSP front-end based on MVC architecture. This bot's primary feature is to read in
messages from the a Discord server that it's in, and add them to an Azure MySQL database. The bot will also add Discord users to the database.
While the bot's primary feature is to put messages into a database, it also has some other basic features, such as: saying "!ping" will prompt
Bacta Bot to reply with "pong!", and if your message contains the word "bacta", Bacta Bot will send you a DM saying "Have a bacta".

# Installation Instructions

In order to use this program with the web frontent, you will need to first download Apache Tomcat, and an IDE of your choice;
This particular program was created in Intellij. Import the project into your IDE and then select Apache Tomcat as a run configuration. 
This should allow you to start the program. Or if you'd like to expedite this entire process, you can go directly to the Azure hosted website here: https://matthew-final-project.azurewebsites.net/ 

# How to use

The website does not have much functionality for non-logged in users, so you will need to create an accout and login.
Once you have done so, you will have access to 3 main features: Viewing the messages that Bacta Bot has read into its database, Viewing Discord users that
Bacta Bot has added to its database, and the ability to chat on the website through [WidgetBot](https://widgetbot.io/). You can certainly chat from the
website, but I'd encourage you to join the Discord server directly, which you can do from the website, and chat directly from Discord's client/website. 
The ability to view discord messages and view discord users is restricted to only those who are logged in, no special permissions are needed other than 
needing to be logged in.

thank you for your interest in Bacta Bot!
