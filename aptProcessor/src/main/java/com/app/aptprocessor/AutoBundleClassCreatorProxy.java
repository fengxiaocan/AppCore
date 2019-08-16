package com.app.aptprocessor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * The type Bind view class creator proxy.
 */
public class AutoBundleClassCreatorProxy extends BaseClassCreatorProxy {
    private HashSet<VariableElement> mVariableElementSet = new HashSet();
    private Map<String, FieldSpec> mFieldElementMap = new HashMap<>();//生成的成员变量
    private boolean isActivity;
    private ClassName mContextClass = ClassName.get("android.content", "Context");

    /**
     * Instantiates a new Bind view class creator proxy.
     *
     * @param elementUtils the element utils
     * @param classElement the class element
     */
    public AutoBundleClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        super(elementUtils, classElement);
        isActivity = ProcessorUtils.isInstanceof(classElement, "android.app.Activity");
    }

    @Override
    protected String createBindingClassName(String className) {
        return className + "AutoBundle";
    }

    /**
     * Put element.
     *
     * @param element the element
     */
    public void addVariableElement(VariableElement element) {
        if (element != null) {
            mVariableElementSet.add(element);
        }
    }

    /**
     * 创建Java代码
     * javapoet
     *
     * @return type spec
     */
    public TypeSpec generateJavaCode() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(mBindingClassName).addModifiers(
                Modifier.PUBLIC).addModifiers(Modifier.FINAL).addFields(generateFields()).addMethod(
                generateConstructorMethods());
        //        if (isActivity && bindLayout != 0) {
        //            builder.addMethod(generateSetLayoutMethods());
        //        }
        return builder.addMethod(generateBindMethods())
                      .addMethods(generateSetFieldMethods())
                      .build();
    }

    /**
     * 生成成员变量
     *
     * @return
     */
    private HashSet<FieldSpec> generateFields() {
        HashSet<FieldSpec> fieldSpecs = new HashSet();
        for (VariableElement element : mVariableElementSet) {
            FieldSpec field = toField(element);
            fieldSpecs.add(field);
            mFieldElementMap.put(field.name, field);
        }
        return fieldSpecs;
    }

    /**
     * 创建构造函数
     *
     * @return
     */
    private MethodSpec generateConstructorMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder().addModifiers(
                Modifier.PUBLIC);
        return methodBuilder.build();
    }

    /**
     * 创建set方法
     *
     * @return
     */
    private HashSet<MethodSpec> generateSetFieldMethods() {
        HashSet<MethodSpec> specs = new HashSet<>();

        for (String key : mFieldElementMap.keySet()) {
            MethodSpec.Builder method = buildFieldMethod(Modifier.PUBLIC, key,
                    mFieldElementMap.get(key).type);
            specs.add(method.build());
        }
        return specs;
    }

    /**
     * 构造静态创建方法
     *
     * @return
     */
    private MethodSpec generateBuilderMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("build").addModifiers(
                Modifier.PUBLIC).addModifiers(Modifier.STATIC).returns(mBindingClass);
        methodBuilder.addCode(String.format("return new %s();", mBindingClassName));
        return methodBuilder.build();
    }

    private MethodSpec generateIntentMethods() {
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build").addModifiers(
                Modifier.PUBLIC).returns(ClassName.get("android.content", "Intent"));
        buildMethod.addParameter(mContextClass, "context");

        buildMethod.addStatement(String.format("Intent intent = new Intent(context, %s.class)",
                mHostClassName));

//        for (FieldHolder field : fields) {
//            buildMethod.addStatement(
//                    String.format("intent.putExtra(\"%s\", %s)", field.getName(), field.getName()));
//        }

        buildMethod.addCode("if (!(context instanceof $T)) {\n",
                ClassName.get("android.app", "Activity"));

        buildMethod.addStatement("intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)");

        buildMethod.addCode("}\n");

        buildMethod.addStatement("return intent");

//        contentBuilder.addMethod(buildMethod.build());
        return buildMethod.build();
    }

    private MethodSpec generateStartMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("start").addModifiers(
                Modifier.PUBLIC).returns(void.class);
        methodBuilder.addCode(String.format("return new %s();", mBindingClassName));
        return methodBuilder.build();
    }


    /**
     * 生成静态方法产生 mBindingClass
     *
     * @return
     */
    private MethodSpec generateBindMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind").addModifiers(
                Modifier.PUBLIC).addModifiers(Modifier.STATIC).returns(mBindingClass);

//        if (isActivity || isView) {
//            //activity 跟View都有自带的findViewById功能
//            methodBuilder.addParameter(mHostClassName, "host");
//        } else {
//            //非Activity的需要传入根View
//            methodBuilder.addParameter(mHostClassName, "host");
//            methodBuilder.addParameter(mViewClassName, "rootView");
//        }

        methodBuilder.addCode(
                String.format("%s binding = new %s();", mBindingClassName, mBindingClassName));
        methodBuilder.addCode("\n");
//        if (isActivity) {
//            if (bindLayout != 0) {
//                methodBuilder.addCode("binding.setContentView(host);");
//                methodBuilder.addCode("\n");
//            }
//            methodBuilder.addCode("binding.bindView(host);");
//        } else if (isView) {
//            methodBuilder.addCode("binding.bindView(host);");
//        } else {
//            methodBuilder.addCode("binding.bindView(host,rootView);");
//        }
        methodBuilder.addCode("\n");
        methodBuilder.addCode("binding.bindClick(host);");
        methodBuilder.addCode("\n");
        methodBuilder.addCode("return binding;");
        methodBuilder.addCode("\n");
        return methodBuilder.build();
    }

}
