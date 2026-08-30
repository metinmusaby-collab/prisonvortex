package com.example.skriptveri.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.example.skriptveri.util.DataManager;

/**
 * Kullanim:
 *   set {_p} to skript veri "puan" of player
 *   send "%skript veri \"sunucu_durumu\"%" to player
 */
@Name("Skript Veri")
@Description("Skript veri sisteminden bir degeri okur. Oyuncu belirtilmezse global veri okunur.")
@Examples({
        "broadcast \"Puan: %skript veri \"\"puan\"\" of player%\""
})
@Since("1.0.0")
public class ExprSkriptVeri extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprSkriptVeri.class, Object.class, ch.njol.skript.lang.ExpressionType.SIMPLE,
                "skript veri %string% [of %-player%]");
    }

    private Expression<String> keyExpr;
    private Expression<Player> playerExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        keyExpr = (Expression<String>) exprs[0];
        playerExpr = (Expression<Player>) exprs[1];
        return true;
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        String key = keyExpr.getSingle(event);
        if (key == null) return null;

        Player player = playerExpr != null ? playerExpr.getSingle(event) : null;
        Object value = (player != null)
                ? DataManager.getPlayer(player, key)
                : DataManager.getGlobal(key);

        if (value == null) return new Object[0];
        return new Object[]{value};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "skript veri " + keyExpr.toString(event, debug);
    }
}
