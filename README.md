# 🏰 Clansystem

Ein vollständiges Clan-Plugin mit Rängen, Homes, Warps, Truhen, Allianzen, Kriegen und einem Glow-Effekt fürs eigene Team.

---

## 📖 Grundprinzip

Es gibt **zwei getrennte Berechtigungs-Systeme**, die man nicht verwechseln sollte:

| | LuckPerms / Bukkit (`clansystem.*`) | Clan-interne Rang-Rechte |
|---|---|---|
| **Was regelt es?** | Ob ein Spieler einen Befehl *überhaupt* benutzen darf | Was ein Spieler *innerhalb seines Clans* darf |
| **Wo vergeben?** | In LuckPerms | Im Spiel über `/clan settings` → Ränge verwalten |
| **Standard** | Jeder Spieler hat alle Rechte | Abhängig vom zugewiesenen Rang |

> 👑 **Der Eigentümer** eines Clans (Ersteller bzw. aktueller Inhaber nach `/clan transfer`) hat **immer automatisch alle clan-internen Rechte** — egal welcher Rang ihm zugewiesen ist. Das lässt sich nicht über die Rang-Verwaltung verändern und hat nichts mit LuckPerms zu tun.
>
> Er wird dafür überall im Spiel (Haupt-Menü, Mitgliederliste, Clan-Chat-Präfix, Placeholder `%clan_rank%`) rein optisch als **„Leader"** angezeigt — statt seines echten Rangnamens (z. B. „Admin"). Es gibt dafür **keinen echten Rang** namens „Leader".

Ein Spieler braucht für viele Aktionen **beides zusammen**: sowohl die LuckPerms-Node (z. B. `clansystem.kick`) als auch das passende Rang-Recht (z. B. `KICK`).

---

## 🎮 Spieler-Befehle

Alle `/clan`-Befehle (Alias `/c`) benötigen als Grundvoraussetzung die Node `clansystem.use`.

### Clan-Verwaltung

| Befehl | Beschreibung | Node | Rang-Recht |
|---|---|---|---|
| `/clan` | Öffnet das Haupt-Menü (oder die Hilfe, falls in keinem Clan) | `clansystem.use` | – |
| `/clan create <name>` | Erstellt einen neuen Clan, man wird automatisch Eigentümer | `clansystem.create` | – |
| `/clan disband` | Löst den eigenen Clan komplett auf (mit Bestätigung) | – *(nur Eigentümer)* | – |
| `/clan leave` | Verlässt den eigenen Clan | `clansystem.leave` | – |
| `/clan transfer <spieler>` | Überträgt die Eigentümerschaft endgültig (auch per GUI) | – *(nur Eigentümer)* | – |

> ⏳ Für `/clan create` gilt ein Cooldown nach dem letzten Verlassen eines Clans — umgehbar mit `clansystem.bypass.cooldown`.
> 🚪 Der Eigentümer kann `/clan leave` nicht direkt nutzen — er muss vorher per `/clan transfer` übergeben.

### Mitglieder & Ränge

| Befehl | Beschreibung | Node | Rang-Recht |
|---|---|---|---|
| `/clan invite <spieler>` | Lädt einen Spieler ein (nur bei Beitrittsart „Einladung") | `clansystem.invite` | `INVITE` |
| `/clan accept` | Nimmt eine erhaltene Einladung an | `clansystem.join` | – |
| `/clan apply <clanname>` | Bewirbt sich bei einem Clan (nur bei „Bewerbung") | `clansystem.join` | – |
| `/clan join <clanname>` | Tritt einem offenen Clan sofort bei (nur bei „Offen") | `clansystem.join` | – |
| `/clan kick <spieler>` | Entfernt ein Mitglied aus dem Clan | `clansystem.kick` | `KICK` |
| `/clan promote <spieler>` | Befördert ein Mitglied einen Rang höher | – | `PROMOTE` |
| `/clan demote <spieler>` | Degradiert ein Mitglied einen Rang tiefer | – | `DEMOTE` |
| `/clan rank set <spieler> <rang>` | Weist direkt einen bestimmten Rang zu | `clansystem.rank` | `PROMOTE` oder `DEMOTE` |
| `/clan rank create <name>` | Erstellt einen neuen Rang (auch per GUI) | `clansystem.rank` | `RANK_MANAGE` |
| `/clan rank delete <name>` | Löscht einen Rang | `clansystem.rank` | `RANK_MANAGE` |
| `/clan rank rename <alt> <neu>` | Benennt einen Rang um (auch per GUI) | `clansystem.rank` | `RANK_MANAGE` |

### 🏠 Homes

> ℹ️ **Es gibt pro Clan immer nur EIN Home** — unabhängig vom Clan-Level. Ist bereits eines gesetzt, muss es erst mit `/clan delhome` gelöscht werden, bevor ein neues (ggf. mit anderem Namen) gesetzt werden kann.

| Befehl | Beschreibung | Node | Rang-Recht |
|---|---|---|---|
| `/clan sethome <name>` | Setzt das Clan-Home an der aktuellen Position (auch per GUI) | `clansystem.home` | `SET_HOME` |
| `/clan home <name>` | Teleportiert zum Clan-Home (Warmup & Cooldown) | `clansystem.home` | – |
| `/clan delhome <name>` | Löscht das Clan-Home | `clansystem.home` | `DEL_HOME` |
| `/clan homes` | Öffnet die Home-Übersicht | `clansystem.home` | – |

> ⏱️ Warmup/Cooldown bei `/clan home` sind umgehbar mit `clansystem.bypass.warmup` bzw. `clansystem.bypass.cooldown`.

### 🌀 Warps

> ℹ️ Die Anzahl möglicher Warps **hängt vom Clan-Level ab** (im Gegensatz zu Homes).

| Befehl | Beschreibung | Node | Rang-Recht |
|---|---|---|---|
| `/clan setwarp <name>` | Setzt einen Clan-Warp (auch per GUI) | `clansystem.warp` | `SET_WARP` |
| `/clan warp <name>` | Teleportiert zu einem Clan- oder freigegebenen Allianz-Warp | `clansystem.warp` | – |
| `/clan delwarp <name>` | Löscht einen Clan-Warp | `clansystem.warp` | `DEL_WARP` |
| `/clan warps` | Öffnet die Warp-Übersicht | `clansystem.warp` | – |

### 📦 Truhe

| Befehl | Beschreibung | Node | Rang-Recht |
|---|---|---|---|
| `/clan chest` | Öffnet die Clan-Truhe | `clansystem.chest` | `CHEST_ACCESS` zum Öffnen, `CHEST_WITHDRAW` zum Entnehmen |

### 🤝 Diplomatie

| Befehl | Beschreibung | Node | Rang-Recht |
|---|---|---|---|
| `/clan ally invite <clanname>` | Sendet eine Allianz-Anfrage (auch per GUI) | `clansystem.ally` | `ALLY_MANAGE` |
| `/clan ally accept <clanname>` | Nimmt eine Allianz-Anfrage an | `clansystem.ally` | `ALLY_MANAGE` |
| `/clan ally remove <clanname>` | Löst eine bestehende Allianz auf | `clansystem.ally` | `ALLY_MANAGE` |
| `/clan war declare <clanname>` | Erklärt einem anderen Clan den Krieg | `clansystem.war` | `WAR_MANAGE` |
| `/clan war surrender <clanname>` | Kapituliert in einem laufenden Krieg (ohne Namen → Kriegs-Menü) | `clansystem.war` | `WAR_MANAGE` |
| `/clan wars <clanname>` | Zeigt die Kriegs-Historie eines Clans | `clansystem.info` | – |

### ⚙️ Einstellungen & Sonstiges

| Befehl | Beschreibung | Node | Rang-Recht |
|---|---|---|---|
| `/clan settings` | Öffnet das Einstellungs-Menü | `clansystem.settings` | `SETTINGS` oder `RANK_MANAGE` |
| `/clan suffix <text>` | Setzt den Clan-Suffix per Chat | `clansystem.settings` | `SET_SUFFIX` |
| `/clan list` *(Alias `/clan top`)* | Öffnet die Clan-Rangliste (Level, Punkte, Mitglieder) | `clansystem.list` | – |
| `/clan info <clanname>` | Zeigt Clan-Infos (ohne Namen: eigener Clan) | `clansystem.info` | – |
| `/clan chat <nachricht>` *(Alias `/clan c`)* | Ohne Text: Clan-Chat-Modus umschalten, mit Text: Nachricht senden | `clansystem.chat` | – |
| `/clan ac <nachricht>` | Wie `/clan chat`, aber für den Allianz-Chat | `clansystem.chat` | – |
| `/clan help` *(Alias `/clan ?`)* | Zeigt die Befehlsübersicht | `clansystem.use` | – |

Im Einstellungs-Menü erreichbar: **Beitrittsart, Suffix, Icon, Ränge verwalten, Glow-Effekt, Clan auflösen.**

Beim Suffix lassen sich Farbe und Formatierung nur über das GUI einstellen (Einstellungen → Clan-Suffix); der Chat-Befehl setzt nur den reinen Text.

#### ✨ Glow-Effekt

Lässt alle **Online-Mitglieder** des Clans durch Wände hindurch leuchten (weißer Umriss).

- 🔒 **Ausschließlich für Mitglieder des eigenen Clans sichtbar** — niemals für Fremde oder gegnerische Clans.
- 🏷️ Der Spielername (über dem Kopf / in der Tab-Liste) bleibt **immer normal** — es gibt keine Farbauswahl.
- ⚠️ **Voraussetzung:** Auf dem Server muss zusätzlich [**ProtocolLib**](https://www.spigotmc.org/resources/protocollib.1997/) installiert sein (Soft-Dependency). Ohne ProtocolLib bleibt der Effekt **aus Sicherheitsgründen komplett deaktiviert** und lässt sich in der GUI nicht aktivieren — sonst wäre er serverweit für jeden sichtbar.

---

## 🛡️ Admin-Befehle

Alle `/clanadmin`-Befehle benötigen als Grundvoraussetzung die Node `clansystem.admin`.

| Befehl | Beschreibung | Node |
|---|---|---|
| `/clanadmin reload` | Lädt `config.yml`, `messages.yml` und `level.yml` neu | `clansystem.admin.reload` |
| `/clanadmin delete <clanname>` | Löst einen beliebigen Clan zwangsweise auf | `clansystem.admin.delete` |
| `/clanadmin setlevel <clanname> <level>` | Setzt das Level eines Clans direkt | `clansystem.admin.setlevel` |
| `/clanadmin setleader <clanname> <spieler>` | Setzt den Eigentümer eines Clans zwangsweise neu | `clansystem.admin.setleader` |
| `/clanadmin chest <clanname>` | Öffnet die Truhe eines fremden Clans zur Einsicht | `clansystem.admin.chest` |
| `/clanadmin addpoints <clanname> <punkte>` | Fügt einem Clan manuell Punkte hinzu | `clansystem.admin.addpoints` |
| `/clanadmin info <clanname>` | Zeigt ausführliche Admin-Infos zu einem Clan | nur `clansystem.admin` |
| `/clanadmin forcejoin <spieler> <clanname>` | Zwingt einen Spieler in einen Clan (umgeht Einladung) | `clansystem.admin.bypass` |
| `/clanadmin forceleave <spieler>` | Entfernt einen Spieler zwangsweise aus seinem Clan | `clansystem.admin.bypass` |
| `/clanadmin list` | Listet alle Clans des Servers auf | nur `clansystem.admin` |
| `/clanadmin help` | Zeigt die Admin-Befehlsübersicht | `clansystem.admin` |

---

## 🔑 Alle LuckPerms-Nodes im Überblick

### Spieler-Nodes (Standard: `true`, jeder hat sie)

| Node | Zweck |
|---|---|
| `clansystem.use` | Grundzugriff auf `/clan` |
| `clansystem.create` | Clan erstellen |
| `clansystem.join` | Clan beitreten, annehmen, bewerben |
| `clansystem.leave` | Clan verlassen |
| `clansystem.chat` | Clan- und Allianz-Chat nutzen |
| `clansystem.home` | Clan-Home nutzen und verwalten |
| `clansystem.warp` | Clan-Warps nutzen und verwalten |
| `clansystem.chest` | Clan-Truhe öffnen |
| `clansystem.list` | Clan-Rangliste anzeigen |
| `clansystem.info` | Clan-Infos und Kriegshistorie anzeigen |
| `clansystem.invite` | Spieler einladen |
| `clansystem.kick` | Spieler kicken |
| `clansystem.ally` | Allianzen verwalten |
| `clansystem.war` | Kriege verwalten |
| `clansystem.settings` | Clan-Einstellungen öffnen |
| `clansystem.rank` | Ränge verwalten per Befehl |

### Bypass- & Admin-Nodes (Standard: nur OP)

| Node | Zweck |
|---|---|
| `clansystem.bypass.cooldown` | Ignoriert alle Cooldowns |
| `clansystem.bypass.warmup` | Ignoriert alle Teleport-Warmups |
| `clansystem.admin` | Voller Zugriff auf `/clanadmin` |
| `clansystem.admin.reload` | Config neu laden |
| `clansystem.admin.delete` | Fremde Clans löschen |
| `clansystem.admin.setlevel` | Clan-Level setzen |
| `clansystem.admin.setleader` | Clan-Eigentümer setzen |
| `clansystem.admin.chest` | Fremde Clan-Truhen öffnen |
| `clansystem.admin.addpoints` | Clan-Punkte hinzufügen |
| `clansystem.admin.bypass` | `forcejoin` und `forceleave` nutzen |

> 💡 Diese Nodes steuern **nur**, ob ein Spieler einen Befehl überhaupt ausführen darf — sie ersetzen **nicht** das clan-interne Rang-System. Für `/clan kick` braucht ein Spieler z. B. sowohl `clansystem.kick` **als auch** das Rang-Recht `KICK`.

---

## 🏅 Clan-interne Rang-Berechtigungen

Diese werden **nicht** über LuckPerms vergeben, sondern pro Clan individuell im Spiel unter `/clan settings` → Ränge verwalten.

| Recht | Bedeutung |
|---|---|
| `INVITE` | Mitglieder einladen |
| `KICK` | Mitglieder kicken |
| `PROMOTE` | Mitglieder befördern |
| `DEMOTE` | Mitglieder degradieren |
| `SET_HOME` | Clan-Home setzen |
| `DEL_HOME` | Clan-Home löschen |
| `SET_WARP` | Clan-Warps setzen |
| `DEL_WARP` | Clan-Warps löschen |
| `CHEST_ACCESS` | Zugriff auf die Clan-Truhe (öffnen & einlagern) |
| `CHEST_WITHDRAW` | Items aus der Clan-Truhe entnehmen |
| `ALLY_MANAGE` | Allianzen einladen, annehmen und auflösen |
| `WAR_MANAGE` | Kriege erklären und kapitulieren |
| `SETTINGS` | Clan-Einstellungen öffnen und bearbeiten |
| `RANK_MANAGE` | Ränge erstellen, löschen, umbenennen, Rechte anpassen |
| `SET_SUFFIX` | Clan-Suffix bearbeiten (Text, Farbe, Formatierung) |
| `DISBAND` | Clan auflösen — **in der Praxis nur der echte Eigentümer**, egal welcher Rang das Recht zugewiesen hat |

### Standard-Ränge eines neuen Clans

*(änderbar in `config.yml` unter `default-ranks`)*

| Rang | Priorität | Rechte |
|---|---|---|
| **Admin** | 100 | Alle Rechte außer `DISBAND` |
| **Mitglied** | 10 | Nur `CHEST_ACCESS` |

> 👑 **„Leader" ist bewusst kein eigener Rang.** Der Ersteller bzw. aktuelle Eigentümer hat automatisch alle Rechte inklusive `DISBAND` — unabhängig davon, welchen Rang er zugewiesen hat. Er wird aber überall im Spiel (Haupt-Menü, Mitgliederliste, Clan-Chat-Präfix, Placeholder `%clan_rank%`) rein optisch als **„Leader"** angezeigt statt mit seinem echten Rangnamen. Das ist reine Anzeige-Logik, kein echter Rang.
