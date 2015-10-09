package br.com.vsgdev.leilaodojo.utils;

import java.util.Calendar;

import br.com.vsgdev.leilaodojo.models.Auction;


public class DateTimeUtils {

    public static String calendar2PortugueseDateFormat(final Calendar calendar) {
        final StringBuilder builder = new StringBuilder();
        builder.append(calendar.get(Calendar.DAY_OF_MONTH));
        builder.append("/");
        builder.append(calendar.get(Calendar.MONTH) + 1);
        builder.append("/");
        builder.append(calendar.get(Calendar.YEAR));

        return builder.toString();
    }

    public static String getEndAtRemainingTime(final Auction auction) {
        final Calendar now = Calendar.getInstance();
        int totalHours = 0;
        final int remainingDays = auction.getEndAt().get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);

        if (remainingDays == 0) {
            //same day
            totalHours = auction.getEndAt().get(Calendar.HOUR_OF_DAY) - now.get(Calendar.HOUR_OF_DAY);
        }
        if (remainingDays == 1) {
            totalHours = 24 - now.get(Calendar.HOUR_OF_DAY);
            totalHours += auction.getEndAt().get(Calendar.HOUR_OF_DAY);
        }
        if (remainingDays > 1) {
            totalHours = 24 - now.get(Calendar.HOUR_OF_DAY);

            totalHours += 24 * (remainingDays - 1);

            totalHours += auction.getEndAt().get(Calendar.HOUR_OF_DAY);
        }

        return String.valueOf(totalHours);
    }

    public static Calendar strToCalendar(final String dateInStr) {
        if (dateInStr.isEmpty()) {
            return null;
        }
        final Calendar cal = Calendar.getInstance();
        final String[] fields = dateInStr.split("T");
        final String[] date = fields[0].split("-");
        final String[] time = fields[1].split(":");

        cal.set(Calendar.YEAR, Integer.parseInt(date[0]));
        cal.set(Calendar.MONTH, (Integer.parseInt(date[1])) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[2]));

        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        cal.set(Calendar.SECOND, Integer.parseInt(time[2]));

        return cal;
    }

    /**
     * Com base em um objeto Calendar, retorna a um dia e horário no formato: YYYY-MM-DDTHH:mm:ss
     *
     * @param calendar
     * @return
     */
    public static String convertCalendar(final Calendar calendar) {
        final StringBuilder dateBuilder = new StringBuilder();
        dateBuilder.append(calendar.get(Calendar.YEAR));
        dateBuilder.append("-");
        dateBuilder.append(calendar.get(Calendar.MONTH) + 1);
        dateBuilder.append("-");
        dateBuilder.append(calendar.get(Calendar.DAY_OF_MONTH));
        dateBuilder.append("T");
        dateBuilder.append(calendar.get(Calendar.HOUR_OF_DAY));
        dateBuilder.append(":");
        dateBuilder.append(calendar.get(Calendar.MINUTE));
        dateBuilder.append(":");
        dateBuilder.append(calendar.get(Calendar.SECOND));
        return dateBuilder.toString();
    }
}