package com.app.apt.processor;

import com.app.apt.base.BaseProcessor;
import com.app.apt.proxy.AutoBundleClassCreatorProxy;
import com.app.apt.util.ProcessorUtil;
import com.app.aptannotation.AutoBundle;
import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * 对View进行操作的Processor
 */
@AutoService(Processor.class)
public class AutoBundleProcessor extends BaseProcessor {
    private Map<String, AutoBundleClassCreatorProxy> mProxyMap = new HashMap<>();


    @Override
    protected Class[] annotationTypes() {
        return new Class[]{AutoBundle.class};
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mProxyMap.clear();
        //得到所有的注解
        // roundEnvironment.getElementsAnnotatedWith(BindView.class)返回所有被注解了@BindView。
        // 你可能已经注意到，我们并没有说“所有被注解了@BindView”，因为它真的是返回Element的列表。
        // 请记住：Element可以是类、方法、变量等。所以，接下来，我们必须检查这些Element是否是一个类：

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(
                AutoBundle.class);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.FIELD) {
                //可以安全地进行强转，将Element对象转换为一个VariableElement对象
                VariableElement variableElement = (VariableElement) element;

                TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
                String fullClassName = classElement.getQualifiedName().toString();

                //elements的信息保存到mProxyMap中
                boolean isActivityOrFragment = ProcessorUtil.isActivity(classElement) ||
                                               ProcessorUtil.isFragment(classElement);

                if (isActivityOrFragment) {
                    AutoBundleClassCreatorProxy proxy = mProxyMap.get(fullClassName);
                    if (proxy == null) {
                        proxy = new AutoBundleClassCreatorProxy(mElementUtils, classElement);
                        mProxyMap.put(fullClassName, proxy);
                    }
                    proxy.addVariableElement(variableElement);
                } else {
                    printMessage("非 activity 和 Fragment 不能使用 AutoBundle 注解");
                }
            }
        }

        //通过javapoet生成
        for (String key : mProxyMap.keySet()) {
            AutoBundleClassCreatorProxy proxyInfo = mProxyMap.get(key);
            javaFile(proxyInfo.getPackageName(), proxyInfo.generateJavaCode());
        }
        return true;
    }
}
