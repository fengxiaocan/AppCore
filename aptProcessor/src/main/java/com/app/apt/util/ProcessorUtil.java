package com.app.apt.util;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class ProcessorUtil {

    public static boolean isActivity(TypeElement classElement) {
        return ProcessorUtil.isInstanceof(classElement, "android.app.Activity");
    }

    public static boolean isView(TypeElement classElement) {
        return ProcessorUtil.isInstanceof(classElement, "android.view.View");
    }

    public static boolean isFragment(TypeElement classElement) {
        return ProcessorUtil.isInstanceof(classElement, "android.app.Fragment") ||
               ProcessorUtil.isInstanceof(classElement, "androidx.fragment.app.Fragment");
    }

    public static Symbol.ClassSymbol getSuperTypeElement(Type.ClassType classType,
            String... superClass)
    {
        if (classType != null && classType.tsym != null &&
            classType.tsym instanceof Symbol.ClassSymbol)
        {
            Symbol.ClassSymbol typeSymbol = (Symbol.ClassSymbol) classType.asElement();

            for (String aClass : superClass) {
                if (typeSymbol.toString().equals(aClass)) {
                    return typeSymbol;
                }
            }

            Type superclass = typeSymbol.getSuperclass();
            if (superclass instanceof Type.ClassType) {
                return getSuperTypeElement(((Type.ClassType) superclass), superClass);
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
        return isInstanceof(parent, className);
    }

    /**
     * 是否继承某个类
     *
     * @param element
     * @param className
     * @return
     */
    public static boolean isInstanceof(TypeMirror element, String className) {
        if (element.toString().equals(className)) {
            return true;
        }
        if (element instanceof DeclaredType) {
            Element elt = ((DeclaredType) element).asElement();
            if (elt instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) elt;
                return isInstanceof(typeElement, className);
            }
        }
        return false;
    }

    /**
     * 是否实现某个接口
     *
     * @param element
     * @param className
     * @return
     */
    public static boolean isInterfacesOf(VariableElement element, String className) {
        if (element.asType() instanceof Type.ClassType) {
            Type.ClassType classType = (Type.ClassType) element.asType();
            return isInterfacesOf(classType, className);
        }
        return false;
    }

    public static boolean isInterfacesOf(Type.ClassType classType, String className) {
        List<Type> interfaces_field = classType.interfaces_field;
        if (interfaces_field != null) {
            for (Type type : interfaces_field) {
                if (type.tsym.toString().equals(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 能够判断是否java.util.ArrayList<com.app.apptest.ParcelableBean>数组类型
     *
     * @param element
     * @param className
     * @return
     */
    public static boolean isInterfacesOfList(VariableElement element, String className) {
        if (element.asType() instanceof Type.ClassType) {
            Type.ClassType classType = (Type.ClassType) element.asType();
            return isInterfacesOfList(classType, className);
        }
        return false;
    }

    /**
     * 能够判断是否java.util.ArrayList<com.app.apptest.ParcelableBean>数组类型
     *
     * @param classType
     * @param className
     * @return
     */
    public static boolean isInterfacesOfList(Type.ClassType classType, String className) {
        List<Type> types = classType.getTypeArguments();
        if (types != null) {
            for (Type type : types) {
                if (type instanceof Type.ClassType) {
                    if (isInterfacesOf((Type.ClassType) type, className)) {
                        return true;
                    }
                } else if (type instanceof Type.ArrayType) {
                    if (isInterfacesOfArray((Type.ArrayType) type, className)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 某个数组的类是否实现某个接口
     *
     * @param element
     * @param className
     * @return
     */
    public static boolean isInterfacesOfArray(VariableElement element, String className) {
        if (element.asType() instanceof Type.ArrayType) {
            return isInterfacesOfArray((Type.ArrayType) element.asType(), className);
        }
        return false;
    }

    /**
     * 某个数组的类是否实现某个接口
     *
     * @param arrayType
     * @param className
     * @return
     */
    public static boolean isInterfacesOfArray(Type.ArrayType arrayType, String className) {
        Type elemtype = arrayType.elemtype;
        if (elemtype instanceof Type.ClassType) {
            List<Type> interfaces_field = ((Type.ClassType) elemtype).interfaces_field;
            for (Type type : interfaces_field) {
                System.out.println("elemtype instanceof Type.ClassType =" + type.toString());
                if (type.toString().equals(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否实现某个接口或者是否是某个类的子类
     *
     * @param classType
     * @param interfaceClass
     * @return
     */
    public static boolean isInstanceof(Class<?> classType, Class<?> interfaceClass) {
        return interfaceClass.isAssignableFrom(classType);
    }

}
