package com.app.aptprocessor;

import com.app.aptannotation.BindView;
import com.app.aptannotation.ViewClick;
import com.app.aptannotation.BindLayout;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.sun.tools.javac.code.Symbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * 对View进行操作的Processor
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {
    private Messager mMessager;//用来打印日志信息
    private Elements mElementUtils;//一个用来处理Element的工具类，源代码的每一个部分都是一个特定类型的Element
    private Map<String, BindViewClassCreatorProxy> mProxyMap = new HashMap<>();


    /**
     * 初始化我们需要的基础工具
     * 每一个注解处理器类都必须有一个空的构造函数。
     * 然而，这里有一个特殊的init()方法，它会被注解处理工具调用，并输入ProcessingEnviroment参数。
     * ProcessingEnviroment提供很多有用的工具类Elements,Types和Filer。
     *
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
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
        supportTypes.add(BindView.class.getCanonicalName());
        supportTypes.add(ViewClick.class.getCanonicalName());
        return supportTypes;
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
     * 这相当于每个处理器的主函数main()。
     * 你在这里写你的扫描、评估和处理注解的代码，以及生成Java文件。
     * 输入参数RoundEnviroment，可以让你查询出包含特定注解的被注解元素。
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        printMessage("processing...");
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
//                boolean isActivity = ProcessorUtils.isInstanceof(classElement, "android.app.Activity");
//                if (isActivity) {
                    //必须为Activity才能使用BindLayout
                    String fullClassName = classElement.getQualifiedName().toString();
                    BindViewClassCreatorProxy proxy = mProxyMap.get(fullClassName);
                    if (proxy == null) {
                        proxy = new BindViewClassCreatorProxy(mElementUtils, classElement);
                        mProxyMap.put(fullClassName, proxy);
                    }
                    BindLayout bindAnnotation = classElement.getAnnotation(BindLayout.class);
                    int id = bindAnnotation.value();
                    proxy.setBindLayout(id);
//                }
            }
        }

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.FIELD) {
                //可以安全地进行强转，将Element对象转换为一个VariableElement对象
                VariableElement variableElement = (VariableElement) element;

                //变量名
                // printMessage("　变量名:" + variableElement.getSimpleName());
                // printMessage("字段类型:" + variableElement.getKind().toString());

                TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
                String fullClassName = classElement.getQualifiedName().toString();

                // printMessage("　类　名:" + classElement.getSimpleName());
                // printMessage("　全类名:" + classElement.getQualifiedName());
                // printMessage("　　包名:" + mElementUtils.getPackageOf(classElement)
                //                                                    .asType()
                //                                                    .toString());
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
            try {
                JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName(),
                        proxyInfo.generateJavaCode()).build();
                //　生成文件
                javaFile.writeTo(processingEnv.getFiler());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        printMessage("process finish ...");
        return true;
    }

    private void printMessage(CharSequence charSequence) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, charSequence);
    }
}
