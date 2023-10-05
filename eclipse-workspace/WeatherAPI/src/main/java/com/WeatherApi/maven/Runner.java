package com.WeatherApi.maven;

public class Runner {

	public static void main(String[] args) throws Exception {

		JsonParser jsonParser = new JsonParser();
		jsonParser.constructAndCallApi(); //construct api request and parse the json response
		jsonParser.checkWeather();
		showMessage();
	}
	

	private static void showMessage() {

		System.out.println(JsonParser.message);
		
	}
}