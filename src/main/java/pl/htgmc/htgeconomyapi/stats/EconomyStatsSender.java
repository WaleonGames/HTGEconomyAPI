package pl.htgmc.htgeconomyapi.stats;

import com.google.gson.*;
import pl.htgmc.htgeconomyapi.HTGEconomyAPI;
import pl.htgmc.htgeconomyapi.analysis.WealthAnalyzer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class EconomyStatsSender {

    private static final String WEBHOOK = "https://discord.com/api/webhooks/1388222972375859231/NOAgvCJns78C3Qs_JRFuMtcSreHklVO8N2PI0jYD1jc2W3eKQ4-D2Cz0nkctZX6ekiRW";
    private static final int MAX_HISTORY = 50;
    private static final Path STATS_FILE = Paths.get(HTGEconomyAPI.getInstance().getDataFolder().getPath(), "economy_stats.json");

    private static final List<Snapshot> history = new ArrayList<>();
    private static Snapshot lastSnapshot = null;

    public static void sendStats() {
        Snapshot current = Snapshot.create();
        if (lastSnapshot != null && current.equals(lastSnapshot)) {
            System.out.println("[StatsSender] ⏸️ Nic się nie zmieniło, pomijam.");
            return;
        }

        lastSnapshot = current;
        history.add(current);
        if (history.size() > MAX_HISTORY) {
            history.remove(0);
        }

        saveHistory();  // Zapis do pliku

        String chartUrl = buildChartUrl();
        String content = String.format("""
                📊 **Nowe dane ekonomii**
                💰 Suma: %.2f
                📈 Średnia: %.2f
                ⚙️ Mnożnik: x%.2f
                """, current.total, current.average, current.multiplier);

        sendToDiscord(content, chartUrl);
    }

    public static void loadHistory() {
        if (!Files.exists(STATS_FILE)) return;
        try (Reader reader = Files.newBufferedReader(STATS_FILE)) {
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement el : array) {
                JsonObject obj = el.getAsJsonObject();
                Snapshot snap = new Snapshot(
                        obj.get("total").getAsDouble(),
                        obj.get("average").getAsDouble(),
                        obj.get("multiplier").getAsDouble()
                );
                history.add(snap);
                lastSnapshot = snap;
            }
            System.out.println("[StatsSender] 📂 Wczytano historię ekonomii (" + history.size() + ")");
        } catch (Exception e) {
            System.err.println("[StatsSender] ❌ Błąd wczytywania historii: " + e.getMessage());
        }
    }

    private static void saveHistory() {
        try (Writer writer = Files.newBufferedWriter(STATS_FILE, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            JsonArray array = new JsonArray();
            for (Snapshot snap : history) {
                JsonObject obj = new JsonObject();
                obj.addProperty("total", snap.total);
                obj.addProperty("average", snap.average);
                obj.addProperty("multiplier", snap.multiplier);
                array.add(obj);
            }
            new GsonBuilder().setPrettyPrinting().create().toJson(array, writer);
        } catch (Exception e) {
            System.err.println("[StatsSender] ❌ Błąd zapisu historii: " + e.getMessage());
        }
    }

    private static String buildChartUrl() {
        String labels = "[" + String.join(",", Collections.nCopies(history.size(), "\"\"")) + "]";
        String total = history.stream().map(s -> String.format(Locale.US, "%.2f", s.total)).reduce((a, b) -> a + "," + b).orElse("");
        String avg = history.stream().map(s -> String.format(Locale.US, "%.2f", s.average)).reduce((a, b) -> a + "," + b).orElse("");
        String mult = history.stream().map(s -> String.format(Locale.US, "%.2f", s.multiplier)).reduce((a, b) -> a + "," + b).orElse("");

        String config = """
    {
      type: 'line',
      data: {
        labels: %s,
        datasets: [
          {
            label: 'Suma',
            borderColor: 'white',
            backgroundColor: 'white',
            fill: false,
            data: [%s]
          },
          {
            label: 'Średnia',
            borderColor: 'white',
            backgroundColor: 'white',
            fill: false,
            data: [%s]
          },
          {
            label: 'Mnożnik',
            borderColor: 'white',
            backgroundColor: 'white',
            fill: false,
            data: [%s]
          }
        ]
      },
      options: {
        plugins: {
          legend: {
            labels: {
              color: 'white'
            }
          },
          title: {
            display: true,
            text: '📈 Historia ekonomii',
            color: 'white'
          }
        },
        scales: {
          x: {
            ticks: {
              color: 'white'
            },
            grid: {
              color: 'rgba(255,255,255,0.2)'
            }
          },
          y: {
            ticks: {
              color: 'white'
            },
            grid: {
              color: 'rgba(255,255,255,0.2)'
            }
          }
        },
        backgroundColor: 'transparent'
      }
    }
    """.formatted(labels, total, avg, mult);

        return "https://quickchart.io/chart?c=" + URLEncoder.encode(config, StandardCharsets.UTF_8);
    }

    private static void sendToDiscord(String content, String chartUrl) {
        try {
            URL url = new URL(WEBHOOK);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String safeContent = content.replace("\"", "\\\"").replace("\n", "\\n");
            String json = """
            {
              "content": "%s",
              "embeds": [{
                "image": { "url": "%s" }
              }]
            }
            """.formatted(safeContent, chartUrl);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("[StatsSender] ✅ Webhook response: " + responseCode);
        } catch (Exception e) {
            System.err.println("[StatsSender] ❌ Błąd wysyłki do Discord: " + e.getMessage());
        }
    }

    // Reprezentacja punktu danych
    private static class Snapshot {
        final double total, average, multiplier;

        Snapshot(double total, double avg, double mult) {
            this.total = total;
            this.average = avg;
            this.multiplier = mult;
        }

        static Snapshot create() {
            return new Snapshot(
                    WealthAnalyzer.getTotalCoins(),
                    WealthAnalyzer.getAverageCoins(),
                    WealthAnalyzer.getCombinedDynamicMultiplier()
            );
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Snapshot other)) return false;
            return Double.compare(other.total, total) == 0 &&
                    Double.compare(other.average, average) == 0 &&
                    Double.compare(other.multiplier, multiplier) == 0;
        }
    }
}
