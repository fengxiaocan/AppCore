package com.app.aptprocessor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.util.List;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import static com.sun.tools.javac.code.Symbol.MethodSymbol;

/**
 * The type Bind view class creator proxy.
 */
public class BindViewClassCreatorProxy extends BaseClassCreatorProxy {
    private Map<Integer, VariableElement> mVariableElementMap = new HashMap<>();
    //需要findViewById的View
    private Map<Integer, MethodSymbol> mClickMethodElementMap = new HashMap<>();//点击事件
    private Map<Integer, FieldSpec> mFieldElementMap = new HashMap<>();//生成的成员变量
    private ClassName mViewClassName = ClassName.get("android.view", "View");//生成的成员变量
    private int bindLayout = 0;
    private boolean isActivity;
    private boolean isView;

    /**
     * Instantiates a new Bind view class creator proxy.
     *
     * @param elementUtils the element utils
     * @param classElement the class element
     */
    public BindViewClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        super(elementUtils, classElement);
        isActivity = ProcessorUtils.isInstanceof(classElement, "android.app.Activity");
        isView = ProcessorUtils.isInstanceof(classElement, "android.view.View");
    }

    @Override
    protected String createBindingClassName(String className) {
        return className + "ViewBinding";
    }

    /**
     * Put element.
     *
     * @param id      the id
     * @param element the element
     */
    public void addVariableElement(int id, VariableElement element) {
        if (element != null) {
            mVariableElementMap.put(id, element);
        }
    }

    /**
     * Put click element.
     *
     * @param id      the id
     * @param element the element
     */
    public void addClickElement(int id, MethodSymbol element) {
        mClickMethodElementMap.put(id, element);
    }

    public void setBindLayout(int bindLayout) {
        this.bindLayout = bindLayout;
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
        if (isActivity && bindLayout != 0) {
            builder.addMethod(generateSetLayoutMethods());
        }
        return builder.addMethod(generateBindMethods())
                      .addMethod(generateBindViewMethods())
                      .addMethod(generateBindClickMethods())
                      .build();
    }

    /**
     * 生成成员变量
     *
     * @return
     */
    private HashSet<FieldSpec> generateFields() {
        HashSet<FieldSpec> fieldSpecs = new HashSet();
        //不引用对象,防止两个对象互相持有引用导致内存泄漏
        //        fieldSpecs.add(
        //                FieldSpec.builder(mHostClassName, "bindSourceHost", Modifier.PRIVATE).build());
        for (int id : mVariableElementMap.keySet()) {
            //遍历需要findViewById的View
            VariableElement element = mVariableElementMap.get(id);
            FieldSpec field = toField(Modifier.PUBLIC, element);
            fieldSpecs.add(field);
            mFieldElementMap.put(id, field);
        }

        for (int id : mClickMethodElementMap.keySet()) {
            //遍历带有点击事件的View
            if (!mVariableElementMap.containsKey(id)) {
                //去重复,防止产生多重变量
                String viewName = "view_" + id;
                FieldSpec fieldSpec = FieldSpec.builder(mViewClassName, viewName, Modifier.PRIVATE)
                                               .build();
                fieldSpecs.add(fieldSpec);
                mFieldElementMap.put(id, fieldSpec);
            }
        }

        return fieldSpecs;
    }

    /**
     * 生成静态方法产生 mBindingClass
     *
     * @return
     */
    private MethodSpec generateBindMethods() {

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind").addModifiers(
                Modifier.PUBLIC).addModifiers(Modifier.STATIC).returns(mBindingClass);

        if (isActivity || isView) {
            //activity 跟View都有自带的findViewById功能
            methodBuilder.addParameter(mHostClassName, "host");
        } else {
            //非Activity的需要传入根View
            methodBuilder.addParameter(mHostClassName, "host");
            methodBuilder.addParameter(mViewClassName, "rootView");
        }

        methodBuilder.addCode(
                String.format("%s binding = new %s();", mBindingClassName, mBindingClassName));
        methodBuilder.addCode("\n");
        if (isActivity) {
            if (bindLayout != 0) {
                methodBuilder.addCode("binding.setContentView(host);");
                methodBuilder.addCode("\n");
            }
            methodBuilder.addCode("binding.bindView(host);");
        } else if (isView) {
            methodBuilder.addCode("binding.bindView(host);");
        } else {
            methodBuilder.addCode("binding.bindView(host,rootView);");
        }
        methodBuilder.addCode("\n");
        methodBuilder.addCode("binding.bindClick(host);");
        methodBuilder.addCode("\n");
        methodBuilder.addCode("return binding;");
        methodBuilder.addCode("\n");
        return methodBuilder.build();
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
     * 创建BindView方法
     *
     * @return
     */
    private MethodSpec generateSetLayoutMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("setContentView").addModifiers(
                Modifier.PUBLIC).returns(void.class).addParameter(mHostClassName, "host");
        methodBuilder.addCode(String.format("host.setContentView(%d);\n", bindLayout));
        return methodBuilder.build();
    }

    private MethodSpec generateBindViewMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bindView").addModifiers(
                Modifier.PUBLIC).returns(void.class);
        if (isActivity || isView) {
            //activity 跟View都有自带的findViewById功能
            methodBuilder.addParameter(mHostClassName, "host");
        } else {
            //非Activity的需要传入根View
            methodBuilder.addParameter(mHostClassName, "host");
            methodBuilder.addParameter(mViewClassName, "rootView");
        }

        for (int id : mFieldElementMap.keySet()) {
            FieldSpec spec = mFieldElementMap.get(id);
            if (isActivity || isView) {
                methodBuilder.addCode(String.format("%s = host.findViewById(%d);", spec.name, id));
            } else {
                methodBuilder.addCode(
                        String.format("%s = rootView.findViewById(%d);", spec.name, id));
            }
            methodBuilder.addCode("\n");
            VariableElement element = mVariableElementMap.get(id);
            if (element != null) {
                String name = element.getSimpleName().toString();
                methodBuilder.addCode(String.format("host.%s = this.%s;", name, spec.name));
                methodBuilder.addCode("\n");
            }
        }

        return methodBuilder.build();
    }

    /**
     * 创建点击事件的方法
     *
     * @return
     */
    private MethodSpec generateBindClickMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bindClick").addModifiers(
                Modifier.PUBLIC).returns(void.class).addParameter(mHostClassName, "host",
                Modifier.FINAL);
        for (int id : mFieldElementMap.keySet()) {
            FieldSpec fieldSpec = mFieldElementMap.get(id);
            MethodSymbol clickElement = mClickMethodElementMap.get(id);
            if (clickElement != null) {
                String clickViewName = clickElement.getSimpleName().toString();

                methodBuilder.addCode(String.format("if( %s != null){", fieldSpec.name));
                methodBuilder.addCode("\n");
                List<Symbol.VarSymbol> parameters = clickElement.getParameters();
                if (parameters != null && parameters.size() == 1) {
                    methodBuilder.addCode(String.format(
                            "%s.setOnClickListener(new View.OnClickListener(){" +
                            "\n     @Override" + "\n     public void onClick(View view) {" +
                            "\n            host.%s(view);" + "        \n}   \n});", fieldSpec.name,
                            clickViewName));
                } else {
                    methodBuilder.addCode(String.format(
                            "   %s.setOnClickListener(new View.OnClickListener(){" +
                            "\n         @Override" + "\n         public void onClick(View view) {" +
                            "\n             host.%s();" + "        \n}   \n});", fieldSpec.name,
                            clickViewName));
                }
                methodBuilder.addCode("\n");
                methodBuilder.addCode("}");
                methodBuilder.addCode("\n");
            }
        }
        return methodBuilder.build();
    }
}
