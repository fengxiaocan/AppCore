package com.app.apt.base;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.Field;
import java.util.HashSet;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public abstract class BaseClassCreatorProxy {
    protected String mBindingClassName;//生成的绑定类
    protected String mPackageName;//包名
    protected TypeElement mTypeElement;//依赖的类
    protected ClassName mHostClassName;//依赖的类的名
    protected ClassName mBindingClass;//生成的类的名

    public BaseClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        this.mTypeElement = classElement;
        //获取依赖的类的包名
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();

        this.mPackageName = packageName;
        //获取依赖的类的名称
        String className = mTypeElement.getSimpleName().toString();
        this.mBindingClassName = createBindingClassName(className);
        mHostClassName = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        mBindingClass = ClassName.get(mPackageName, mBindingClassName);
    }

    protected abstract String createBindingClassName(String className);

    public String getPackageName() {
        return mPackageName;
    }

    public String getBindingClassName() {
        return mBindingClassName;
    }

    /**
     * 生成成员变量
     *
     * @return
     */
    protected Iterable<FieldSpec> generateFields(Modifier modifier, Field[] fields) {
        HashSet<FieldSpec> fieldSpecs = new HashSet();
        for (Field field : fields) {
            fieldSpecs.add(
                    FieldSpec.builder(ClassName.get(field.getType()), field.getName(), modifier)
                             .build());
        }
        return fieldSpecs;
    }


    /**
     * 生成私有的成员变量
     *
     * @param fields
     * @return
     */
    protected Iterable<FieldSpec> generateFields(Field[] fields) {
        return generateFields(Modifier.PRIVATE, fields);
    }

    /**
     * 转换为成员变量
     *
     * @param modifier
     * @param element
     * @return
     */
    protected FieldSpec toField(Modifier modifier, VariableElement element)
    {
        String name = element.getSimpleName().toString();
        TypeMirror typeMirror = element.asType();
        return FieldSpec.builder(ClassName.get(typeMirror), name, modifier).build();
    }

    /**
     * 转换为成员变量
     *
     * @param element
     * @return
     */
    protected FieldSpec toField(VariableElement element)
    {
        return toField(Modifier.PRIVATE, element);
    }

    /**
     * 构建一个Builder,返回值为本身
     *
     * @param modifier
     * @return
     */
    protected MethodSpec.Builder buildFieldMethod(Modifier modifier, String name,
            TypeName returnType)
    {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name)
                                                     .addModifiers(modifier)
                                                     .returns(mBindingClass)
                                                     .addParameter(returnType, name);
        methodBuilder.addCode(String.format("this.%s = %s;", name, name));
        methodBuilder.addCode("\n");
        methodBuilder.addCode("return this;\n");
        return methodBuilder;
    }

    /**
     * 构建一个没有返回值的Setter方法
     *
     * @param modifier
     * @return
     */
    protected MethodSpec.Builder fieldMethod(Modifier modifier, String name, TypeName returnType)
    {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name)
                                                     .addModifiers(modifier)
                                                     .returns(void.class)
                                                     .addParameter(returnType, name);
        methodBuilder.addCode(String.format("this.%s = %s;", name, name));
        methodBuilder.addCode("\n");
        return methodBuilder;
    }

    /**
     * 构建一个Builder Setter方法,返回值为本身
     *
     * @param modifier
     * @return
     */
    protected MethodSpec.Builder buildSetFieldMethod(Modifier modifier, String name,
            TypeName returnType)
    {
        String specName;
        if (name.length() == 1) {
            specName = name.toUpperCase();
        } else {
            specName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(specName)
                                                     .addModifiers(modifier)
                                                     .returns(mBindingClass)
                                                     .addParameter(returnType, name);
        methodBuilder.addCode(String.format("this.%s = %s;", name, name));
        methodBuilder.addCode("\n");
        methodBuilder.addCode("return this;\n");
        return methodBuilder;
    }

    /**
     * 构建一个没有返回值的Setter方法
     *
     * @param modifier
     * @return
     */
    protected MethodSpec.Builder fieldSetMethod(Modifier modifier, String name, TypeName returnType)
    {
        String specName;
        if (name.length() == 1) {
            specName = name.toUpperCase();
        } else {
            specName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(specName)
                                                     .addModifiers(modifier)
                                                     .returns(mBindingClass)
                                                     .addParameter(returnType, name);

        methodBuilder.addCode(String.format("this.%s = %s;", name, name));
        methodBuilder.addCode("\n");
        return methodBuilder;
    }

    /**
     * 构建Getter  区别在于加上get前缀+驼峰命名
     *
     * @param modifier
     * @param name
     * @param returnType
     * @return
     */
    protected MethodSpec.Builder getFieldMethod(Modifier modifier, String name, TypeName returnType)
    {
        String specName;
        if (name.length() == 1) {
            specName = name.toUpperCase();
        } else {
            specName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(specName)
                                                     .addModifiers(modifier)
                                                     .returns(returnType);
        methodBuilder.addCode(String.format("return %s;", name));

        return methodBuilder;
    }

    /**
     * 构建Getter 区别在于直接以名字做方法名
     *
     * @param modifier
     * @param name
     * @param returnType
     * @return
     */
    protected MethodSpec.Builder buildGetFieldMethod(Modifier modifier, String name,
            TypeName returnType)
    {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name)
                                                     .addModifiers(modifier)
                                                     .returns(returnType);
        methodBuilder.addCode(String.format("return %s;", name));

        return methodBuilder;
    }

}
