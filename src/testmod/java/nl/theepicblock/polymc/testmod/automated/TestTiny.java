package nl.theepicblock.polymc.testmod.automated;

import io.github.theepicblock.polymc.impl.generator.asm.Mapping;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import java.lang.reflect.Method;

import static nl.theepicblock.polymc.testmod.automated.TestUtil.*;

public class TestTiny implements FabricGameTest {
    @Override
    public void invokeTestMethod(TestContext context, Method method) {
        System.out.println("Current runtime mappings: "+FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace());
        FabricGameTest.super.invokeTestMethod(context, method);
    }

    @SuppressWarnings("ConstantValue")
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void testInvalid(TestContext ctx) {
        var mapping = Mapping.runtimeToObfFromClasspath();

        var bullshit = "nfeiofurhwgoieiw";

        assertEq(mapping.getClassname(bullshit), bullshit, "Non-existant classnames should be ignored");
        var bsClassDef1 = mapping.getClassByInputName(bullshit);
        var bsClassDef2 = mapping.getClassByOutputName(bullshit);

        assertTrue(bsClassDef1 != null, "class def shouldn't be null, even if the class doesn't exist");
        assertTrue(bsClassDef2 != null, "class def shouldn't be null, even if the class doesn't exist");

        assertEq(bsClassDef1.getFieldName(bullshit), bullshit, "Non-existant fieldnames should be ignored");
        assertEq(bsClassDef2.getFieldName(bullshit), bullshit, "Non-existant fieldnames should be ignored");
        assertEq(bsClassDef1.getMethodName(bullshit, "bsDesc"), new Mapping.MethodDef(bullshit, "bsDesc"), "Non-existant methodnames should be ignored");
        assertEq(bsClassDef2.getMethodName(bullshit, "bsDesc"), new Mapping.MethodDef(bullshit, "bsDesc"), "Non-existant methodnames should be ignored");

        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void testClass(TestContext ctx) {
        var mapping = Mapping.runtimeToObfFromClasspath();

        var fapiResolver = FabricLoader.getInstance().getMappingResolver();
        var intermClass = "net.minecraft.class_2960"; // This is the Identifier class

        // Since these are retrieved from fapi, we assume them to be correct
        var runtimeClass = fapiResolver.mapClassName("intermediary", intermClass);
        var obfClass = fapiResolver.unmapClassName("official", runtimeClass);

        assertEq(
            mapping.getClassname(runtimeClass),
            obfClass,
            "Failed to map the identifier class from runtime to obfuscated");

        assertEq(
            mapping.getClassByInputName(runtimeClass.replace(".", "/")),
            mapping.getClassByOutputName(obfClass.replace(".", "/")),
            "getClassByInputName and getClassByOutputName should give same results if the names match");

        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void testField(TestContext ctx) {
        var mapping = Mapping.runtimeToObfFromClasspath();

        var fapiResolver = FabricLoader.getInstance().getMappingResolver();
        var intermClass = "net.minecraft.class_2960"; // Identifier
        var intermField = "field_13353"; // Identifier#namespace
        var desc = "Ljava/lang/String;";

        // Since these are retrieved from fapi, we assume them to be correct
        var runtimeClass = fapiResolver.mapClassName("intermediary", intermClass);
        var obfClass = fapiResolver.unmapClassName("official", runtimeClass);
        var runtimeField = fapiResolver.mapFieldName("intermediary", intermClass, intermField, desc);

        var classDef = mapping.getClassByInputName(runtimeClass.replace(".", "/"));
        var obfField = classDef.getFieldName(runtimeField);
        assertDifferent(obfField, runtimeField, "No remapping was done");

        // We can't get runtime -> obfuscated via fapi, so we're doing obfuscated -> runtime and checking if that matches our previous results
        assertEq(
            fapiResolver.mapFieldName("official", obfClass, obfField, desc),
            runtimeField,
            "Failed to map Identifier#withPath(String) from runtime to obfuscated");

        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void testMethod(TestContext ctx) {
        var mapping = Mapping.runtimeToObfFromClasspath();

        var fapiResolver = FabricLoader.getInstance().getMappingResolver();
        var intermediaryClass = "net.minecraft.class_2960"; // Identifier
        var intermediaryMeth = "method_45136"; // Identifier#withPath(String)
        var intermediaryDesc = "(Ljava/lang/String;)Lnet/minecraft/class_2960;";

        // Since these are retrieved from fapi, we assume them to be correct
        var runtimeClass = fapiResolver.mapClassName("intermediary", intermediaryClass);
        var obfClass = fapiResolver.unmapClassName("official", runtimeClass);
        var runtimeMeth = fapiResolver.mapMethodName("intermediary", intermediaryClass, intermediaryMeth, intermediaryDesc);
        var runtimeDesc = "(Ljava/lang/String;)L"+runtimeClass.replace(".", "/")+";";
        var obfDesc = "(Ljava/lang/String;)L"+obfClass.replace(".", "/")+";";

        var classDef = mapping.getClassByInputName(runtimeClass.replace(".", "/"));
        var obfMethDef = classDef.getMethodName(runtimeMeth, runtimeDesc);

        assertDifferent(obfMethDef.name(), runtimeMeth, "No remapping was done");
        assertEq(obfMethDef.desc(), obfDesc, "Descriptor does not match");

        // We can't get runtime -> obfuscated via fapi, so we're doing obfuscated -> runtime and checking if that matches our previous results
        assertEq(
            fapiResolver.mapMethodName("official", obfClass, obfMethDef.name(), obfMethDef.desc()),
            runtimeMeth,
            "Failed to map Identifier#withPath(String) from runtime to obfuscated");

        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void testDesc(TestContext ctx) {
        var mapping = Mapping.runtimeToObfFromClasspath();

        var fapiResolver = FabricLoader.getInstance().getMappingResolver();
        var intermediaryClass = "net.minecraft.class_2960";
        var runtimeClass = fapiResolver.mapClassName("intermediary", intermediaryClass);
        var obfClass = fapiResolver.unmapClassName("official", runtimeClass);

        var desc = "(IIL<class>;BZ)L<class>;";
        var runtimeDesc = desc.replace("<class>", runtimeClass.replace(".", "/"));
        var obfDesc = desc.replace("<class>", obfClass.replace(".", "/"));

        assertEq(mapping.remapDescriptor(runtimeDesc), obfDesc, "Failed to remap descriptor");

        ctx.complete();
    }
}