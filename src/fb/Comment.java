package fb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Comment {

	public String text, time, appName, appVersion, event, rating, store;
	public double[] vector;
	
	public Comment(String s) {
		String arr[] = s.split(Pattern.quote("\t"));
		this.text = arr[0];
		this.time = arr[1];
		this.appName = arr[2];
		this.appVersion = arr[3];
		this.event = arr[4];
		this.rating = arr[5];
		this.store = arr[6];
		this.vector = new double[] {};
	}
	
	public static void printTime(String t) {
		long unixSeconds = Long.parseLong(t);
		Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
		String formattedDate = sdf.format(date);
		System.out.println(formattedDate);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Text:\t" + text + "\n");
		sb.append("Time:\t" + time + "\n");
		sb.append("App Name:\t" + appName + "\n");
		sb.append("App Version:\t" + appVersion + "\n");
		sb.append("Event:\t" + event + "\n");
		sb.append("Rating:\t" + rating + "\n");
		sb.append("Store:\t" + store + "\n");
		return sb.toString();
	}

}
