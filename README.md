# 💰 HTGEconomyAPI

**HTGEconomyAPI** to zaawansowany system ekonomiczny dla serwerów Minecraft, zaprojektowany z myślą o dynamicznej, trudnej i nieprzewidywalnej gospodarce.  
Obsługuje **coins**, statystyki bogactwa graczy, dynamiczne mnożniki, integrację z PlaceholderAPI oraz system kar.  
Od wersji `0.0.8` plugin obsługuje **MySQL** i nowy system sprawdzania wersji (seria `0.0.8.x`, `0.0.7.x`).

---

## 📄 Dokumentacja

- [Opcje integracji (Options.md)](Options.md)
- [Instrukcja główna (README.md)](README.md)

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
2. Upewnij się, że masz zainstalowane wymagane pluginy (`LuckPerms`, `PlaceholderAPI`, `Vault`)
3. Uruchom serwer — plugin automatycznie utworzy potrzebne pliki i tabele
4. Jeśli chcesz używać **MySQL**, skonfiguruj sekcję `database` w `config.yml`.

---

## 🔌 Integracje

- **Vault** – dzięki integracji z Vault API system działa z praktycznie wszystkimi pluginami ekonomicznymi.
- **LuckPerms** – wykorzystywany do analizy rang i wpływu na ceny w ekonomii.
- **PlaceholderAPI** – wbudowane rozszerzenia:
    - `%htgcoins_coins%` – balans gracza
    - `%htgeconomy_top_money_X%` – gracz na pozycji X w rankingu bogactwa
- **QuickChart.io** – generowanie wykresów ekonomicznych i automatyczna wysyłka na Discord Webhook.
- **HTGSklep** – sklep z obsługą coins i dynamicznych cen.
- **HTGSprawdzanie** – system sprawdzania graczy, powiązany z karami ekonomicznymi.

---

## 📦 Aktualna wersja API dla pluginów

| Plugin            | Wersja       |
|-------------------|--------------|
| **HTGSklep**       | `0.0.6-beta` |
| **HTGSprawdzanie** | `0.0.4-beta` |
| **HTG2137**        | `0.0.6.1-beta` |

[![](https://jitpack.io/v/WaleonGames/HTGEconomyAPI.svg)](https://jitpack.io/#WaleonGames/HTGEconomyAPI)

---

## ⚙️ Funkcje

- 📊 **System coins** – każdy gracz ma swój balans
- 📈 **Dynamiczny mnożnik ekonomiczny** – zależny od stanu gospodarki
- 🗄️ **Obsługa MySQL** – zamiast plików YAML (domyślnie włączona od `0.0.8.2-beta`)
- 🔁 **Przewalutowanie Vault ⇄ HTG** z dynamicznym mnożnikiem i cooldownem
- 📡 **Wysyłka statystyk ekonomii** na Discord (co 60s)
- 🧠 **WealthAnalyzer** – analiza średniej, sumy coins, rozkładu graczy
- 🔒 **PenaltyManager** – system kar i ograniczeń (np. blokady zarobków)
- 🛡️ **Zabezpieczenia** przed manipulacją przez administratorów
- 🧾 **Historia statystyk** – zapisywana do `economy_stats.json`
- 🔍 **VersionChecker** – sprawdzanie zgodności serii (`0.0.8.x`, `0.0.7.x`) z GitHub API

---

## 🔎 Komendy

| Komenda        | Opis                                                         |
|----------------|--------------------------------------------------------------|
| `/coins`       | Admin: dodaj, usuń, ustaw, kara                               |
| `/dynamics`    | Pokazuje aktualny stan ekonomii (suma, średnia)              |
| `/transfer`    | Przelew HTG innemu graczowi lub przewalutowanie Vault ⇄ HTG  |
| `/bank`        | Otwiera GUI z informacjami o koncie ekonomicznym             |

---

## 📂 Pliki konfiguracyjne

- `coins.yml` – dane ekonomiczne graczy (tylko gdy nie używasz MySQL)
- `economy_stats.json` – historia zmian (do wykresów)
- `config.yml` – ustawienia bazy danych, logowania i trybu pracy

---

## 🧪 PlaceholderAPI

| Placeholder                    | Opis                                    |
|--------------------------------|------------------------------------------|
| `%htgcoins_coins%`             | Ilość coins gracza (sformatowana)        |
| `%htgeconomy_top_money_1%`     | Najbogatszy gracz                        |
| `%htgeconomy_top_money_2%`     | Gracz na 2. miejscu w rankingu pieniędzy |

---

## 📈 Wysyłka statystyk na Discord

Plugin automatycznie generuje wykresy (ciemne tło, linie trendu) i wysyła co 60 sekund na webhook Discord.  
Dane aktualizują się tylko przy zmianach — brak spamu.

---

## 📌 Przyszłe funkcje

- Rozszerzone raporty ekonomiczne
- Lepsza integracja z bazami danych (np. PostgreSQL)

---

## 👨‍💻 Autor

- Projekt stworzony przez **ToJaWGYT**
- Aktualna linia rozwoju: `0.0.8.x` (wcześniej `0.0.7.x`)
- Plugin wykorzystywany w ekosystemie **HTGMC**

---

## 🧾 Licencja

Ten projekt jest przeznaczony do użytku prywatnego i niepublicznego.  
Nie udostępniaj bez zgody autora.  
