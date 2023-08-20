package io.github.theepicblock.polymc.impl.generator.asm.stack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.theepicblock.polymc.impl.generator.asm.*;
import io.github.theepicblock.polymc.impl.generator.asm.MethodExecutor.VmException;
import io.github.theepicblock.polymc.impl.generator.asm.VirtualMachine.Clazz;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;

import java.util.Objects;

public record KnownVmObject(@NotNull Clazz type, @NotNull CowCapableMap<@NotNull String> fields) implements StackEntry {
    public KnownVmObject(@NotNull Clazz type) {
        this(type, new CowCapableMap<>());
    }

    @Override
    public @NotNull StackEntry getField(String name) {
        var value = this.fields().get(name);
        if (value == null) {
            // We need to get the default value depending on the type of the field
            var field = AsmUtils.getFields(type)
                    .filter(f -> f.name.equals(name))
                    .filter(f -> !AsmUtils.hasFlag(f, Opcodes.ACC_STATIC))
                    .findAny().orElse(null);
            if (field == null) {
                return new UnknownValue("Don't know value of field '"+name+"'");
            }
            var result = switch (field.desc) {
                case "I", "Z", "S", "C", "B" -> new KnownInteger(0);
                case "J" -> new KnownLong(0);
                case "F" -> new KnownFloat(0);
                case "D" -> new KnownDouble(0);
                default -> KnownObject.NULL;
            };
            this.fields.put(name, result);
            return result;
        }
        return value;
    }

    @Override
    public void setField(String name, @NotNull StackEntry e) {
        this.fields().put(name, e);
    }

    @Override
    public <T> T extractAs(Class<T> type) {
        if ((type == int.class && this.type.getNode().name.equals("java/lang/Integer")) ||
                (type == long.class && this.type.getNode().name.equals("java/lang/Long")) ||
                (type == float.class && this.type.getNode().name.equals("java/lang/Float")) ||
                (type == double.class && this.type.getNode().name.equals("java/lang/Double"))) {
            return this.getField("value").extractAs(type);
        }
        return StackEntry.super.extractAs(type);
    }

    @Override
    public void write(PacketByteBuf buf, StackEntryTable table) {
        buf.writeString(this.type.name());
        this.fields.write(buf, PacketByteBuf::writeString, table::writeEntry);
    }

    public static VirtualMachine hehe = new VirtualMachine(new ClientClassLoader(), new VirtualMachine.VmConfig() {}); // TODO
    public static StackEntry read(PacketByteBuf buf, StackEntryTable table) {
        var name = buf.readString();
        var fields = new CowCapableMap<String>();
        try {
            return new KnownVmObject(hehe.getClass(name), fields);
        } catch (VmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finalizeRead(PacketByteBuf buf, StackEntryTable table) {
        this.fields.readFromByteBuf(buf, PacketByteBuf::readString, table::readEntry);
    }

    @Override
    public JsonElement toJson() {
        var element = new JsonObject();
        this.fields.forEachImmutable((key, val) -> {
            element.add(key, val.toJson());
        });
        return element;
    }

    @Override
    public StackEntry simplify(VirtualMachine vm, Reference2ReferenceOpenHashMap<StackEntry,StackEntry> simplificationCache) throws VmException {
        if (!vm.getConfig().shouldSimplifyVmObjects()) return this;

        if (simplificationCache.containsKey(this)) return simplificationCache.get(this);
        simplificationCache.put(this, this);
        var tmpMap = new Object2ObjectOpenHashMap<String, StackEntry>();
        this.fields.forEachImmutable((field, val) -> {
            try {
                tmpMap.put(field, val.simplify(vm, simplificationCache));
            } catch (VmException ignored) {}
        });
        this.fields.putAll(tmpMap);
        return this;
    }

    @Override
    public boolean isConcrete() {
        return true;
    }

    @Override
    public StackEntry copyTmp(Reference2ReferenceOpenHashMap<StackEntry,StackEntry> copyCache) {
        if (copyCache.containsKey(this)) return copyCache.get(this);
        var newMap = new CowCapableMap<String>();
        var newObj = new KnownVmObject(this.type, newMap);
        copyCache.put(this, newObj);
        newMap.clearAndCopy(this.fields, copyCache);
        return newObj;
    }

    @Override
    public StackEntry copy(Reference2ReferenceOpenHashMap<StackEntry,StackEntry> copyCache) {
        if (copyCache.containsKey(this)) return copyCache.get(this);
        var newMap = new CowCapableMap<String>();
        var newObj = new KnownVmObject(this.type, newMap);
        copyCache.put(this, newObj);
        this.fields.forEachImmutable((key, value) -> {
            newMap.put(key, value.copy(copyCache));
        });
        return newObj;
    }

    // We're overriding these because the type shouldn't really matter
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnownVmObject that = (KnownVmObject)o;
        return Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }

    @Override
    public String toString() {
        return "KnownVmObject["+type+"]";
    }
}
