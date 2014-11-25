package at.ac.tuwien.ase09.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class DateUtil {
  
	public static String formatDate(String pattern, Date date) {
		if (date == null || pattern == null) {
			return "";
		}
		return new SimpleDateFormat(pattern).format(date);
	}
	
	public static String formatDate(Date date) {
		return DateUtil.formatDate("dd.MM.yyyy", date);
	}
	
	public static String formatDateWithTime(Date date) {
		return DateUtil.formatDate("dd.MM.yyyy - hh:mm:ss", date);
	}
}
