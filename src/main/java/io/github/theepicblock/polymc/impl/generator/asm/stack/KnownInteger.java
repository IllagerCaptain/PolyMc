package io.github.theepicblock.polymc.impl.generator.asm.stack;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.theepicblock.polymc.impl.generator.asm.MethodExecutor;
import io.github.theepicblock.polymc.impl.generator.asm.StackEntryTable;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

public record KnownInteger(int i) implements StackEntry {
    public KnownInteger(boolean b) {
        this(b ? 1 : 0);
    }

    public KnownInteger(char c) {
        this((int)c);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(i);
    }

    @Override
    public <T> T extractAs(Class<T> type) {
        if (type == int.class) {
            return (T)(Integer)i;
        }
        return StackEntry.super.extractAs(type);
    }

    @Override
    public void write(PacketByteBuf buf, StackEntryTable table) {
        buf.writeVarInt(this.i);
    }

    public static StackEntry read(PacketByteBuf buf, StackEntryTable table) {
        return new KnownInteger(buf.readVarInt());
    }

    @Override
    public @NotNull StackEntry getField(String name) throws MethodExecutor.VmException {
        if (name.equals("value")) return this; // I like dealing with boxing in dumb ways
        return StackEntry.super.getField(name);
    }

    @Override
    public boolean isConcrete() {
        return true;
    }
}