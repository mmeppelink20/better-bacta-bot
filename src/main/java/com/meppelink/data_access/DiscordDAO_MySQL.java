package com.meppelink.data_access;

import com.meppelink.Discord.DiscordMessage;
import com.meppelink.User.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;



public class DiscordDAO_MySQL implements DAO_MySQL<HashMap<String, Object>> {
    public int addDiscordUser(HashMap<String, String> discordUser) {
        int numRowsAffected = 0;
        try (Connection connection = getConnection()) {
            if (connection.isValid(2)) {
                CallableStatement callableStatement = connection.prepareCall("{CALL sp_add_discord_user(?,?,?,?,?,?)}");
                callableStatement.setString(1, discordUser.get("user_id"));
                callableStatement.setString(2, discordUser.get("user_name"));
                callableStatement.setString(3, discordUser.get("user_avatar"));
                callableStatement.setString(4, discordUser.get("user_discriminator"));
                callableStatement.setString(5, discordUser.get("user_mention"));
                callableStatement.setBoolean(6, Boolean.parseBoolean(discordUser.get("is_bot")));
                numRowsAffected = callableStatement.executeUpdate();
                callableStatement.close();
            }
        } catch (SQLException e) {
            System.out.println("Add discord user failed");
            System.out.println(e.getMessage());
        }
        return numRowsAffected;
    }

    public int addDiscordMessage(HashMap<String, String> discordMessage, ZonedDateTime zonedDateTime) {
        int numRowsAffected = 0;
        try (Connection connection = getConnection()) {
            if (connection.isValid(2)) {
                CallableStatement callableStatement = connection.prepareCall("{CALL sp_add_discord_message(?,?,?,?,?,?,?,?,?,?)}");
                callableStatement.setString(1, discordMessage.get("user_name"));
                callableStatement.setString(2, discordMessage.get("user_id"));
                callableStatement.setString(3, discordMessage.get("server_name"));
                callableStatement.setString(4, discordMessage.get("server_id"));
                callableStatement.setString(5, discordMessage.get("channel_name"));
                callableStatement.setString(6, discordMessage.get("channel_id"));
                callableStatement.setString(7, discordMessage.get("message"));
                callableStatement.setString(8, discordMessage.get("message_id"));
                System.out.println();
                callableStatement.setTimestamp(9, Timestamp.valueOf(zonedDateTime.toLocalDateTime()));
                callableStatement.setString(10, discordMessage.get("message_link"));
                numRowsAffected = callableStatement.executeUpdate();
                callableStatement.close();
            }
        } catch (SQLException e) {
            System.out.println("Add discord message failed");
            System.out.println(e.getMessage());
        }
        return numRowsAffected;
    }

    public int selectDiscordUserByUserID(String userID) {
        int rowCount = 0;
        try (Connection connection = getConnection()) {
            if (connection.isValid(2)) {
                CallableStatement statement = connection.prepareCall("{CALL sp_select_discord_user_by_user_id(?, ?)}");
                statement.setString(1, userID);
                statement.registerOutParameter(2, Types.INTEGER);
                statement.execute();
                rowCount = statement.getInt(2);
            }
        } catch (SQLException e) {
            System.out.println("Select discord user by user id failed");
            System.out.println(e.getMessage());
        }
        return rowCount;
    }

    public ArrayList<String> selectAllDiscordUsersInServer(String serverID) {
        ArrayList<String> userList = new ArrayList<>();

        return userList;
    }

    public ArrayList<DiscordMessage> selectAllMessages() {
        ArrayList<DiscordMessage> discordMessages = new ArrayList<>();
        try(Connection connection = getConnection()) {

            CallableStatement cs = connection.prepareCall("{CALL sp_get_all_discord_messages()}");
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                String userName = rs.getString("userName");
                String userID = rs.getString("userID");
                String serverName = rs.getString("serverName");
                String serverID = rs.getString("serverID");
                String channelName = rs.getString("channelName");
                String channelID = rs.getString("channelID");
                String message = rs.getString("message");
                String messageID = rs.getString("messageID");
                String messageTime = rs.getString("messageTime");
                String messageLink = rs.getString("messageLink");

                DiscordMessage discordMessage = new DiscordMessage(userName, userID, serverName, serverID, channelName, channelID, message, messageID, messageTime, messageLink);
                discordMessages.add(discordMessage);
            }

            rs.close();
            cs.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return discordMessages;
    }

}



