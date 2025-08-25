package pl.htgmc.htgeconomyapi.gratisy;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Random;

/**
 * Manager "Złotego Tygodnia".
 * Raz w tygodniu losuje nowy mnożnik cen (1%–500%) i zapisuje do goldenweek.json.
 */
public class GoldenWeekManager {
    private double multiplier = 1.0;
    private final double min;
    private final double max;
    private final DayOfWeek changeDay;
    private final File saveFile;
    private LocalDate lastChange = null;

    private final Random random = new Random();

    public GoldenWeekManager(File dataFolder, double min, double max, DayOfWeek changeDay) {
        this.min = min;
        this.max = max;
        this.changeDay = changeDay;

        File gratisyDir = new File(dataFolder, "gratisy");
        if (!gratisyDir.exists()) gratisyDir.mkdirs();

        this.saveFile = new File(gratisyDir, "goldenweek.json");
        load();
    }

    /**
     * Sprawdza, czy trzeba wylosować nowy mnożnik.
     * Wywoływane np. co godzinę przez scheduler.
     */
    public void tick() {
        LocalDate now = LocalDate.now();
        if (lastChange == null || (now.getDayOfWeek() == changeDay && !now.equals(lastChange))) {
            rollNewMultiplier(now);
        }
    }

    /**
     * Losuje nowy mnożnik i zapisuje go do pliku.
     */
    private void rollNewMultiplier(LocalDate now) {
        multiplier = min + (max - min) * random.nextDouble();
        lastChange = now;
        save();
        System.out.println("🎉 Nowy Golden Week! Multiplier = " + String.format("%.2f", multiplier));
    }

    /**
     * Zwraca aktualny mnożnik cen.
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Zapisuje dane do goldenweek.json.
     */
    private void save() {
        try (FileWriter fw = new FileWriter(saveFile)) {
            JSONObject obj = new JSONObject();
            obj.put("multiplier", multiplier);
            obj.put("lastChange", lastChange.toString());
            fw.write(obj.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Wczytuje dane z goldenweek.json.
     */
    private void load() {
        if (!saveFile.exists()) return;
        try {
            String txt = Files.readString(saveFile.toPath());
            JSONObject obj = new JSONObject(txt);
            this.multiplier = obj.getDouble("multiplier");
            this.lastChange = LocalDate.parse(obj.getString("lastChange"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
