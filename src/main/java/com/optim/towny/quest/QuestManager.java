package com.optim.towny.quest;

import com.optim.towny.OptimTowny;
import com.optim.towny.town.Town;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Oyunculara rastgele görevler atar, ilerlemeyi takip eder ve tamamlanınca ödül +
 * rütbe ilerleme sayacı verir.
 */
public class QuestManager {

    private final OptimTowny plugin;
    private final List<Quest> questPool = new ArrayList<>();

    // oyuncu -> aktif görevler (questId -> ilerleme)
    private final Map<UUID, Map<String, Integer>> progress = new HashMap<>();
    private final Map<UUID, Set<String>> activeQuests = new HashMap<>();
    private final Map<UUID, Integer> completedCount = new HashMap<>();

    public QuestManager(OptimTowny plugin) {
        this.plugin = plugin;
        seedDefaultQuests();
    }

    private void seedDefaultQuests() {
        questPool.add(new Quest("odun_kes", "20 odun kütüğü kır", Quest.Type.BLOK_KIR, 20, 50));
        questPool.add(new Quest("taş_kaz", "40 taş kaz", Quest.Type.BLOK_KIR, 40, 60));
        questPool.add(new Quest("zombi_avla", "10 zombi öldür", Quest.Type.MOB_OLDUR, 10, 80));
        questPool.add(new Quest("iskelet_avla", "10 iskelet öldür", Quest.Type.MOB_OLDUR, 10, 80));
        questPool.add(new Quest("para_biriktir", "500 para biriktir", Quest.Type.PARA_BIRIKTIR, 500, 100));
    }

    public List<Quest> getAvailableQuests() {
        return questPool;
    }

    public boolean acceptQuest(Player player, String questId) {
        Quest quest = findQuest(questId);
        if (quest == null) return false;
        activeQuests.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(questId);
        progress.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).putIfAbsent(questId, 0);
        return true;
    }

    public Quest findQuest(String id) {
        for (Quest q : questPool) if (q.getId().equalsIgnoreCase(id)) return q;
        return null;
    }

    public Set<String> getActiveQuestIds(UUID player) {
        return activeQuests.getOrDefault(player, Collections.emptySet());
    }

    public int getProgress(UUID player, String questId) {
        return progress.getOrDefault(player, Collections.emptyMap()).getOrDefault(questId, 0);
    }

    public int getCompletedCount(UUID player) {
        return completedCount.getOrDefault(player, 0);
    }

    /**
     * Belirtilen tipte bir ilerleme olayı tetiklendiğinde çağrılır (blok kırma, mob öldürme vb.)
     */
    public void addProgress(Player player, Quest.Type type, int amount) {
        Set<String> active = activeQuests.get(player.getUniqueId());
        if (active == null || active.isEmpty()) return;

        for (String questId : new HashSet<>(active)) {
            Quest quest = findQuest(questId);
            if (quest == null || quest.getTip() != type) continue;

            Map<String, Integer> playerProgress = progress.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
            int current = playerProgress.getOrDefault(questId, 0) + amount;
            playerProgress.put(questId, current);

            if (current >= quest.getHedefMiktar()) {
                completeQuest(player, quest);
            }
        }
    }

    private void completeQuest(Player player, Quest quest) {
        activeQuests.get(player.getUniqueId()).remove(quest.getId());
        completedCount.merge(player.getUniqueId(), 1, Integer::sum);

        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("dil.prefix", ""));
        player.sendMessage(prefix + ChatColor.GREEN + "Görev tamamlandı: " + quest.getAciklama() +
                " (+" + quest.getOdulPara() + " para)");

        Town town = plugin.getTownManager().getTownOf(player.getUniqueId());
        if (town != null) {
            town.deposit(quest.getOdulPara());
            int completed = getCompletedCount(player.getUniqueId());
            if (plugin.getTownRank().canPromote(town, player.getUniqueId(), completed)) {
                plugin.getTownRank().promote(town, player.getUniqueId(), completed);
                player.sendMessage(prefix + ChatColor.GOLD + "Tebrikler! Yeni rütben: " +
                        town.getRank(player.getUniqueId()));
            }
        }
    }
}
