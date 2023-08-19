package io.github.theepicblock.polymc.impl.generator.asm.stack;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.theepicblock.polymc.impl.generator.asm.MethodExecutor;
import io.github.theepicblock.polymc.impl.generator.asm.StackEntryTable;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

public record KnownFloat(float i) implements StackEntry {
    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(i);
    }

    @Override
    public <T> T extractAs(Class<T> type) {
        if (type == float.class) {
            return (T)(Float)i;
        }
        return StackEntry.super.extractAs(type);
    }

    @Override
    public void write(PacketByteBuf buf, StackEntryTable table) {
        buf.writeFloat(this.i);
    }

    public static StackEntry read(PacketByteBuf buf, StackEntryTable table) {
        return new KnownFloat(buf.readFloat());
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