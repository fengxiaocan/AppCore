package com.app.apt.proxy;

import com.app.apt.base.BaseClassCreatorProxy;
import com.app.aptannotation.BindData;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * The type Bind view class creator proxy.
 */
public class BindDataCreatorProxy extends BaseClassCreatorProxy {
    private HashMap<String, VariableElement> mVariableElementMap = new HashMap<>();
    private HashMap<String, ClassName> mClassNameMap = new HashMap<>();
    private Map<String, TypeElement> mTypeProxyMap = new HashMap<>();

    /**
     * Instantiates a new Bind view class creator proxy.
     *
     * @param elementUtils the element utils
     * @param classElement the class element
     */
    public BindDataCreatorProxy(Elements elementUtils, TypeElement classElement) {
        super(elementUtils, classElement);
    }

    @Override
    protected String createBindingClassName(String className) {
        return className + "DataBinding";
    }

    /**
     * Put element.
     *
     * @param element the element
     */
    public void addVariableElement(VariableElement element) {
        if (element != null) {
            mVariableElementMap.put(element.getSimpleName().toString(), element);
        }
    }

    public void setTypeProxy(Map<String, TypeElement> map) {
        mTypeProxyMap = map;
    }

    /**
     * 创建Java代码
     * javapoet
     *
     * @return type spec
     */
    public TypeSpec generateJavaCode() {
        initClassList();
        TypeSpec.Builder builder = TypeSpec.classBuilder(mBindingClassName)
                                           //public
                                           .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                           //生成构造方法
                                           .addMethod(generateConstructorMethods());

//        Set<String> keySet = mClassNameMap.keySet();
//
//        for (String key : keySet) {
//            ClassName className = mClassNameMap.get(key);
//            //添加继承
//            builder.addSuperinterface(className);
//
//            TypeElement element = mTypeProxyMap.get(key);
//            //判断是否需要继承某个类,以防止出现protected修饰符
//            boolean isNeedSuperClass = false;
//            if (element != null) {
//                java.util.List<? extends Element> list = element.getEnclosedElements();
//                if (list != null) {
//                    for (Element element1 : list) {
//                        if (element1.getKind().isField() ||
//                            element1.getKind() == ElementKind.METHOD)
//                        {
//                            Set<Modifier> modifiers = element1.getModifiers();
//                            if (modifiers.contains(Modifier.PROTECTED)) {
//                                isNeedSuperClass = true;
//                            }
//                        }
//                    }
//                }
//            }
//            if (isNeedSuperClass) {
//                builder.addSuperinterface(className);
//            }
//
//            //添加
//            builder.addMethod(generateBindMethods(className));
//        }

        return builder.build();
    }

    private void initClassList() {
        for (String key : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(key);
            System.out.println("initClassList="+element.getSimpleName().toString());
                        BindData data = element.getAnnotation(BindData.class);
//            String[] value = data.value();
//            for (String absPath : value) {
//                System.out.println("initClassList="+absPath);
//                int index = absPath.lastIndexOf("\\.");
//                if (index <= 0) {
//                    continue;
//                }
//                String className = absPath.substring(0, index);
//                int i1 = className.lastIndexOf("\\.");
//                if (i1 <= 0) {
//                    mClassNameMap.put(className, ClassName.get("", className));
//                } else {
//                    String packName = className.substring(0, i1);
//                    mClassNameMap.put(className, ClassName.get(packName, className.substring(i1)));
//                }
//            }
        }
    }


    /**
     * 生成静态方法产生 mBindingClass
     *
     * @param className
     * @return
     */
    private MethodSpec generateBindMethods(ClassName className) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind").addModifiers(
                Modifier.PUBLIC).addModifiers(Modifier.STATIC).returns(TypeName.VOID);

        methodBuilder.addParameter(mHostClassName, "root");
        methodBuilder.addParameter(className, "data");

        for (String key : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(key);
            System.out.println(element.getSimpleName());
//            BindData data = element.getAnnotation(BindData.class);
            String[] value = null;
            for (String absPath : value) {
                int index = absPath.lastIndexOf("\\.");
                if (index <= 0) {
                    continue;
                }
                String clazz = absPath.substring(0, index);
                if (className.equals(clazz)) {
                    TypeElement dataElement = mTypeProxyMap.get(key);
                    if (dataElement != null) {
                        java.util.List<? extends Element> list = dataElement.getEnclosedElements();
                        if (list != null) {
                            for (Element element1 : list) {
                                if (element1.getKind().isField() )
                                {
                                    methodBuilder.addStatement(
                                            String.format("root.%s = data.%s", element1.getSimpleName(),
                                                    clazz));
                                }else if (element1.getKind() == ElementKind.METHOD){
                                    methodBuilder.addStatement(
                                            String.format("root.%s = data.%s()", element1.getSimpleName(),
                                                    clazz));
                                }
                            }
                        }
                    } else {
                        int i1 = clazz.lastIndexOf("\\.");
                        if (i1 <= 0) {
                            methodBuilder.addStatement(
                                    String.format("root.%s = data.%s", element.getSimpleName(),
                                            clazz));
                        } else {
                            methodBuilder.addStatement(
                                    String.format("root.%s = data.%s", element.getSimpleName(),
                                            clazz.substring(i1)));
                        }
                    }
                }
            }
        }

        return methodBuilder.build();
    }

    /**
     * 创建构造函数
     *
     * @return
     */
    private MethodSpec generateConstructorMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder().addModifiers(
                Modifier.PRIVATE);
        return methodBuilder.build();
    }
}
