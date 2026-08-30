package com.example.skriptveri.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.example.skriptveri.util.DataManager;

/**
 * Kullanim:
 *   set skript veri "isim" to "deger"
 *   set skript veri "isim" to 5 for player
 *   set skript veri "isim" to "%player's name%" for {_hedef}
 */
@Name("Skript Veri Ata")
@Description("Skript uzerinden isimlendirilmis bir veri atar. Oyuncuya ozel ya da global olabilir. " +
        "PlaceholderAPI uzerinden %skriptveri_isim% seklinde okunabilir.")
@Examples({
        "set skript veri \"puan\" to 10 for player",
        "set skript veri \"sunucu_durumu\" to \"acik\""
})
@Since("1.0.0")
public class EffSetSkriptVeri extends Effect {

    static {
        Skript.registerEffect(EffSetSkriptVeri.class,
                "set skript veri %string% to %object% [for %-player%]");
    }

    private Expression<String> keyExpr;
    private Expression<Object> valueExpr;
    private Expression<Player> playerExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        keyExpr = (Expression<String>) exprs[0];
        valueExpr = (Expression<Object>) exprs[1];
        playerExpr = (Expression<Player>) exprs[2];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        String key = keyExpr.getSingle(event);
        Object value = valueExpr.getSingle(event);
        if (key == null) return;

        Player player = playerExpr != null ? playerExpr.getSingle(event) : null;

        if (player != null) {
            DataManager.setPlayer(player, key, value);
        } else {
            DataManager.setGlobal(key, value);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "set skript veri " + keyExpr.toString(event, debug) + " to " + valueExpr.toString(event, debug);
    }
}
