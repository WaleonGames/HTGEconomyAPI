# 🔧 Opcje integracji z innymi pluginami HTG

Plugin `HTGEconomyAPI` umożliwia pełną lub częściową integrację z innymi oficjalnymi pluginami systemu ekonomii. Poniżej znajdziesz przykładowe konfiguracje i opcje dostosowania.

---

## HTG2137 (publiczny)

> Oficjalna integracja | Dostępny: Prywatny

**Opis:**  
`HTG2137` To wyjątkowy plugin świętujący godzinę 21:37! Automatyczne eventy, nagrody i powiadomienia – gotowe do gry i w pełni po polsku!

**Opcje w `config.yml`:**
```yaml
actions:
- type: "htggive"
  amount: 1000
```

---

## HTGSklep

> Oficjalna integracja | Dostępny: Prywatny

**Opis:**
`HTGSklep` Oficjalny sklep z obsługą waluty HTG oraz dynamicznego przeliczania cen w zależności od bogactwa gracza.

**Opcje w `config.yml`**
```yaml
shop:
  vip:
    name: "&6Ranga VIP"
    price: 10000 # support in HTGEconomyAPI
    slot: 10
    material: NAME_TAG
    type: RANK
    data: vip
    description:
      - "&7Ranga na 30 dni"
      - "&8Dostęp do /kit vip"
```

---

**Ostatnia aktualizacja:** 2025-07-24
**Autor:** Zespół ToJaWGYT
