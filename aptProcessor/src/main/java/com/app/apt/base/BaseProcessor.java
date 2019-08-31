package com.app.apt.base;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

public abstract class BaseProcessor extends AbstractProcessor {
    // apt 相关类
    protected Filer filer;
    protected Elements mElementUtils;
    protected Messager messager;

    // javac 编译器相关类
    protected Trees trees;
    protected TreeMaker treeMaker;
    protected Names names;
    protected Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
        trees = Trees.instance(processingEnvironment);
        Context context = ((JavacProcessingEnvironment) processingEnvironment).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
        types = Types.instance(context);
    }

    /**
     * 支持的java版本
     * 用来指定你使用的Java版本。通常这里返回SourceVersion.latestSupported()。
     * 然而，如果你有足够的理由只支持Java 7的话，你也可以返回SourceVersion.RELEASE_7。
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 这里你必须指定，这个注解处理器是注册给哪个注解的。
     * 注意，它的返回值是一个字符串的集合，包含本处理器想要处理的注解类型的合法全称。
     * 换句话说，你在这里定义你的注解处理器注册到哪些注解上。
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        for (Class aClass : annotationTypes()) {
            supportTypes.add(aClass.getCanonicalName());
        }
        return supportTypes;
    }

    protected abstract Class[] annotationTypes();

    protected void javaFile(String packageName, TypeSpec typeSpec) {
        try {
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
            //　生成文件
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void printMessage(CharSequence charSequence) {
        messager.printMessage(Diagnostic.Kind.NOTE, charSequence);
    }

    protected String getPackageName(TypeElement classElement) {
        String string = mElementUtils.getPackageOf(classElement).asType().toString();
        String[] split = string.split("\\.");
        if (split.length > 3) {
            return split[0] + "." + split[1] + "." + split[2];
        } else {
            return string;
        }
    }

}
