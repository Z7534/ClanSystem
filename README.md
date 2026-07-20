# Clansystem

Ein umfangreiches Clan-Plugin für **Paper 1.21.10** mit Rängen, Homes, Warps, Allianzen, Kriegen, Clan-Truhen, Levelsystem, PvP-Punkten, Glow-Effekt und PlaceholderAPI-Unterstützung.

- **Autor:** Z7534
- **API-Version:** 1.21
- **Softdepends:** PlaceholderAPI, LuckPerms, ProtocolLib (alle optional)
- **Datenbank:** SQLite (lokale `.db`-Datei) **oder** MariaDB

---

## Inhaltsverzeichnis

1. [Abhängigkeiten (Softdepends)](#abhängigkeiten-softdepends)
2. [Datenbank-Konfiguration](#datenbank-konfiguration)
3. [Befehle](#befehle)
   - [/clan](#clan-hauptbefehl)
   - [/clanadmin](#clanadmin-admin-befehl)
4. [Permissions](#permissions)
5. [Interne Rang-Berechtigungen](#interne-rang-berechtigungen)
6. [PlaceholderAPI-Platzhalter](#placeholderapi-platzhalter)
7. [Konfigurationsdateien](#konfigurationsdateien)
   - [config.yml](#configyml)
   - [level.yml](#levelyml)
   - [messages.yml](#messagesyml)
8. [Features im Detail](#features-im-detail)

---

## Abhängigkeiten (Softdepends)

Alle drei sind **optional** – das Plugin läuft auch ohne sie, einzelne Features fallen dann jedoch weg:

| Plugin | Zweck | Ohne dieses Plugin |
|---|---|---|
| **PlaceholderAPI** | Stellt die `%clansystem_*%`-Platzhalter bereit | Platzhalter funktionieren nicht |
| **LuckPerms** | Empfohlen für die Verwaltung der `clansystem.*`-Permissions | Permissions müssen über ein anderes Permission-Plugin oder `op` vergeben werden |
| **ProtocolLib** | Wird für den Glow-Effekt (leuchtender Umriss für Clan-Mitglieder) benötigt | Der Glow-Effekt bleibt aus Sicherheitsgründen automatisch deaktiviert |

## Datenbank-Konfiguration

Das Plugin unterstützt zwei Speicher-Typen, umschaltbar in der `config.yml` unter `database.type`:

```yaml
database:
  type: "sqlite"        # "sqlite" oder "mariadb"
  file: "clans.db"      # nur bei type: "sqlite"
  table-prefix: "clan_"
  mariadb:
    host: "localhost"
    port: 3306
    database: "clansystem"
    username: "root"
    password: ""
    use-ssl: false
    pool-size: 6
```

- **`sqlite`** (Standard): Speichert alle Daten in einer lokalen `.db`-Datei direkt im Plugin-Ordner (`plugins/Clansystem/clans.db`). Keine externe Datenbank nötig, ideal für kleinere Server.
- **`mariadb`**: Verbindet sich mit einem externen MariaDB/MySQL-Server über den Connection-Pool. Empfohlen für Netzwerke mit mehreren Servern, die sich eine Clan-Datenbank teilen.
- **`table-prefix`** gilt für beide Speicher-Typen und verhindert Namenskonflikte, falls die Datenbank/Datei von mehreren Plugins genutzt wird.

---

## Befehle

### `/clan` (Hauptbefehl)

Basis-Permission: **`clansystem.use`**

| Befehl | Beschreibung | Permission |
|---|---|---|
| `/clan create <name>` | Erstellt einen neuen Clan | `clansystem.create` |
| `/clan disband` | Löst den eigenen Clan auf (nur Owner) | `clansystem.use` |
| `/clan leave` | Verlässt den aktuellen Clan | `clansystem.leave` |
| `/clan kick <spieler>` | Kickt ein Mitglied aus dem Clan | `clansystem.kick` + Rang-Recht `KICK` |
| `/clan invite <spieler>` | Lädt einen Spieler in den Clan ein | `clansystem.invite` + Rang-Recht `INVITE` |
| `/clan accept` | Nimmt eine Clan-Einladung an | `clansystem.use` |
| `/clan apply <clanname>` | Bewirbt sich bei einem Clan (Beitrittsart `APPLY`) | `clansystem.join` |
| `/clan join <clanname>` | Tritt einem offenen Clan bei (Beitrittsart `OPEN`) | `clansystem.join` |
| `/clan sethome <name>` | Setzt ein Clan-Home an der aktuellen Position | `clansystem.home` + Rang-Recht `SET_HOME` |
| `/clan home <name>` | Teleportiert zu einem Clan-Home | `clansystem.home` |
| `/clan delhome <name>` | Löscht ein Clan-Home | `clansystem.home` + Rang-Recht `DEL_HOME` |
| `/clan homes` | Zeigt alle Clan-Homes in einem GUI | `clansystem.home` |
| `/clan setwarp <name>` | Setzt einen Clan-Warp | `clansystem.warp` + Rang-Recht `SET_WARP` |
| `/clan warp <name>` | Teleportiert zu einem Clan- oder Allianz-Warp | `clansystem.warp` |
| `/clan delwarp <name>` | Löscht einen Clan-Warp | `clansystem.warp` + Rang-Recht `DEL_WARP` |
| `/clan warps` | Zeigt alle Clan-Warps in einem GUI | `clansystem.warp` |
| `/clan chest` | Öffnet die Clan-Truhe | `clansystem.chest` + Rang-Recht `CHEST_ACCESS` |
| `/clan ally invite <clanname>` | Schlägt einem anderen Clan eine Allianz vor | `clansystem.ally` + Rang-Recht `ALLY_MANAGE` |
| `/clan ally accept <clanname>` | Nimmt einen Allianz-Vorschlag an | `clansystem.ally` + Rang-Recht `ALLY_MANAGE` |
| `/clan ally remove <clanname>` | Beendet eine bestehende Allianz | `clansystem.ally` + Rang-Recht `ALLY_MANAGE` |
| `/clan war declare <clanname>` | Erklärt einem Clan den Krieg | `clansystem.war` + Rang-Recht `WAR_MANAGE` |
| `/clan war surrender <clanname>` | Kapituliert in einem laufenden Krieg | `clansystem.war` + Rang-Recht `WAR_MANAGE` |
| `/clan rank set <spieler> <rang>` | Weist einem Mitglied einen Rang zu | `clansystem.rank` + Rang-Recht `PROMOTE`/`DEMOTE` |
| `/clan rank create <name>` | Erstellt einen neuen Rang | `clansystem.rank` + Rang-Recht `RANK_MANAGE` |
| `/clan rank delete <name>` | Löscht einen Rang | `clansystem.rank` + Rang-Recht `RANK_MANAGE` |
| `/clan rank rename <alter-name> <neuer-name>` | Benennt einen Rang um | `clansystem.rank` + Rang-Recht `RANK_MANAGE` |
| `/clan settings` | Öffnet die Clan-Einstellungen (Icon, Farbe, Glow, Suffix, Beitrittsart) | `clansystem.settings` |
| `/clan list` / `/clan top` | Zeigt die Clan-Rangliste (nach Punkten) | `clansystem.list` |
| `/clan info [clanname]` | Zeigt Informationen zu einem Clan (ohne Argument: eigener Clan) | `clansystem.info` |
| `/clan chat <nachricht>` bzw. `/clan c` | Sendet eine Nachricht in den Clan-Chat, ohne Argument: schaltet den Clan-Chat-Modus um | `clansystem.chat` |
| `/clan ac <nachricht>` | Sendet eine Nachricht in den Allianz-Chat, ohne Argument: schaltet den Allianz-Chat-Modus um | `clansystem.chat` |
| `/clan suffix <suffix>` | Setzt das Clan-Kürzel/Tag | Rang-Recht `SET_SUFFIX` |
| `/clan promote <spieler>` | Befördert ein Mitglied zum nächsthöheren Rang | Rang-Recht `PROMOTE` |
| `/clan demote <spieler>` | Degradiert ein Mitglied zum nächstniedrigeren Rang | Rang-Recht `DEMOTE` |
| `/clan transfer <spieler>` | Überträgt die Clan-Eigentümerschaft an ein anderes Mitglied | nur Owner |
| `/clan help` bzw. `/clan ?` | Zeigt die Befehlsübersicht | `clansystem.use` |

> Beitrittsarten: Ein Clan kann `OPEN` (jeder kann per `/clan join` beitreten), `INVITE` (nur per Einladung) oder `APPLY` (Bewerbung, muss von einem berechtigten Mitglied angenommen werden) sein. Einstellbar über `/clan settings` bzw. Standardwert in der `config.yml` (`clan.default-join-type`).

### `/clanadmin` (Admin-Befehl)

Basis-Permission: **`clansystem.admin`**

| Befehl | Beschreibung | Permission |
|---|---|---|
| `/clanadmin reload` | Lädt `config.yml`, `level.yml` und `messages.yml` neu | `clansystem.admin.reload` |
| `/clanadmin delete <clanname>` | Löscht einen Clan endgültig | `clansystem.admin.delete` |
| `/clanadmin setlevel <clanname> <level>` | Setzt das Level eines Clans direkt | `clansystem.admin.setlevel` |
| `/clanadmin setleader <clanname> <spieler>` | Setzt einen neuen Clan-Owner | `clansystem.admin.setleader` |
| `/clanadmin chest <clanname>` | Öffnet die Truhe eines fremden Clans | `clansystem.admin.chest` |
| `/clanadmin addpoints <clanname> <punkte>` | Fügt einem Clan Punkte hinzu (kann Level-Aufstiege auslösen) | `clansystem.admin.addpoints` |
| `/clanadmin info <clanname>` | Zeigt detaillierte Admin-Infos zu einem Clan | `clansystem.admin` |
| `/clanadmin forcejoin <spieler> <clanname>` | Fügt einen Spieler zwangsweise einem Clan hinzu | `clansystem.admin.bypass` |
| `/clanadmin forceleave <spieler>` | Entfernt einen Spieler zwangsweise aus seinem Clan | `clansystem.admin.bypass` |
| `/clanadmin list` | Listet alle existierenden Clans auf | `clansystem.admin` |
| `/clanadmin help` bzw. `/clanadmin ?` | Zeigt die Admin-Befehlsübersicht | `clansystem.admin` |

---

## Permissions

Alle Permissions sind in der `plugin.yml` registriert. Standard-Vergabe steht jeweils in Klammern.

### Allgemeine Nutzung

| Permission | Beschreibung | Standard |
|---|---|---|
| `clansystem.use` | Grundzugriff auf alle `/clan`-Befehle | `true` |
| `clansystem.create` | Erlaubt das Erstellen eines Clans | `true` |
| `clansystem.join` | Erlaubt das Beitreten/Bewerben bei einem Clan | `true` |
| `clansystem.leave` | Erlaubt das Verlassen eines Clans | `true` |
| `clansystem.chat` | Erlaubt die Nutzung von Clan- und Allianz-Chat | `true` |
| `clansystem.home` | Erlaubt die Nutzung von Clan-Homes | `true` |
| `clansystem.warp` | Erlaubt die Nutzung von Clan-Warps | `true` |
| `clansystem.chest` | Erlaubt den Zugriff auf die Clan-Truhe | `true` |
| `clansystem.list` | Erlaubt das Anzeigen der Clan-Rangliste | `true` |
| `clansystem.info` | Erlaubt das Anzeigen von Clan-Infos | `true` |
| `clansystem.invite` | Erlaubt das Einladen von Spielern | `true` |
| `clansystem.kick` | Erlaubt das Kicken von Spielern | `true` |
| `clansystem.ally` | Erlaubt die Verwaltung von Allianzen | `true` |
| `clansystem.war` | Erlaubt die Verwaltung von Kriegen | `true` |
| `clansystem.settings` | Erlaubt die Verwaltung von Clan-Einstellungen | `true` |
| `clansystem.rank` | Erlaubt die Verwaltung von Rängen | `true` |

### Bypass-Permissions

| Permission | Beschreibung | Standard |
|---|---|---|
| `clansystem.bypass.cooldown` | Ignoriert alle Cooldowns (z.B. Beitritts-Cooldown, Teleport-Cooldown) | `op` |
| `clansystem.bypass.warmup` | Ignoriert alle Teleport-Warmups | `op` |

### Admin-Permissions

| Permission | Beschreibung | Standard |
|---|---|---|
| `clansystem.admin` | Voller Admin-Zugriff auf das Clan-System (Elternknoten) | `op` |
| `clansystem.admin.reload` | Erlaubt das Neuladen der Konfiguration | `op` |
| `clansystem.admin.delete` | Erlaubt das Löschen von Clans | `op` |
| `clansystem.admin.setlevel` | Erlaubt das Setzen von Clan-Leveln | `op` |
| `clansystem.admin.setleader` | Erlaubt das Setzen von Clan-Ownern | `op` |
| `clansystem.admin.chest` | Erlaubt den Zugriff auf fremde Clan-Truhen | `op` |
| `clansystem.admin.addpoints` | Erlaubt das Hinzufügen von Clan-Punkten | `op` |
| `clansystem.admin.bypass` | Umgeht alle Einschränkungen (u.a. `forcejoin`/`forceleave`) | `op` |

> `clansystem.admin` ist der Elternknoten aller `clansystem.admin.*`-Rechte – wer `clansystem.admin` besitzt, erhält automatisch auch alle Kind-Permissions.

## Interne Rang-Berechtigungen

Zusätzlich zu den Bukkit-Permissions oben gibt es **clan-interne Ränge** mit eigenen Rechten, die pro Clan über `/clan rank` verwaltet werden (unabhängig vom Server-Permission-System). Der Clan-Owner besitzt **immer alle** dieser Rechte, unabhängig vom zugewiesenen Rang:

| Rang-Recht | Beschreibung |
|---|---|
| `INVITE` | Mitglieder einladen |
| `KICK` | Mitglieder kicken |
| `PROMOTE` | Mitglieder befördern |
| `DEMOTE` | Mitglieder degradieren |
| `SET_HOME` | Homes setzen |
| `DEL_HOME` | Homes löschen |
| `SET_WARP` | Warps setzen |
| `DEL_WARP` | Warps löschen |
| `CHEST_ACCESS` | Zugriff auf die Clan-Truhe |
| `CHEST_WITHDRAW` | Aus der Clan-Truhe entnehmen |
| `ALLY_MANAGE` | Allianzen verwalten |
| `WAR_MANAGE` | Kriege verwalten |
| `SETTINGS` | Clan-Einstellungen ändern |
| `RANK_MANAGE` | Ränge verwalten |
| `SET_SUFFIX` | Clan-Suffix ändern |
| `DISBAND` | Clan auflösen |

Die Standard-Ränge für neu erstellte Clans (`Admin` und `Mitglied`) sowie deren Rechte lassen sich in der `config.yml` unter `default-ranks` anpassen (siehe unten).

---

## PlaceholderAPI-Platzhalter

Voraussetzung: PlaceholderAPI ist installiert. Identifier: **`clansystem`**

| Platzhalter | Beschreibung | Beispiel |
|---|---|---|
| `%clansystem_clan_name%` | Name des Clans des Spielers (leer, falls in keinem Clan) | `Ravenclan` |
| `%clansystem_clan_suffix%` | Eingefärbtes Clan-Suffix/-Tag | `§b[RVC]` |
| `%clansystem_clan_level%` | Aktuelles Level des Clans | `4` |
| `%clansystem_clan_points%` | Aktuelle Punktzahl des Clans | `1250` |
| `%clansystem_clan_rank%` | Angezeigter Rangname des Spielers im Clan | `Admin` |
| `%clansystem_clan_membercount%` | Aktuelle Mitgliederzahl des Clans | `12` |
| `%clansystem_clan_maxmembers%` | Maximale Mitgliederzahl (abhängig vom Level) | `20` |

Ist der Spieler in keinem Clan, liefern alle Platzhalter einen leeren String bzw. `"0"` (bei Zahlenwerten).

---

## Konfigurationsdateien

### `config.yml`

| Sektion | Beschreibung |
|---|---|
| `database` | Speicher-Typ (`sqlite`/`mariadb`), Dateiname, Tabellen-Prefix, MariaDB-Zugangsdaten (siehe [Datenbank-Konfiguration](#datenbank-konfiguration)) |
| `clan.name` | Mindest-/Maximallänge sowie erlaubte Zeichen (Regex) für Clan-Namen |
| `clan.leave-cooldown` | Cooldown in Sekunden nach Verlassen eines Clans, bevor ein neuer erstellt/beigetreten werden kann |
| `clan.default-join-type` | Standard-Beitrittsart neuer Clans: `OPEN`, `INVITE` oder `APPLY` |
| `suffix` | Maximallänge, erlaubte Formatierungen und verbotene Wörter für Clan-Suffixe |
| `teleport` | Warmup/Cooldown für Homes & Warps, Abbruch bei Bewegung/Schaden |
| `pvp` | Punktevergabe für Kriegs-/normale Kills, Friendly-Fire-Einstellungen |
| `broadcasts` | Welche Ereignisse serverweit angekündigt werden (Clan erstellt/aufgelöst, Mitglied beigetreten/verlassen) |
| `default-ranks` | Standard-Ränge samt Priorität und Rechten für neu erstellte Clans |
| `chest-logging` | Aktiviert/deaktiviert das Logging der Clan-Truhen-Interaktionen |

### `level.yml`

Definiert das Levelsystem der Clans:

- **`base-values`**: Startwerte eines neuen Clans (`max-members`, `max-warps`, `chest-rows`, `icon`). `max-homes` wird ignoriert – jeder Clan hat immer genau **1** Home, unabhängig vom Level.
- **`levels`**: Pro Level `points-required` (benötigte Gesamtpunktzahl) sowie `rewards` (**additive** Boni zu den Basiswerten: `max-members`, `max-warps`, `chest-rows`, optional `icon` für ein neu freigeschaltetes Clan-Icon).

### `messages.yml`

Enthält sämtliche Spieler-Nachrichten des Plugins (Chat-Format, Fehlermeldungen, GUI-Texte, Broadcasts usw.) und kann frei angepasst/übersetzt werden. Unterstützt Farbcodes (`&`) und Platzhalter wie `{clan}`, `{spieler}`, `{usage}` je nach Nachricht.

---

## Features im Detail

- **Levelsystem**: Clans sammeln Punkte (z.B. durch PvP-Kills) und steigen dadurch automatisch im Level auf, wodurch mehr Mitglieder, Warps, Truhenplatz und neue Clan-Icons freigeschaltet werden.
- **Homes & Warps**: Jeder Clan hat genau ein Home; Warps sind in der Anzahl vom Level abhängig und können optional für Allianzen sichtbar gemacht werden.
- **Clan-Truhe**: Ein gemeinsames, geteiltes Inventar pro Clan mit optionalem Logging aller Einlagerungen/Entnahmen.
- **Allianzen**: Zwei Clans können sich verbünden, wodurch z. B. Allianz-Chat, gemeinsame Warp-Sichtbarkeit und deaktiviertes Friendly Fire möglich werden.
- **Kriege**: Clans können sich gegenseitig den Krieg erklären; Kills im Krieg geben mehr Punkte als reguläre Kills.
- **Rangsystem**: Frei konfigurierbare Ränge pro Clan mit granularen Rechten (siehe [Interne Rang-Berechtigungen](#interne-rang-berechtigungen)); der Clan-Owner hat immer alle Rechte.
- **Glow-Effekt**: Optionaler, immer weißer Leucht-Umriss für Clan-Mitglieder (nur mit ProtocolLib), ohne dass Spielernamen farblich verändert werden.
- **GUIs**: Vollständig menügesteuerte Bedienung für Clan-Übersicht, Mitgliederliste, Homes/Warps, Allianzen, Ränge, Einstellungen, Icon-/Farbauswahl u. v. m.
