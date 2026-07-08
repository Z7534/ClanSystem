# 🏰 Clansystem – Spieler-Guide

Willkommen! Hier steht alles, was du als Spieler über Clans wissen musst: wie du einen gründest oder beitrittst, was die einzelnen Ränge dürfen, wie Homes, Warps, die Truhe, Allianzen und Kriege funktionieren – und was Server-Admins am Ende noch draufsatteln können.

> 💬 Alle Befehle beginnen mit `/clan` (Kurzform: `/c`). Am einfachsten geht fast alles auch per Menü – einfach `/clan` ohne alles eingeben, dann öffnet sich das Haupt-Menü.

---

## 📜 Inhaltsverzeichnis

1. [Erste Schritte](#-erste-schritte)
2. [Ränge & was sie dürfen](#-ränge--was-sie-dürfen)
3. [Mitglieder verwalten](#-mitglieder-verwalten)
4. [Homes](#-homes)
5. [Warps](#-warps)
6. [Die Clan-Truhe](#-die-clan-truhe)
7. [Chat](#-chat)
8. [Allianzen & Kriege](#-allianzen--kriege)
9. [Einstellungen](#️-einstellungen)
10. [Der Glow-Effekt](#-der-glow-effekt)
11. [Clan-Level & Punkte](#-clan-level--punkte)
12. [Was Server-Admins können](#️-was-server-admins-können)

---

## 🚀 Erste Schritte

### Clan erstellen

```
/clan create <name>
```

Du wirst automatisch **Eigentümer** deines neuen Clans. Der Name darf nur bestimmte Zeichen enthalten und muss innerhalb einer festgelegten Mindest-/Höchstlänge liegen (sagt dir der Server per Fehlermeldung genau, falls es nicht passt).

> ⏳ Nachdem du zuvor einen Clan verlassen hast, gibt es einen kurzen Cooldown, bevor du wieder einen neuen erstellen kannst.

### Einem Clan beitreten

Es gibt **drei Beitrittsarten**, die der Clan selbst festlegt (unter Einstellungen → Beitrittsart):

| Beitrittsart | Wie trittst du bei? |
|---|---|
| 🔒 **Einladung** | Ein Mitglied mit dem Recht `INVITE` lädt dich per `/clan invite <dich>` ein, du nimmst mit `/clan accept` an |
| 📝 **Bewerbung** | Du bewirbst dich selbst mit `/clan apply <clanname>`, ein berechtigtes Mitglied muss dich annehmen |
| 🌍 **Offen** | Du trittst sofort bei mit `/clan join <clanname>`, keine Bestätigung nötig |

Willst du selbst herausfinden, welche Clans es gibt und wie sie stehen? `/clan list` (oder `/clan top`) zeigt dir die Rangliste nach Level, Punkten oder Mitgliederzahl.

### Clan wieder verlassen

```
/clan leave
```

Geht für jedes normale Mitglied. **Der Eigentümer** kann das nicht direkt – er muss vorher mit `/clan transfer <spieler>` die Eigentümerschaft an jemand anderen übergeben (geht auch per Menü: Mitglied anklicken → „Eigentümer übertragen"). Das ist **endgültig**, also gut überlegen!

> ⏳ Nach dem Verlassen läuft ein Cooldown, bevor du selbst wieder einen Clan gründen kannst.

---

## 👑 Ränge & was sie dürfen

Jeder Clan hat eigene **Ränge**, die unabhängig vom Server-Rang (LuckPerms) funktionieren. Ein Rang ist einfach ein Bündel aus einzelnen Rechten – die legt jeder Clan selbst fest unter **`/clan settings` → Ränge verwalten**.

### Die Standard-Ränge eines neuen Clans

| Rang | Was er darf |
|---|---|
| **Admin** | So gut wie alles – einladen, kicken, befördern, degradieren, Homes/Warps setzen & löschen, Truhe nutzen & entnehmen, Allianzen & Kriege verwalten, Einstellungen ändern, Ränge verwalten, Suffix bearbeiten. Einzige Ausnahme: den Clan auflösen kann er **nicht**. |
| **Mitglied** | Nur Zugriff auf die Clan-Truhe (rein legen und ansehen) |

Diese beiden Ränge sind nur der Standard – der Eigentümer kann jederzeit neue Ränge erstellen, umbenennen, löschen oder die Rechte anders zusammenstellen.

### Die einzelnen Rechte im Überblick

| Recht | Bedeutet, das Mitglied darf … |
|---|---|
| `INVITE` | … andere Spieler in den Clan einladen |
| `KICK` | … Mitglieder aus dem Clan werfen |
| `PROMOTE` | … Mitglieder einen Rang höher befördern |
| `DEMOTE` | … Mitglieder einen Rang tiefer degradieren |
| `SET_HOME` | … das Clan-Home setzen |
| `DEL_HOME` | … das Clan-Home löschen |
| `SET_WARP` | … Clan-Warps setzen |
| `DEL_WARP` | … Clan-Warps löschen |
| `CHEST_ACCESS` | … die Clan-Truhe öffnen und Items reinlegen |
| `CHEST_WITHDRAW` | … Items aus der Clan-Truhe **entnehmen** (strenger als reinlegen!) |
| `ALLY_MANAGE` | … Allianzen einladen, annehmen und auflösen |
| `WAR_MANAGE` | … Kriege erklären und kapitulieren |
| `SETTINGS` | … das Einstellungsmenü öffnen und bearbeiten |
| `RANK_MANAGE` | … Ränge erstellen, löschen, umbenennen und deren Rechte anpassen |
| `SET_SUFFIX` | … den Clan-Suffix bearbeiten (Text, Farbe, Formatierung) |
| `DISBAND` | … den Clan auflösen — **klappt aber in der Praxis nur beim echten Eigentümer**, siehe unten |

### 👑 Der Eigentümer ist ein Sonderfall

Der Ersteller deines Clans (oder wer auch immer die Eigentümerschaft per `/clan transfer` bekommen hat) hat **automatisch immer alle Rechte oben drüber** – inklusive Clan auflösen – ganz egal, welchen Rang er gerade zugewiesen hat. Das kann ihm auch niemand über die Rang-Verwaltung wegnehmen.

Er wird deswegen im Spiel (Haupt-Menü, Mitgliederliste, Chat-Präfix) auch überall als **„Leader"** angezeigt, nicht mit seinem eigentlichen Rangnamen wie „Admin". Es gibt aber keinen echten Rang namens „Leader" zum Zuweisen – das ist reine Anzeigesache für genau eine Person pro Clan: den Eigentümer.

---

## 🧑‍🤝‍🧑 Mitglieder verwalten

| Befehl | Was passiert | Braucht Recht |
|---|---|---|
| `/clan invite <spieler>` | Lädt jemanden ein | `INVITE` |
| `/clan accept` | Nimmt eine Einladung an, die du bekommen hast | – |
| `/clan kick <spieler>` | Wirft ein Mitglied raus | `KICK` |
| `/clan promote <spieler>` | Befördert ein Mitglied einen Rang höher | `PROMOTE` |
| `/clan demote <spieler>` | Degradiert ein Mitglied einen Rang tiefer | `DEMOTE` |
| `/clan rank set <spieler> <rang>` | Setzt direkt einen bestimmten Rang | `PROMOTE` oder `DEMOTE` |

> ⚖️ **Rang-Hierarchie beim Kicken:** Du kannst niemanden kicken, der einen gleich hohen oder höheren Rang hat als du selbst – auch mit dem Recht `KICK` nicht.

---

## 🏠 Homes

```
/clan sethome <name>      Setzt das Home an deiner Position    (Recht: SET_HOME)
/clan home <name>         Teleportiert dich zum Home            
/clan delhome <name>      Löscht das Home                       (Recht: DEL_HOME)
/clan homes               Zeigt die Übersicht
```

> ⚠️ **Wichtig:** Ein Clan hat **immer nur genau EIN Home** – und zwar unabhängig vom Clan-Level. Willst du das Home an einen anderen Ort verlegen oder umbenennen, musst du erst das alte mit `/clan delhome` löschen, bevor du ein neues setzt.

Beim Teleportieren gibt es einen kurzen **Warmup** (du darfst dich nicht bewegen, sonst wird abgebrochen) und danach einen **Cooldown**, bevor du erneut teleportieren kannst. Wer die Node `clansystem.bypass.warmup` bzw. `clansystem.bypass.cooldown` hat (meistens nur Admins), umgeht das.

---

## 🌀 Warps

```
/clan setwarp <name>      Setzt einen neuen Warp                (Recht: SET_WARP)
/clan warp <name>         Teleportiert zu einem Warp
/clan delwarp <name>      Löscht einen Warp                     (Recht: DEL_WARP)
/clan warps               Zeigt die Übersicht
```

Im Gegensatz zum Home kann ein Clan **mehrere Warps** haben – wie viele genau, hängt vom **Clan-Level** ab (je höher das Level, desto mehr Warps). Ein Warp kann außerdem für Allianzen freigegeben werden, dann können auch verbündete Clans ihn per `/clan warp <name>` benutzen.

---

## 📦 Die Clan-Truhe

```
/clan chest
```

Öffnet die gemeinsame Clan-Truhe. Wie groß sie ist, richtet sich nach dem Clan-Level.

- Mit dem Recht `CHEST_ACCESS` darfst du sie öffnen und Items **reinlegen**.
- Erst mit dem zusätzlichen Recht `CHEST_WITHDRAW` darfst du auch Items **entnehmen**.

So kann ein Clan z. B. neuen Mitgliedern erlauben, Sachen zu spenden, ohne dass sie gleich alles wieder rausnehmen können.

---

## 💬 Chat

| Befehl | Was passiert |
|---|---|
| `/clan chat` *(Alias `/clan c`)* | Ohne Text: schaltet deinen normalen Chat dauerhaft auf Clan-Chat um. Mit Text dahinter: sendet eine einzelne Nachricht in den Clan-Chat |
| `/clan ac <nachricht>` | Genau wie oben, aber im **Allianz-Chat** – den lesen alle verbündeten Clans mit |

Im Clan-Chat wird vor deinem Namen automatisch dein Rang angezeigt (bzw. „Leader", falls du der Eigentümer bist) sowie der Clan-Suffix.

---

## 🤝 Allianzen & Kriege

### Allianzen (Frieden & Zusammenarbeit)

```
/clan ally invite <clanname>     Anfrage senden          (Recht: ALLY_MANAGE)
/clan ally accept <clanname>     Anfrage annehmen        (Recht: ALLY_MANAGE)
/clan ally remove <clanname>     Allianz auflösen        (Recht: ALLY_MANAGE)
```

Verbündete Clans können sich im Allianz-Chat (`/clan ac`) austauschen und ggf. freigegebene Warps des anderen nutzen. Standardmäßig gibt es außerdem **kein Friendly Fire** zwischen Verbündeten (kann der Server aber anders konfigurieren).

### Kriege

```
/clan war declare <clanname>      Erklärt den Krieg              (Recht: WAR_MANAGE)
/clan war surrender <clanname>    Kapituliert                    (Recht: WAR_MANAGE)
/clan war                         Ohne Namen: öffnet das Kriegs-Menü
/clan wars <clanname>             Zeigt die Kriegs-Historie
```

Während eines Kriegs bringen Kills gegen Mitglieder des verfeindeten Clans **Kriegspunkte** und Clan-Punkte (mehr als normale Kills). Wer kapituliert, verliert automatisch und der Gegner bekommt einen Sieg-Bonus.

---

## ⚙️ Einstellungen

```
/clan settings
```

Öffnet das Einstellungsmenü (Recht: `SETTINGS` oder `RANK_MANAGE`). Von hier aus erreichst du:

- **Beitrittsart** ändern (Einladung / Bewerbung / Offen)
- **Suffix** bearbeiten – Text per `/clan suffix <text>` oder komplett mit Farbe & Formatierung im Menü (Recht: `SET_SUFFIX`)
- **Icon** des Clans auswählen
- **Ränge verwalten** – erstellen, löschen, umbenennen, Rechte anpassen (Recht: `RANK_MANAGE`)
- **Glow-Effekt** ein-/ausschalten (siehe unten)
- **Clan auflösen** – nur der Eigentümer kann das wirklich durchziehen, egal was das Recht `DISBAND` sagt

---

## ✨ Der Glow-Effekt

Im Einstellungsmenü lässt sich ein Glow-Effekt aktivieren, der alle **online** Mitglieder deines Clans durch Wände hindurch leuchten lässt (praktisch, um Teamkollegen auf großen Distanzen oder in Gebäuden zu finden).

- 🔒 **Nur dein eigenes Team sieht den Glow** – fremde Spieler und gegnerische Clans sehen rein gar nichts davon. Ihr verratet euch also nicht gegenüber Feinden.
- 🏷️ Euer Spielername bleibt dabei ganz normal (weder über dem Kopf noch in der Tab-Liste färbt sich etwas ein) – es gibt keine wählbare Farbe, der Umriss ist immer schlicht weiß.
- ⚠️ Der Effekt braucht ein zusätzliches Server-Plugin (**ProtocolLib**). Ist das nicht installiert, lässt sich der Glow gar nicht erst aktivieren – frag im Zweifel einen Admin, ob das Plugin läuft.

---

## 📈 Clan-Level & Punkte

Euer Clan sammelt **Punkte** (z. B. durch Kills im Krieg, normale Kills, oder wenn ein Admin welche vergibt) und steigt dadurch im **Level** auf. Mit höherem Level bekommt ihr:

- 👥 Mehr maximale Mitgliederplätze
- 🌀 Mehr mögliche Warps
- 📦 Eine größere Clan-Truhe

> ℹ️ **Nicht** vom Level beeinflusst: die Anzahl der Homes – davon gibt es immer nur eines, siehe oben.

---

## 🛡️ Was Server-Admins können

Server-Admins (mit der Node `clansystem.admin` bzw. den passenden Unter-Nodes) haben über `/clanadmin` Befehle, die **eure normalen Clan-Regeln komplett umgehen können**. Gut zu wissen, falls mal etwas an eurem Clan „von außen" passiert:

| Was ein Admin tun kann | Befehl |
|---|---|
| Euren Clan komplett zwangsauflösen | `/clanadmin delete <clanname>` |
| Euer Clan-Level direkt setzen (hoch oder runter) | `/clanadmin setlevel <clanname> <level>` |
| Euren Eigentümer zwangsweise austauschen | `/clanadmin setleader <clanname> <spieler>` |
| Eure Clan-Truhe einsehen (auch ohne Mitglied zu sein) | `/clanadmin chest <clanname>` |
| Eurem Clan manuell Punkte geben (z. B. Event-Belohnung) | `/clanadmin addpoints <clanname> <punkte>` |
| Sich ausführliche Infos zu eurem Clan anzeigen lassen | `/clanadmin info <clanname>` |
| Einen Spieler in euren Clan zwingen (Einladungssystem wird umgangen) | `/clanadmin forcejoin <spieler> <clanname>` |
| Einen Spieler zwangsweise aus eurem Clan entfernen | `/clanadmin forceleave <spieler>` |
| Alle Clans des Servers auflisten | `/clanadmin list` |
| Die komplette Konfiguration (Texte, Level-Werte, Einstellungen) neu laden | `/clanadmin reload` |

> 💡 Das bedeutet vor allem: Auch wenn du Eigentümer bist und normalerweise „alle Rechte" in deinem Clan hast – ein Server-Admin kann jederzeit von außen eingreifen, dich austauschen, Mitglieder zwangsweise entfernen/hinzufügen oder den ganzen Clan auflösen. Das ist so gewollt, damit das Server-Team bei Problemen (Regelverstöße, Streit, Bugs) eingreifen kann.

---

## 🔎 Kurzübersicht: Brauche ich eine LuckPerms-Berechtigung?

Fast jeder Server-Spieler hat standardmäßig **alle** normalen `/clan`-Rechte (Erstellen, Beitreten, Chatten, Homes/Warps/Truhe/Allianzen/Kriege/Einstellungen nutzen usw.) – ihr müsst euch darum in der Regel **nicht kümmern**. Wirklich eingeschränkt sind meist nur:

- ⏳ **Cooldowns/Warmups umgehen** – nur für Admins/Ops gedacht
- 🛡️ **Alle `/clanadmin`-Befehle** – nur für das Server-Team gedacht

Solltest du also mal eine „Keine Berechtigung"-Meldung bei einem ganz normalen `/clan`-Befehl bekommen, obwohl du eigentlich das passende Rang-Recht in deinem Clan hast, frag am besten kurz einen Admin – dann fehlt dir vermutlich die LuckPerms-Node dazu.
