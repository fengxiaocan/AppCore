package com.app.aptprocessor;

import com.squareup.javapoet.ClassName;

import org.checkerframework.checker.units.qual.C;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class ProcessorUtils {
    /**
     * 获取父类对象
     *
     * @param element
     * @return
     */
    public static TypeElement getSuperClass(TypeElement element) {
        TypeMirror parent = element.getSuperclass();
        if (parent instanceof DeclaredType) {
            Element elt = ((DeclaredType) parent).asElement();
            if (elt instanceof TypeElement) {
                return (TypeElement) elt;
            }
        }
        return null;
    }

    /**
     * 判断是否继承某个类
     *
     * @param element
     * @param className
     * @return
     */
    public static boolean isInstanceof(TypeElement element, String className) {
        if (element.getQualifiedName().toString().equals(className)) {
            return true;
        }
        TypeMirror parent = element.getSuperclass();

        if (parent.toString().equals(className)) {
            return true;
        }

        if (parent instanceof DeclaredType) {
            Element elt = ((DeclaredType) parent).asElement();
            if (elt instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) elt;
                return isInstanceof(typeElement, className);
            }
        }
        return false;
    }

    public static boolean isInstanceof(String classType, String className) {
        try {
            if (classType.equals(className)) {
                return true;
            }
            Class<?> aClass = Class.forName(classType);
            return isInstanceof(aClass,className);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isInstanceof(Class<?> classType, String className) {
        try {
            if (classType.getName().equals(className)) {
                return true;
            }
            if (!(classType != Object.class)){
                Class<?> superclass = classType.getSuperclass();
                return isInstanceof(superclass,className);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
