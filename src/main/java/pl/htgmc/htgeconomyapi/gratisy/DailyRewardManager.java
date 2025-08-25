package pl.htgmc.htgeconomyapi.gratisy;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDate;

/**
 * Manager nagród dziennych dla graczy.
 * Każdy dzień streaka = +100 HTG, do maksymalnie 10000 HTG (100 dni).
 * Jeśli gracz opuści dzień – streak resetuje się do 1.
 */
public class DailyRewardManager {
    private final File saveFile;
    private JSONObject data;

    public DailyRewardManager(File dataFolder) {
        File gratisyDir = new File(dataFolder, "gratisy");
        if (!gratisyDir.exists()) gratisyDir.mkdirs();
        this.saveFile = new File(gratisyDir, "daily.json");
        load();
    }

    /**
     * Próbuje odebrać dzienną nagrodę dla gracza.
     * @param player nazwa gracza
     * @return wysokość nagrody, -1 jeśli już odebrana dzisiaj
     */
    public int claimReward(String player) {
        LocalDate today = LocalDate.now();
        JSONObject obj = data.optJSONObject(player);
        int streak = 1;

        if (obj != null) {
            LocalDate last = LocalDate.parse(obj.getString("lastClaim"));
            if (last.plusDays(1).isEqual(today)) {
                streak = obj.getInt("streak") + 1; // ciągłość
            } else if (last.isEqual(today)) {
                return -1; // nagroda już odebrana dzisiaj
            } else {
                streak = 1; // przerwana seria
            }
        }

        int reward = calculateReward(streak);

        JSONObject newObj = new JSONObject();
        newObj.put("lastClaim", today.toString());
        newObj.put("streak", streak);
        data.put(player, newObj);
        save();

        return reward;
    }

    /**
     * Wylicza nagrodę na podstawie streaka.
     * @param streak liczba dni ciągiem
     * @return kwota nagrody
     */
    private int calculateReward(int streak) {
        return Math.min(streak * 100, 10000);
    }

    /**
     * Zwraca aktualny streak gracza.
     */
    public int getStreak(String player) {
        JSONObject obj = data.optJSONObject(player);
        return obj != null ? obj.getInt("streak") : 0;
    }

    private void load() {
        try {
            if (!saveFile.exists()) {
                data = new JSONObject();
                return;
            }
            String txt = Files.readString(saveFile.toPath());
            data = new JSONObject(txt);
        } catch (Exception e) {
            data = new JSONObject();
        }
    }

    private void save() {
        try (FileWriter fw = new FileWriter(saveFile)) {
            fw.write(data.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
