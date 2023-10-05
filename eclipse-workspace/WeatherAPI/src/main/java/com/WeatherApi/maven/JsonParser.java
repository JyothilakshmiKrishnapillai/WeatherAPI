package com.WeatherApi.maven;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.ParseException;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;



public class JsonParser {

    private static JSONObject jsonobj;
    public static String message;

    public static JSONObject getJsonObject() {
        return jsonobj;
    }

    public void constructAndCallApi() throws Exception {

        String apiurl="https://api.openweathermap.org/data/2.5/forecast?";
        System.out.println("Enter location: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        String location = reader.readLine();
        String apiKey = "d42b2df75cbc7368527a6b3cdb6c6b8d";
        URL url = new URL(apiurl + "q=" + location + "&appid=" + apiKey);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("accept", "application/json");
        int responsecode = conn.getResponseCode();
        parseJsonResponse(responsecode, url);
    }

    private static JSONObject parseJsonResponse(int responsecode, URL aPIURL) throws Exception {
        if (responsecode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responsecode);
        } else {

            String inline = "";
            Scanner scanner = new Scanner(aPIURL.openStream());

            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }
            scanner.close();

            JSONParser parse = new JSONParser();
            jsonobj = (JSONObject) parse.parse(inline);
            return jsonobj;
        }

    }

    private static JSONArray getweatherList() {
        // TODO Auto-generated method stub
        JSONArray weatherlist = (JSONArray) getJsonObject().get("list");
        return weatherlist;
    }

    public static JSONObject getDatesByIndex(int DateIndex) throws Exception {

        JSONArray weatherlist = getweatherList();
        int weatherlistSize = weatherlist.size();
        if (DateIndex >= weatherlistSize) {
            throw new IndexOutOfBoundsException(String.format("There are only %d test suites and we requested for suite index %d, please choose an index from 0 to %d", weatherlistSize, DateIndex, weatherlistSize - 1));
        }
        return (JSONObject) weatherlist.get(DateIndex);
    }
// Trying to get the dates to compare it with current time but date is not fetching

    public void checkWeather() throws Exception {
        // TODO Auto-generated method stub
        ObjectMapper mapper = new ObjectMapper();
        JSONArray weatherlist = getweatherList();
        String weatherExpected = "";
        String weatherMain = "";


        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = formatter.parse(format);
//        System.out.println(formatter.format(currentDate));
        DateTime dt1 = new DateTime(currentDate);
        int hourOfDay = dt1.getHourOfDay();
        MainDto currentMain = new MainDto();
//iterate through the weather list to get the main block by comparing the current time and weather time in api
        for (int i = 0; i < weatherlist.size(); i++) {
            JSONObject json = getDatesByIndex(i);
            String dateFromApiStr = json.get("dt_txt").toString();
            Date dateFromApi = formatter.parse(dateFromApiStr);
            DateTime dt2 = new DateTime(dateFromApi);
            int hourOfDayApi = dt2.getHourOfDay();
            if(hourOfDay == hourOfDayApi){
                String main = json.get("main").toString();
                currentMain = mapper.readValue(main, MainDto.class);
            }
        }

        for (int i = 0; i < weatherlist.size(); i++) {
            JSONObject json = getDatesByIndex(i);
            String dateFromApiStr = json.get("dt_txt").toString();

            JSONArray list = (JSONArray) json.get("weather");
            String weather = list.get(0).toString();
            WeatherDto weatherDto = mapper.readValue(weather, WeatherDto.class);

            String main = json.get("main").toString();
            MainDto mainDto = mapper.readValue(main, MainDto.class);

            try {
                Date dateFromApi = formatter.parse(dateFromApiStr);
                int result = currentDate.compareTo(dateFromApi);
                if (result < 0) {
                    DateTime dt2 = new DateTime(dateFromApi);
                    int timeDiff = new Period(dt2, dt1).getHours();
                    if (timeDiff < 2) {
                        weatherExpected = calculateWeatherExpected(currentMain,mainDto);
                        weatherMain = generateMessage(weatherDto);
                        break;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        message = "weatherExpected = " + weatherExpected + " - " + weatherMain;
//        System.out.println("weatherExpected = " + weatherExpected+ " - " + weatherMain );
    }

    private String generateMessage(WeatherDto weatherDto) {

        String weatherMain = "";
        if(weatherDto.getMain().equalsIgnoreCase("Clouds")){
            weatherMain = "Steady red";
        }
        else if(weatherDto.getMain().equalsIgnoreCase("Rain")){
            weatherMain = "Flashing red";
        }
        else if(weatherDto.getMain().equalsIgnoreCase("Snow")){
            weatherMain = "Flashing white";
        }
        else if(weatherDto.getMain().equalsIgnoreCase("Clear")){
            weatherMain = "Steady green";
        }
        return weatherMain;
    }

    private static String calculateWeatherExpected(MainDto currentMain, MainDto mainDto) {
        String weatherExpected = null;
        if(currentMain.getTemp() > mainDto.getTemp()){
            weatherExpected = "DOWN";
        }
        else if(currentMain.getTemp() < mainDto.getTemp()){
            weatherExpected = "UP";
        }
        else{
            weatherExpected = "SAME";
        }
        return weatherExpected;
    }
}