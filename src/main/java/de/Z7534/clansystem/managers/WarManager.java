package de.Z7534.clansystem.managers;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.models.ClanWar;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WarManager {

    private final Clansystem plugin;
    private final Map<Integer, ClanWar> activeWars;
    private final Map<String, ClanWar> warsByClanPair;

    public WarManager(Clansystem plugin) {
        this.plugin = plugin;
        this.activeWars = new ConcurrentHashMap<>();
        this.warsByClanPair = new ConcurrentHashMap<>();
    }

    public void registerWar(ClanWar war) {
        activeWars.put(war.getId(), war);
        warsByClanPair.put(getWarKey(war.getAttackerClanId(), war.getDefenderClanId()), war);
    }

    private String getWarKey(int clanIdA, int clanIdB) {
        int first = Math.min(clanIdA, clanIdB);
        int second = Math.max(clanIdA, clanIdB);
        return first + "-" + second;
    }

    public ClanWar declareWar(Clan attacker, Clan defender) {
        ClanWar war = new ClanWar(attacker.getId(), defender.getId());

        int warId = plugin.getDatabaseManager().createWar(war).join();
        if (warId == -1) {
            return null;
        }

        war.setId(warId);
        registerWar(war);

        attacker.addWar(defender.getId());
        defender.addWar(attacker.getId());

        String declaredMessage = plugin.getMessageManager().get("broadcast.war-declared",
                MessageManager.of("clan", attacker.getColoredName(), "enemy", defender.getColoredName()));
        attacker.broadcastMessage(declaredMessage);
        defender.broadcastMessage(declaredMessage);

        return war;
    }

    public void endWar(int clanIdA, int clanIdB, Integer winnerId) {
        String key = getWarKey(clanIdA, clanIdB);
        ClanWar war = warsByClanPair.get(key);

        if (war == null || !war.isActive()) {
            return;
        }

        war.end(winnerId);
        plugin.getDatabaseManager().updateWar(war);

        activeWars.remove(war.getId());
        warsByClanPair.remove(key);

        Clan clanA = plugin.getClanManager().getClan(clanIdA);
        Clan clanB = plugin.getClanManager().getClan(clanIdB);

        if (clanA != null) {
            clanA.removeWar(clanIdB);
        }
        if (clanB != null) {
            clanB.removeWar(clanIdA);
        }

        String winnerName = "Unentschieden";
        if (winnerId != null) {
            Clan winner = plugin.getClanManager().getClan(winnerId);
            if (winner != null) {
                winnerName = winner.getColoredName();
            }
        }

        String endedMessage = plugin.getMessageManager().get("broadcast.war-ended",
                MessageManager.of("clan", clanA != null ? clanA.getColoredName() : "?",
                        "enemy", clanB != null ? clanB.getColoredName() : "?",
                        "winner", winnerName));
        if (clanA != null) {
            clanA.broadcastMessage(endedMessage);
        }
        if (clanB != null) {
            clanB.broadcastMessage(endedMessage);
        }
    }

    public void surrender(Clan surrendering, Clan opponent) {
        ClanWar war = getWarBetween(surrendering.getId(), opponent.getId());
        if (war == null) {
            return;
        }

        endWar(surrendering.getId(), opponent.getId(), opponent.getId());

        int bonusPoints = plugin.getConfigManager().getWarKillPoints() * 10;
        plugin.getClanManager().addPointsToClan(opponent, bonusPoints);
    }

    public ClanWar getWarBetween(int clanIdA, int clanIdB) {
        return warsByClanPair.get(getWarKey(clanIdA, clanIdB));
    }

    public boolean areAtWar(int clanIdA, int clanIdB) {
        ClanWar war = getWarBetween(clanIdA, clanIdB);
        return war != null && war.isActive();
    }

    public void recordKill(UUID killerUuid, UUID victimUuid, int killerClanId, int victimClanId) {
        ClanWar war = getWarBetween(killerClanId, victimClanId);
        if (war == null || !war.isActive()) {
            return;
        }

        int points = plugin.getConfigManager().getWarKillPoints();
        war.addKill(killerUuid, killerClanId, points);

        plugin.getDatabaseManager().addWarKill(war.getId(), killerUuid, victimUuid, killerClanId, points);
        plugin.getDatabaseManager().updateWar(war);

        Clan killerClan = plugin.getClanManager().getClan(killerClanId);
        if (killerClan != null) {
            plugin.getClanManager().addPointsToClan(killerClan, points);
        }
    }

    public List<ClanWar> getActiveWarsForClan(int clanId) {
        List<ClanWar> wars = new ArrayList<>();
        for (ClanWar war : activeWars.values()) {
            if (war.involves(clanId) && war.isActive()) {
                wars.add(war);
            }
        }
        return wars;
    }

    public List<ClanWar> getWarHistory(int clanId) {
        return plugin.getDatabaseManager().loadWarHistory(clanId);
    }

    public Collection<ClanWar> getAllActiveWars() {
        return Collections.unmodifiableCollection(activeWars.values());
    }
}
