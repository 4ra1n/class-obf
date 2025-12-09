package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.test.samples.ComboSample;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class BadAnnoLevelTest {

    private int countAnnotations(List<?> annotations) {
        if (annotations == null) return 0;
        // In this specific obfuscator, the descriptor is the ASCII art string.
        // We just count how many annotations are there.
        // Assuming the sample class doesn't have other invisible annotations for simplicity,
        // or we can just check if count > 0.
        return annotations.size();
    }

    private void verifyLevel(int level, boolean expectClass, boolean expectField, boolean expectMethod) {
        BaseConfig cfg = ObfJUnitUtil.baseOff();
        cfg.setEnableBadAnno(true);
        cfg.setBadAnnoLevel(level);
        // Ensure we have a text file if needed, but default generates one if missing or uses default.
        // The visitor uses ObfEnv.config.getBadAnnoTextFile() which defaults to null/empty in baseOff?
        // baseOff uses Default() which sets text file to "bad-anno.txt" in BaseConfig.Default()??
        // No, BaseConfig.Default() sets it? Let's check.
        // BaseConfig.Default() does NOT set badAnnoTextFile.
        // But BadAnnoClassVisitor handles null/empty by using DEFAULT string.

        byte[] obf = ObfJUnitUtil.obfuscateBytes(ComboSample.class, cfg);

        ClassReader cr = new ClassReader(obf);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        // Check Class Annotation
        int classAnnoCount = countAnnotations(cn.invisibleAnnotations);
        System.out.println("Level " + level + " Class Annos: " + classAnnoCount);
        if (classAnnoCount > 0) {
            System.out.println("  Class Anno Desc: " + ((org.objectweb.asm.tree.AnnotationNode) cn.invisibleAnnotations.get(0)).desc);
        }

        if (expectClass) {
            Assertions.assertTrue(classAnnoCount > 0, "Level " + level + ": Class should have annotation");
        } else {
            Assertions.assertEquals(0, classAnnoCount, "Level " + level + ": Class should NOT have annotation");
        }

        // Check Field Annotation
        boolean foundFieldAnno = false;
        for (FieldNode fn : cn.fields) {
            int count = countAnnotations(fn.invisibleAnnotations);
            if (count > 0) {
                foundFieldAnno = true;
                System.out.println("Level " + level + " Field " + fn.name + " Annos: " + count);
                System.out.println("  Field Anno Desc: " + ((org.objectweb.asm.tree.AnnotationNode) fn.invisibleAnnotations.get(0)).desc);
                break;
            }
        }
        if (expectField) {
            Assertions.assertTrue(foundFieldAnno, "Level " + level + ": Fields should have annotation");
        } else {
            Assertions.assertFalse(foundFieldAnno, "Level " + level + ": Fields should NOT have annotation");
        }

        // Check Method Annotation
        boolean foundMethodAnno = false;
        for (MethodNode mn : cn.methods) {
            int count = countAnnotations(mn.invisibleAnnotations);
            System.out.println("Level " + level + " Method " + mn.name + " Annos: " + count);
            if (count > 0) {
                foundMethodAnno = true;
                System.out.println("  Method Anno Desc: " + ((org.objectweb.asm.tree.AnnotationNode) mn.invisibleAnnotations.get(0)).desc);
                // Don't break immediately to see all methods
            }
        }
        if (expectMethod) {
            Assertions.assertTrue(foundMethodAnno, "Level " + level + ": Methods should have annotation");
        } else {
            Assertions.assertFalse(foundMethodAnno, "Level " + level + ": Methods should NOT have annotation");
        }
    }

    @Test
    public void testLevel1() {
        // Level 1: Only Class
        verifyLevel(1, true, false, false);
    }

    @Test
    public void testLevel2() {
        // Level 2: Class + Field
        verifyLevel(2, true, true, false);
    }

    @Test
    public void testLevel3() {
        // Level 3: Class + Field + Method
        verifyLevel(3, true, true, true);
    }
}
