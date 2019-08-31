package com.app.apt.processor;

import com.app.apt.base.BaseProcessor;
import com.app.apt.proxy.BindDataCreatorProxy;
import com.app.aptannotation.BindData;
import com.app.aptannotation.BindDataClass;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

/**
 * 对View进行操作的Processor
 */
@AutoService(Processor.class)
public class BindDataProcessor extends BaseProcessor {
    protected JCTree.JCCompilationUnit rootTree;
    private Map<String, HashSet<TypeElement>> typeProxy = new HashMap<>();
    private Map<String, BindDataCreatorProxy> mProxyMap = new HashMap<>();
    private Map<String, TypeElement> mTypeProxyMap = new HashMap<>();

    @Override
    protected Class[] annotationTypes() {
        return new Class[]{BindDataClass.class, BindData.class};
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        typeProxy.clear();
        mProxyMap.clear();
        mTypeProxyMap.clear();
        //得到所有的注解
        // roundEnvironment.getElementsAnnotatedWith(BindView.class)返回所有被注解了@BindView。
        // 你可能已经注意到，我们并没有说“所有被注解了@BindView”，因为它真的是返回Element的列表。
        // 请记住：Element可以是类、方法、变量等。所以，接下来，我们必须检查这些Element是否是一个类：

        Set<? extends Element> layoutElements = roundEnvironment.getElementsAnnotatedWith(
                BindDataClass.class);
        for (Element element : layoutElements) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                //                String packageName = getPackageName(classElement);
                //                mTypeProxyMap.put(classElement.getQualifiedName().toString(), classElement);
                //                HashSet<TypeElement> list = typeProxy.get(packageName);
                //                if (list == null) {
                //                    HashSet<TypeElement> elements = new HashSet<>();
                //                    elements.add(classElement);
                //                    typeProxy.put(packageName, elements);
                //                } else {
                //                    list.add(classElement);
                //                }


                TreePath treePath = trees.getPath(classElement);
                CompilationUnitTree compilationUnit = treePath.getCompilationUnit();
                rootTree = (JCTree.JCCompilationUnit) compilationUnit;
                JCTree.JCClassDecl tree = (JCTree.JCClassDecl) trees.getTree(classElement);
                tree.accept(new InstanceHolderTreeTranslator(classElement.getSimpleName()));
            }
        }

        //        //通过javapoet生成
        //        for (String key : typeProxy.keySet()) {
        //            HashSet<TypeElement> hashSet = typeProxy.get(key);
        //            TypeSpec.Builder builder = TypeSpec.classBuilder(ClassName.get(key, "C")).addModifiers(
        //                    Modifier.PUBLIC, Modifier.FINAL).addMethod(
        //                    MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());
        //            try {
        //                JavaFile javaFile = JavaFile.builder(key,
        //                        generateBindDataClassJavaCode(builder, hashSet)).build();
        //                //　生成文件
        //                javaFile.writeTo(processingEnv.getFiler());
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //        }
        //
        //
        //        Set<? extends Element> bindDataElements = roundEnvironment.getElementsAnnotatedWith(
        //                BindData.class);
        //
        //        for (Element element : bindDataElements) {
        //            if (element.getKind() == ElementKind.FIELD) {
        //                //可以安全地进行强转，将Element对象转换为一个VariableElement对象
        //                VariableElement variableElement = (VariableElement) element;
        //                TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
        //                String fullClassName = classElement.getQualifiedName().toString();
        //                BindDataCreatorProxy proxy = mProxyMap.get(fullClassName);
        //                if (proxy == null) {
        //                    proxy = new BindDataCreatorProxy(mElementUtils, classElement);
        //                    mProxyMap.put(fullClassName, proxy);
        //                }
        //                proxy.addVariableElement(variableElement);
        //                proxy.setTypeProxy(mTypeProxyMap);
        //            }
        //        }
        //
        //
        //        for (String key : mProxyMap.keySet()) {
        //            BindDataCreatorProxy creatorProxy = mProxyMap.get(key);
        //            javaFile(creatorProxy.getPackageName(),creatorProxy.generateJavaCode());
        //        }
        //
        //        typeProxy.clear();
        //        mProxyMap.clear();
        //        mTypeProxyMap.clear();
        return true;
    }


    private TypeSpec generateBindDataClassJavaCode(TypeSpec.Builder builder,
            HashSet<TypeElement> hashSet)
    {
        for (TypeElement element : hashSet) {
            builder.addType(generateTypeElementCode(element));
        }
        return builder.build();
    }

    /**
     * @param element
     * @return
     */
    private TypeSpec generateTypeElementCode(TypeElement element)
    {
        TypeSpec.Builder type = TypeSpec.classBuilder(element.getSimpleName().toString())
                                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC,
                                                Modifier.FINAL)
                                        .addMethod(MethodSpec.constructorBuilder()
                                                             .addModifiers(Modifier.PRIVATE)
                                                             .build());
        java.util.List<? extends Element> enclosedElements = element.getEnclosedElements();
        for (Element enclosedElement : enclosedElements) {
            String name = enclosedElement.getSimpleName().toString();
            Set<Modifier> modifiers = enclosedElement.getModifiers();
            if (modifiers.contains(Modifier.PRIVATE)) {
                continue;
            }
            if (enclosedElement.getKind().isField()) {
                //成员变量
                FieldSpec.Builder field = FieldSpec.builder(String.class, name, Modifier.PUBLIC,
                        Modifier.STATIC, Modifier.FINAL).initializer(
                        String.format("\"%s.%s\"", element.getQualifiedName().toString(), name));
                type.addField(field.build());

            } else if (enclosedElement.getKind() == ElementKind.METHOD) {
                //方法
                Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) enclosedElement;
                List<Symbol.VarSymbol> parameters = methodSymbol.getParameters();
                //带参数的方法不能作为赋值的变量
                if (parameters == null || parameters.size() == 0) {
                    FieldSpec.Builder field = FieldSpec.builder(String.class, name, Modifier.PUBLIC,
                            Modifier.STATIC, Modifier.FINAL).initializer(
                            String.format("\"%s.%s\"", element.getQualifiedName().toString(),
                                    name));
                    type.addField(field.build());
                }

            }
        }
        return type.build();
    }

    private class InstanceHolderTreeTranslator extends TreeTranslator {
        private Name rootClazzName;

        public InstanceHolderTreeTranslator(Name rootClazzName) {
            this.rootClazzName = rootClazzName;
        }

        @Override
        public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
            treeMaker.at(jcClassDecl.pos);

            ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer();

//            com.sun.tools.javac.util.List<JCTree.JCStatement> list = jcStatements.append(
//                    treeMaker.Return(treeMaker.Ident(names.empty))).toList();

            JCTree.JCBlock body = treeMaker.Block(0, jcStatements.toList());

            JCTree.JCMethodDecl jcMethodDecl = treeMaker.MethodDef(
                    treeMaker.Modifiers(Flags.PUBLIC), names.fromString("sInstance"),
                    treeMaker.TypeIdent(TypeTag.VOID), List.<JCTree.JCTypeParameter>nil(),
                    List.<JCTree.JCVariableDecl>nil(), List.<JCTree.JCExpression>nil(),
                    body, null);

            try {
                jcClassDecl.defs = jcClassDecl.defs.prepend(jcMethodDecl);
            } catch (Exception e) {
            }

            super.visitClassDef(jcClassDecl);

        }

        @Override
        public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {

//            JCTree.JCMethodDecl jcMethodDecl1 = treeMaker.MethodDef(
//                    treeMaker.Modifiers(Flags.PUBLIC), names.fromString("sInstance"),
//                    treeMaker.Ident(names.empty), List.<JCTree.JCTypeParameter>nil(),
//                    List.<JCTree.JCVariableDecl>nil(), List.<JCTree.JCExpression>nil(),
//                    treeMaker.Block(0, null), null);

//            jcMethodDecl.mods.flags = Flags.PUBLIC|Flags.STATIC;
//            if (jcMethodDecl.name.toString().equals("ssss")) {
//                printMessage("jcMethodDecl = "+ jcMethodDecl.toString());
//
//                printMessage("jcMethodDecl = "+   jcMethodDecl.getReturnType().toString());
//                JCTree.JCPrimitiveTypeTree
//                printMessage("jcMethodDecl = "+   jcMethodDecl.getReturnType().getClass().getName());
//                printMessage("jcMethodDecl = "+   jcMethodDecl.getReturnType().getKind().name());
//            }


            super.visitMethodDef(jcMethodDecl);
        }
    }

}
