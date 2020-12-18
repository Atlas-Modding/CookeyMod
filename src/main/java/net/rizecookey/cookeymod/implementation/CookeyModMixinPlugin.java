package net.rizecookey.cookeymod.implementation;

import net.fabricmc.loader.api.FabricLoader;
import net.rizecookey.cookeymod.annotation.mixin.Incompatible;
import net.rizecookey.cookeymod.annotation.mixin.ModSpecific;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public class CookeyModMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Logger logger = LogManager.getLogger("CookeyMod");
        try {
            ClassNode mixin = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName, false);

            if (mentionsActiveMods(Incompatible.class, mixin)) {
                logger.warn("[" + getClass().getSimpleName() + "] Mod \"" + getFirstMentionedActiveMod(Incompatible.class, mixin) + "\" is marked incompatible with mixin \"" + mixinClassName + "\", cancelling application.");
                return false;
            }
            if (hasAnnotation(ModSpecific.class, mixin)) {
                if (mentionsActiveMods(ModSpecific.class, mixin)) {
                    logger.info("[" + getClass().getSimpleName() + "] Loading mod-specific mixin \"" + mixinClassName + "\" since mod \"" + getFirstMentionedActiveMod(ModSpecific.class, mixin) + "\" is present.");
                    return true;
                }
                else {
                    return false;
                }
            }
            return true;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationClass, ClassNode mixin) {
        return Annotations.getInvisible(mixin, annotationClass) != null;
    }

    @SuppressWarnings("unchecked")
    public String getFirstMentionedActiveMod(Class<? extends Annotation> annotationClass, ClassNode mixin) {
        FabricLoader fabricLoader = FabricLoader.getInstance();
        AnnotationNode annotationNode = Annotations.getInvisible(mixin, annotationClass);
        List<Object> values;
        if (annotationNode != null && (values = annotationNode.values) != null) {
            for (int i = 0; i < values.size(); i += 2) {
                Object pKey = values.get(i);
                Object pValue = values.size() > i + 1 ? values.get(i + 1) : null;
                if ("value".equals(pKey) && pValue instanceof List) {
                    for (String modId : (List<String>) pValue) {
                        if (fabricLoader.isModLoaded(modId)) {
                            return modId;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean mentionsActiveMods(Class<? extends Annotation> annotationClass, ClassNode mixin) {
        return getFirstMentionedActiveMod(annotationClass, mixin) != null;
    }
}
