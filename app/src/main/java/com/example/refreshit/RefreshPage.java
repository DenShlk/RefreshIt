package com.example.refreshit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class RefreshPage {
	public String path;
	public String name;
	public String delay_s;
	Date delay;

	public RefreshPage(String path, String name, Date delay){
		this.path = path;
		this.name = name;
		this.delay = delay;
		delay_s = "15 min";
		//TODO: true delay to string transformation
	}

}
