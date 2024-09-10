package com.bacta.Olympics;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import com.bacta.Olympics.DataObjects.OlympicsNationMedalRecord;

public class Olympics {

  public static int findCountryIndex(ArrayList<OlympicsNationMedalRecord> medalLeaderBoard, String country) {
    int index = -1;
    for (int i = 0; i < medalLeaderBoard.size(); i++) {
      if (medalLeaderBoard.get(i).getCountryName().toLowerCase().equals(country.toLowerCase())) {
        index = i;
      }
    }

    return index;
  }

  public static ArrayList<OlympicsNationMedalRecord> GetMedalLeaderBoard() {
    ArrayList<OlympicsNationMedalRecord> medalLeaderBoard = new ArrayList<OlympicsNationMedalRecord>();

    String url = "https://olympics.com/en/paris-2024/medals";

      try {
        Document doc = Jsoup.connect(url).get();

        // Select the parent div containing the medal data
        Element medalTable = doc.selectFirst("div[data-test-id=virtuoso-item-list]");

        // Loop through each child div with data-index attribute
        for (Element countryElement : medalTable.select("div[data-index]")) {
            Elements nameElements = countryElement.select("span.emotion-srm-uu3d5n");
            if (!nameElements.isEmpty()) {
                String name = nameElements.text();
                String flagUrl = countryElement.select("img.elhe7kv3").attr("src"); // Access the image source
                Elements medals = countryElement.select("span.e1oix8v91.emotion-srm-81g9w1");
                if (medals.size() >= 3) { // Check if there are at least 3 medals
                  OlympicsNationMedalRecord olympicRecord = 
                      new OlympicsNationMedalRecord(
                      name,
                      flagUrl,
                      Integer.parseInt(medals.get(0).text()),
                      Integer.parseInt(medals.get(1).text()),
                      Integer.parseInt(medals.get(2).text()),
                      Integer.parseInt(countryElement.select("span.emotion-srm-5nhv3o").text())
                    );
                  medalLeaderBoard.add(olympicRecord);
                } else {
                    System.out.println("Incomplete medal data for " + name);
                }
                
            }
        }
      } catch (IOException e) {
          e.printStackTrace();
      } catch (Exception e) {
          e.printStackTrace();
      }

      return medalLeaderBoard;
  }
}
