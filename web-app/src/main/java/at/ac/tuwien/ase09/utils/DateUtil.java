package at.ac.tuwien.ase09.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class DateUtil {
  
	public static String formatDate(Date date, String pattern) {
		if (date == null || pattern == null) {
			return "";
		}
		return new SimpleDateFormat(pattern).format(date);
	}
	
	public static String formatDate(Date date) {
		return DateUtil.formatDate(date, "dd.MM.yyyy");
	}
	
	public static String formatDateWithTime(Date date) {
		return DateUtil.formatDate(date, "dd.MM.yyyy - hh:mm:ss");
	}
	
	public static String formatCalendar(Calendar calendar, String pattern) {
		return DateUtil.formatDate(calendar.getTime(), pattern);
	}
	
	public static String formatCalendar(Calendar calendar) {
		return DateUtil.formatDate(calendar.getTime());
	}
	
	public static String formatCalendarWithTime(Calendar calendar) {
		return DateUtil.formatDateWithTime(calendar.getTime());
	}
}
