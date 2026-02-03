package my.project;

import com.batoulapps.adhan.*;
import com.batoulapps.adhan.CalculationParameters.*;
import com.batoulapps.adhan.data.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

public class Main {
    static final File TEMPFILE =new File("/tmp/prayerTime.json");
    public static void main(String[] args) {
        creatTempLocation();
        double [] loc = new double[2];
        try(Scanner sc = new Scanner(TEMPFILE);){
            loc[0] = Double.parseDouble(sc.next());
            loc[1] = Double.parseDouble(sc.next());
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
        Coordinates coordinates = new Coordinates(loc[0] ,loc[1] );
        ZoneId zone = ZoneId.of("Africa/Cairo");
        ZonedDateTime nowZoned = ZonedDateTime.now(zone);
        DateComponents dateComponents = new DateComponents(nowZoned.getYear(), nowZoned.getMonthValue(), nowZoned.getDayOfMonth());

        CalculationParameters params = CalculationMethod.EGYPTIAN.getParameters();
        params.madhab = Madhab.SHAFI;
        params.adjustments.fajr = -1;
        params.adjustments.dhuhr=-2;
        params.adjustments.asr=-1;
        params.adjustments.isha = -1;

        PrayerTimes prayerTimes = new PrayerTimes(coordinates, dateComponents, params);

        Prayer next = prayerTimes.nextPrayer();
        Instant nextTime;

        if (next == Prayer.NONE ) {
            PrayerTimes tomorrowTimes;
            tomorrowTimes = new PrayerTimes(coordinates,
                    new DateComponents(nowZoned.plusDays(1).getYear(),nowZoned.plusDays(1).getMonthValue(),nowZoned.plusDays(1).getDayOfMonth()),params);
            next = Prayer.FAJR;
            nextTime = tomorrowTimes.fajr.toInstant();
        } else {
            nextTime = prayerTimes.timeForPrayer(next).toInstant();
        }

        // حساب المتبقي بدقة
        Duration remaining = Duration.between(Instant.now(), nextTime);
        long totalMinutes = remaining.toMinutes();
        String timeStr = String.format("%02d:%02d", totalMinutes / 60, totalMinutes % 60);

        // تنسيق الأوقات للقائمة
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");


        String tooltip = String.format(
                "Fajr:    %s\nSunrise: %s\nDhuhr:   %s\nAsr:     %s\nMaghrib: %s\nIsha:    %s",
                formatTime(prayerTimes.fajr.toInstant(), zone, formatter),
                formatTime(prayerTimes.sunrise.toInstant(), zone,formatter),
                formatTime(prayerTimes.dhuhr.toInstant(), zone, formatter),
                formatTime(prayerTimes.asr.toInstant(), zone, formatter),
                formatTime(prayerTimes.maghrib.toInstant(), zone, formatter),
                formatTime(prayerTimes.isha.toInstant(), zone, formatter)
        );
        System.out.println(new Gson().toJson(Map.of(
                "text", next.name() + " " + timeStr,
                "tooltip", tooltip
        )));
    }

    private static String formatTime(Instant instant, ZoneId zone, DateTimeFormatter f) {
        return instant.atZone(zone).format(f);
    }
    private static double[] getLocation(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://ip-api.com/json"))
                .build();
        String response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();

        return new double[]{json.get("lat").getAsDouble(), json.get("lon").getAsDouble()};
    }
    private static void creatTempLocation(){
        if(!TEMPFILE.exists()){
            try(FileWriter fr = new FileWriter(TEMPFILE);){
                double [] loc = getLocation();
                fr.write(loc[0]+" "+loc[1]);
            } catch (IOException fne){
                fne.printStackTrace();
            }

        }
    }
}