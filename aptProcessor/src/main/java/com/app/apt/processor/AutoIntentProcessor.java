package com.app.apt.processor;

import com.app.apt.util.ProcessorUtil;
import com.app.apt.base.BaseProcessor;
import com.app.apt.proxy.AutoIntentClassCreatorProxy;
import com.app.aptannotation.AutoIntent;
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
public class AutoIntentProcessor extends BaseProcessor {
    private Map<String, AutoIntentClassCreatorProxy> mProxyMap = new HashMap<>();

    @Override
    protected Class[] annotationTypes() {
        return new Class[]{AutoIntent.class};
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
        mProxyMap.clear();
        //得到所有的注解
        // roundEnvironment.getElementsAnnotatedWith(BindView.class)返回所有被注解了@BindView。
        // 你可能已经注意到，我们并没有说“所有被注解了@BindView”，因为它真的是返回Element的列表。
        // 请记住：Element可以是类、方法、变量等。所以，接下来，我们必须检查这些Element是否是一个类：

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(
                AutoIntent.class);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.FIELD) {
                //可以安全地进行强转，将Element对象转换为一个VariableElement对象
                VariableElement variableElement = (VariableElement) element;

                TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
                String fullClassName = classElement.getQualifiedName().toString();

                boolean isActivity = ProcessorUtil.isActivity(classElement);
                if (isActivity) {
                    AutoIntentClassCreatorProxy proxy = mProxyMap.get(fullClassName);
                    if (proxy == null) {
                        proxy = new AutoIntentClassCreatorProxy(mElementUtils, classElement);
                        mProxyMap.put(fullClassName, proxy);
                    }
                    proxy.addVariableElement(variableElement);
                } else {
                    printMessage("非 activity 不能使用 AutoIntent 注解");
                }
            }
        }

        //通过javapoet生成
        for (String key : mProxyMap.keySet()) {
            AutoIntentClassCreatorProxy proxyInfo = mProxyMap.get(key);
            javaFile(proxyInfo.getPackageName(), proxyInfo.generateJavaCode());
        }
        return true;
    }
}
