package com.WeatherApi.maven;

import lombok.Data;

//@Data
public class WeatherDto {

    public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMain() {
		return main;
	}
	public void setMain(String main) {
		this.main = main;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	String icon;
    String description;
    String main;
    String id;
}
