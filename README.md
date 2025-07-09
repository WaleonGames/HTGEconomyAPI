# 💰 HTGEconomyAPI

**HTGEconomyAPI** to zaawansowany system ekonomiczny dla serwerów Minecraft, zaprojektowany z myślą o dynamicznej, trudnej i nieprzewidywalnej gospodarce. Obsługuje coins, statystyki bogactwa graczy, dynamiczne mnożniki, integrację z PlaceholderAPI oraz system kar.

---

## ✅ Wymagania

- Minecraft `1.20.x` / `1.21.x`
- Java 17+
- ✅ [LuckPerms](https://luckperms.net)
- ✅ [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- ✅ [Vault](https://www.spigotmc.org/resources/vault.34315/) (polecane) 1.7.1+
- ✅ Kompatybilny z Purpur, Paper, Spigot

---

## 🔧 Instalacja

1. Umieść `HTGEconomyAPI.jar` w folderze `plugins/`
2. Upewnij się, że masz zainstalowane wymagane pluginy (`LuckPerms`, `PlaceholderAPI`)
3. Uruchom serwer — plugin automatycznie utworzy potrzebne pliki

---

## 🔌 Integracje

- **LuckPerms** – wykorzystywany do analizy rozkładu rang, które wpływają na dynamiczne ceny w ekonomii.  
- **PlaceholderAPI** – wbudowane rozszerzenie z placeholderem `%htgcoins_coins%`.  
- **QuickChart.io** – generowanie wykresów ekonomicznych i ich automatyczna wysyłka na Discord Webhook.  
- **HTGSklep** – system sklepu z obsługą coins oraz dynamicznych cen w oparciu o API ekonomii.  
- **HTGSprawdzanie** – zaawansowany system sprawdzania graczy, z karami dla administracji za brak podania powodu zakończenia sprawdzania.

---

## 📦 Aktualna wersja API dla pluginów

| Plugin            | Wersja       |
|-------------------|--------------|
| **HTGSklep**       | `0.0.4-beta` |
| **HTGSprawdzanie** | `0.0.4-beta` |

[![](https://jitpack.io/v/WaleonGames/HTGEconomyAPI.svg)](https://jitpack.io/#WaleonGames/HTGEconomyAPI)

---

## ⚙️ Funkcje

- 📊 **System coins** – każdy gracz ma swój balans
- 📈 **Dynamiczny mnożnik ekonomiczny** – zależny od stanu ekonomii
- 📡 **Automatyczna wysyłka statystyk ekonomii** na Discord (co 60s)
- 🧠 **WealthAnalyzer** – analiza średniej, sumy coins, rozkładu graczy
- 🔒 **PenaltyManager** – obsługa kar dla graczy (np. blokady zarobków)
- 🛡️ **Zabezpieczenia przed manipulacją przez administratorów**
- 🧾 **Historia statystyk** – zapisywana do pliku `economy_stats.json`

---

## 🔎 Komendy

| Komenda        | Opis                                             |
|----------------|--------------------------------------------------|
| `/coins`       | Admin: dodaj, usuń, ustaw, kara                   |
| `/dynamics`    | Pokazuje aktualny stan ekonomii (suma, średnia)  |

---

## 📂 Pliki konfiguracyjne

- `coins.yml` – dane ekonomiczne graczy
- `economy_stats.json` – historia zmian (do wykresów)
- `config.yml` – (w przygotowaniu)

---

## 🧪 PlaceholderAPI

| Placeholder              | Opis                                  |
|--------------------------|----------------------------------------|
| `%htgcoins_coins%`       | Ilość coins gracza (z tagiem waluty)   |

---

## 📈 Wysyłka statystyk na Discord

Plugin automatycznie generuje wykres (linia, kolory, tło ciemne) i wysyła co 60 sekund na webhook Discord. Dane są analizowane i aktualizowane tylko jeśli się zmieniły (brak spamu).

---

## 📌 Przyszłe funkcje

- Sezonowe bonusy (Złoty Tydzień, Tani Tydzień itp.)*

---

## 👨‍💻 Autor

- Projekt stworzony przez **ToJaWGYT**
- Wersja: `0.0.4-beta`
- Plugin wykorzystywany w ekosystemie HTGMC

---

## 🧾 Licencja

Ten projekt jest przeznaczony do użytku prywatnego i niepublicznego. Nie udostępniaj bez zgody autora.

