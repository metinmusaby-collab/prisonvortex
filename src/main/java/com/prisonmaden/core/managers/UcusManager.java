package com.prisonmaden.core.managers;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UcusManager {

    private final PrisonMaden plugin;

    public UcusManager(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    /**
     * Sadece kendi maden dunyasindayken calisir; ucusu ac/kapa.
     */
    public void acKapaDegistir(Player oyuncu) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());

        if (!veri.madeniVarMi() || !oyuncu.getWorld().getName().equals(veri.getDunyaAdi())) {
            mesajGonder(oyuncu, "&cUcus sadece kendi madeninde kullanilabilir!");
            return;
        }

        boolean yeniDurum = !oyuncu.getAllowFlight();
        oyuncu.setAllowFlight(yeniDurum);
        oyuncu.setFlying(yeniDurum);
        mesajGonder(oyuncu, "&7Ucus artik " + (yeniDurum ? "&aACIK" : "&cKAPALI") + "&7!");
    }

    /**
     * Maden dunyasindan cikince ucusu guvenlik amacli kapatir.
     */
    public void ucusuKapat(Player oyuncu) {
        if (oyuncu.getAllowFlight() && !oyuncu.isOp() && oyuncu.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            oyuncu.setAllowFlight(false);
            oyuncu.setFlying(false);
        }
    }

    private void mesajGonder(Player oyuncu, String mesaj) {
        oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &7" + mesaj));
    }
}
