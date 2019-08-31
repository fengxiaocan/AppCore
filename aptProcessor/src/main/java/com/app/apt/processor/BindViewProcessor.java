package com.app.apt.processor;

import com.app.apt.base.BaseProcessor;
import com.app.apt.base.JCTreeProxy;
import com.app.apt.proxy.BindViewClassCreatorProxy;
import com.app.apt.util.ProcessorUtil;
import com.app.aptannotation.BindLayout;
import com.app.aptannotation.BindView;
import com.app.aptannotation.ViewClick;
import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * 对View进行操作的Processor
 */
@AutoService(Processor.class)
public class BindViewProcessor extends BaseProcessor {
    private Map<String, BindViewClassCreatorProxy> mProxyMap = new HashMap<>();

    @Override
    protected Class[] annotationTypes() {
        return new Class[]{BindView.class, ViewClick.class};
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mProxyMap.clear();
        //得到所有的注解
        // roundEnvironment.getElementsAnnotatedWith(BindView.class)返回所有被注解了@BindView。
        // 你可能已经注意到，我们并没有说“所有被注解了@BindView”，因为它真的是返回Element的列表。
        // 请记住：Element可以是类、方法、变量等。所以，接下来，我们必须检查这些Element是否是一个类：

        Set<? extends Element> layoutElements = roundEnvironment.getElementsAnnotatedWith(
                BindLayout.class);
        for (Element element : layoutElements) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                //必须为Activity才能使用BindLayout
                boolean isActivity = ProcessorUtil.isActivity(classElement);
                boolean isFragment = ProcessorUtil.isFragment(classElement);

                if (isActivity || isFragment) {
                    String fullClassName = classElement.getQualifiedName().toString();
                    BindViewClassCreatorProxy proxy = mProxyMap.get(fullClassName);
                    if (proxy == null) {
                        proxy = new BindViewClassCreatorProxy(mElementUtils, classElement);
                        mProxyMap.put(fullClassName, proxy);
                    }
                    BindLayout bindAnnotation = classElement.getAnnotation(BindLayout.class);
                    int id = bindAnnotation.value();
                    proxy.setBindLayout(id);
                    //插入代码
                    JCTree.JCClassDecl tree = (JCTree.JCClassDecl) trees.getTree(classElement);
                    if (isFragment) {
                        tree.accept(new FragmentTreeTranslator(id, classElement));
                    } else {
                        tree.accept(new ActivityTreeTranslator(classElement.getQualifiedName()));
                    }
                }
            }
        }

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.FIELD) {
                //可以安全地进行强转，将Element对象转换为一个VariableElement对象
                VariableElement variableElement = (VariableElement) element;

                TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
                String fullClassName = classElement.getQualifiedName().toString();
                //elements的信息保存到mProxyMap中
                BindViewClassCreatorProxy proxy = mProxyMap.get(fullClassName);
                if (proxy == null) {
                    proxy = new BindViewClassCreatorProxy(mElementUtils, classElement);
                    mProxyMap.put(fullClassName, proxy);
                }
                BindView bindAnnotation = variableElement.getAnnotation(BindView.class);
                int id = bindAnnotation.value();

                proxy.addVariableElement(id, variableElement);
            }
        }

        Set<? extends Element> viewClicklements = roundEnvironment.getElementsAnnotatedWith(
                ViewClick.class);
        for (Element element : viewClicklements) {
            if (element.getKind() == ElementKind.METHOD) {
                Symbol.MethodSymbol variableElement = (Symbol.MethodSymbol) element;
                TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
                String fullClassName = classElement.getQualifiedName().toString();
                //elements的信息保存到mProxyMap中
                BindViewClassCreatorProxy proxy = mProxyMap.get(fullClassName);
                if (proxy == null) {
                    proxy = new BindViewClassCreatorProxy(mElementUtils, classElement);
                    mProxyMap.put(fullClassName, proxy);
                }
                ViewClick bindAnnotation = variableElement.getAnnotation(ViewClick.class);
                int[] id = bindAnnotation.value();
                for (int i : id) {
                    proxy.addClickElement(i, variableElement);
                }
            }
        }

        //通过javapoet生成
        for (String key : mProxyMap.keySet()) {
            BindViewClassCreatorProxy proxyInfo = mProxyMap.get(key);
            javaFile(proxyInfo.getPackageName(), proxyInfo.generateJavaCode());
        }
        return true;
    }

    /**
     * fragment class 字节码插入
     */
    private class FragmentTreeTranslator extends TreeTranslator {
        private int layoutId;
        private TypeElement thisElement;
        private boolean isHasOnCreateView = false;
        private boolean isHasOnViewCreate = false;
        private JCTreeProxy jcTreeProxy;
        private String thisPackageName;//当前包名

        public FragmentTreeTranslator(int layoutId, TypeElement className) {
            this.layoutId = layoutId;
            this.thisElement = className;
            jcTreeProxy = new JCTreeProxy(treeMaker, names);
            thisPackageName = thisElement.getQualifiedName().toString();
        }

        @Override
        public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
            treeMaker.at(jcClassDecl.pos);
            //遍历获取是否重写过 onCreateView 跟 onViewCreated方法
            for (JCTree def : jcClassDecl.defs) {
                if (def.getKind() == Tree.Kind.METHOD) {
                    //方法
                    JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) def;
                    if (layoutId != 0 && methodDecl.name.toString().startsWith("onCreateView")) {
                        //方法名一致
                        if (methodDecl.params != null && methodDecl.params.size() == 3) {
                            //参数一致
                            JCTree returnType = methodDecl.getReturnType();
                            String returnTypes = returnType.toString();
                            //获取三个参数
                            String var1 = methodDecl.params.get(0).vartype.toString();
                            String var2 = methodDecl.params.get(1).vartype.toString();
                            String var3 = methodDecl.params.get(2).vartype.toString();
                            if ("View".equals(returnTypes) && "LayoutInflater".equals(var1) &&
                                "ViewGroup".equals(var2) && "Bundle".equals(var3))
                            {
                                //重写了该方法
                                isHasOnCreateView = true;
                            }
                        }
                    } else if (methodDecl.name.toString().startsWith("onViewCreated")) {
                        //判断 onViewCreated 是否重写了
                        if (methodDecl.params != null && methodDecl.params.size() == 2) {
                            JCTree.JCVariableDecl variableDecl1 = methodDecl.params.get(0);
                            JCTree.JCVariableDecl variableDecl2 = methodDecl.params.get(1);

                            String var1 = variableDecl1.vartype.toString();
                            String var2 = variableDecl2.vartype.toString();
                            if ("View".equals(var1) && "Bundle".equals(var2)) {
                                //重写了该方法
                                isHasOnViewCreate = true;
                            }
                        }
                    }
                }
            }
            //没有重写 OnCreateView 方法
            if (!isHasOnCreateView) {
                Type.ClassType superclass = (Type.ClassType) thisElement.getSuperclass();
                Symbol.ClassSymbol superElement = ProcessorUtil.getSuperTypeElement(superclass,
                        "android.app.Fragment", "androidx.fragment.app.Fragment");
                java.util.List<Symbol> elements = superElement.getEnclosedElements();

                for (Symbol symbol : elements) {
                    if (symbol instanceof Symbol.MethodSymbol) {
                        Symbol.MethodSymbol symbol1 = (Symbol.MethodSymbol) symbol;
                        if ("onCreateView".equals(symbol1.name.toString()) &&
                            symbol1.params.size() == 3)
                        {
                            onCreateView(jcClassDecl, symbol1);
                            break;
                        }
                    }
                }
            }

            if (!isHasOnViewCreate) {
                Type.ClassType superclass = (Type.ClassType) thisElement.getSuperclass();
                Symbol.ClassSymbol superElement = ProcessorUtil.getSuperTypeElement(superclass,
                        "android.app.Fragment", "androidx.fragment.app.Fragment");
                java.util.List<Symbol> elements = superElement.getEnclosedElements();

                for (Symbol symbol : elements) {
                    if (symbol instanceof Symbol.MethodSymbol) {
                        Symbol.MethodSymbol symbol1 = (Symbol.MethodSymbol) symbol;
                        if ("onViewCreated".equals(symbol1.name.toString()) &&
                            symbol1.params.size() == 2)
                        {
                            onViewCreate(jcClassDecl, symbol1);
                            break;
                        }
                    }
                }
            }
            super.visitClassDef(jcClassDecl);
        }

        /**
         * 重写 onViewCreate 方法
         *
         * @param jcClassDecl
         * @param symbol
         */
        private void onViewCreate(JCTree.JCClassDecl jcClassDecl, Symbol.MethodSymbol symbol) {
            //获取第一个参数
            Symbol.VarSymbol varSymbol1 = symbol.params.get(0);
            //调用该类生成的附生类的bind方法
            JCTree.JCExpressionStatement literal = treeMaker.Exec(
                    treeMaker.Apply(List.<JCTree.JCExpression>nil(),
                            jcTreeProxy.memberAccess(thisPackageName + "ViewBinding.bind"),
                            //方法名
                            List.<JCTree.JCExpression>of(treeMaker.Ident(names._this),//参数集合
                                    treeMaker.Ident(varSymbol1.name))

                    ));
            //调用super.onViewCreated 方法
            JCTree.JCExpressionStatement superMethod = jcTreeProxy.superMethod(symbol);
            //把代码 生成 代码块block
            JCTree.JCBlock block = jcTreeProxy.block(superMethod, literal);
            //代码块生成方法
            JCTree.JCMethodDecl jcMethodDecl = treeMaker.MethodDef(symbol, block);
            //让类重新生成方法
            jcClassDecl.defs = jcClassDecl.defs.prepend(jcMethodDecl);
        }

        private void onCreateView(JCTree.JCClassDecl jcClassDecl, Symbol.MethodSymbol symbol) {
            //参数一致名字一致
            Symbol.VarSymbol varSymbol1 = symbol.params.get(0);
            Symbol.VarSymbol varSymbol2 = symbol.params.get(1);
            //获取 LayoutInflater 这个类的参数信息
            JCTree.JCExpression jcExpression1 = treeMaker.QualIdent(varSymbol1);
            //调用 LayoutInflater 的inflate方法
            JCTree.JCExpressionStatement literal = treeMaker.Exec(
                    treeMaker.Apply(List.<JCTree.JCExpression>nil(),
                            jcTreeProxy.memberAccess(jcExpression1, "inflate"),
                            //方法名
                            List.of(treeMaker.Literal(layoutId),//参数集合
                                    treeMaker.Ident(varSymbol2.name), treeMaker.Literal(false))));
            //作为返回值返回
            JCTree.JCReturn aReturn = treeMaker.Return(literal.getExpression());
            JCTree.JCBlock block = jcTreeProxy.block(aReturn);
            //生成方法
            JCTree.JCMethodDecl jcMethodDecl = treeMaker.MethodDef(symbol, block);
            //把方法添加到类中
            jcClassDecl.defs = jcClassDecl.defs.prepend(jcMethodDecl);
        }


        @Override
        public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
            if (isHasOnCreateView && layoutId != 0 && jcMethodDecl.name.toString().equals(
                    "onCreateView") && jcMethodDecl.params.size() == 3)
            {
                //如果重写了onCreateView的方法,那么需要在重新修改该方法
                //获取inflater参数
                JCTree.JCVariableDecl inflaterDecl = jcMethodDecl.params.get(0);
                //获取container参数
                JCTree.JCVariableDecl containerDecl = jcMethodDecl.params.get(1);
                //构建方法
                JCTree.JCExpressionStatement literal = treeMaker.Exec(
                        treeMaker.Apply(List.<JCTree.JCExpression>nil(),
                                //                        List.of(jcTreeProxy.memberExpr(TypeTag.INT)),//泛型参数类型
                                jcTreeProxy.memberAccess(treeMaker.Ident(inflaterDecl), "inflate"),
                                //方法名
                                List.of(treeMaker.Literal(layoutId),//参数集合
                                        treeMaker.Ident(containerDecl.name),
                                        treeMaker.Literal(false))));
                //构建返回值
                JCTree.JCReturn aReturn = treeMaker.Return(literal.getExpression());
                //生成方法体
                JCTree.JCBlock block = jcTreeProxy.block(aReturn);
                //替换重写的方法的方法体
                jcMethodDecl.body = block;
            } else if (isHasOnViewCreate && jcMethodDecl.name.toString().equals("onViewCreated") &&
                       jcMethodDecl.params.size() == 2)
            {
                //重写onViewCreated
                //获取第一个View的参数
                JCTree.JCVariableDecl viewDecl = jcMethodDecl.params.get(0);
                //直接调用ViewBinding.bind的方法
                JCTree.JCExpressionStatement literal = treeMaker.Exec(
                        treeMaker.Apply(List.<JCTree.JCExpression>nil(),
                                jcTreeProxy.memberAccess(thisPackageName + "ViewBinding.bind"),
                                //方法名
                                List.<JCTree.JCExpression>of(treeMaker.Ident(names._this),//参数集合
                                        treeMaker.Ident(viewDecl.name))

                        ));
                //在最顶部插入方法
                jcMethodDecl.body.stats = jcMethodDecl.body.stats.prepend(literal);
            }
            super.visitMethodDef(jcMethodDecl);
        }

    }

    private class ActivityTreeTranslator extends TreeTranslator {
        private Name className;
        private JCTreeProxy jcTreeProxy;

        public ActivityTreeTranslator(Name className) {
            this.className = className;
            jcTreeProxy = new JCTreeProxy(treeMaker, names);
        }

        @Override
        public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
            if (jcMethodDecl.name.toString().equals("onCreate") &&
                jcMethodDecl.params.size() == 1)
            {
                LinkedHashSet<JCTree.JCStatement> hashSet = new LinkedHashSet<>();
                boolean isSuperHasCreate = false;
                for (int i = 0; i < jcMethodDecl.body.stats.size(); i++) {
                    JCTree.JCStatement statement = jcMethodDecl.body.stats.get(i);
                    String charSequence = statement.toString();
                    hashSet.add(statement);
                    if (charSequence.startsWith("super.onCreate")) {
                        isSuperHasCreate = true;
                        hashSet.add(disposeBind());
                    }
                }
                if (isSuperHasCreate) {
                    jcMethodDecl.body.stats = List.from(hashSet);
                } else {
                    jcMethodDecl.body.stats = jcMethodDecl.body.stats.prepend(disposeBind());
                }
            }
            super.visitMethodDef(jcMethodDecl);
        }

        private JCTree.JCExpressionStatement disposeBind() {
            return treeMaker.Exec(treeMaker.Apply(List.<JCTree.JCExpression>nil(),
                    //方法名
                    jcTreeProxy.memberAccess(className.toString() + "ViewBinding.bind"),
                    //参数集合
                    List.<JCTree.JCExpression>of(treeMaker.Ident(names._this))

            ));
        }

    }
}
