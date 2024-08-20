package com.bacta.Discord.DataObjects;

import java.util.ArrayList;
import java.util.Arrays;

public class BactaData {
    private static ArrayList<String> developerIDList = new ArrayList<String>(Arrays.asList("197944571844362240"));
    private static String botID = "1095791282531610655";
    private static String askQuestionAboutConversationGPTModel = "gpt-4o";

    public static ArrayList<String> GetDevIDList() {
        return developerIDList;
    }

    public static boolean AddDevID(String id) {
        boolean result = true;
        try {
            developerIDList.add(id);
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    public static boolean RemoveDevID(String id) {
        boolean result = true;

        try {
            developerIDList.remove(id);
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    public static String getBotID() {
        return botID;
    }

    public static String GetBotAsMention(){
        return "<@" + botID + ">";
    }

    public static String getAskQuestionAboutConversationGPTModel() {
        return askQuestionAboutConversationGPTModel;
    }


}
