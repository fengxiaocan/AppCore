package com.app.aptprocessor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
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
    private ClassName mContextClass = ClassName.get("android.content", "Context");
    private ClassName mActivityClass = ClassName.get("android.app", "Activity");
    private boolean isActivity;

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
        return TypeSpec.classBuilder(mBindingClassName).addModifiers(
                Modifier.PUBLIC).addModifiers(Modifier.FINAL).addFields(generateFields()).addMethod(
                generateConstructorMethods())
                .addMethods(generateSetFieldMethods())
                .addMethod(generateBuilderMethods())
                .addMethod(generateIntentMethods())
                .addMethod(generateStartMethods())
                .addMethod(generateStartForResultMethods())
                .addMethod(generateBindMethods())
//                .addMethod(generateBindMethods())
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

    /**
     * 构建生成Intent的方法
     *
     * @return
     */
    private MethodSpec generateIntentMethods() {
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("buildIntent").addModifiers(
                Modifier.PUBLIC).returns(ClassName.get("android.content", "Intent"));
        //添加参数
        buildMethod.addParameter(mContextClass, "context");
        //创建intent
        buildMethod.addStatement(String.format("Intent intent = new Intent(context, %s.class)",
                mHostClassName));
        //把所有的数据传递到intent中
        for (String key : mFieldElementMap.keySet()) {
            buildMethod.addStatement(
                    String.format("intent.putExtra(\"%s\", %s)", key, key));
        }
        //判断context是否是activity
        buildMethod.addCode("if (!(context instanceof $T)) {\n", mActivityClass);
        //不是则添加flag
        buildMethod.addStatement("intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)");

        buildMethod.addCode("}\n");
        //返回intent
        buildMethod.addStatement("return intent");

        return buildMethod.build();
    }

    /**
     * 构建跳转方法
     *
     * @return
     */
    private MethodSpec generateStartMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("start").addModifiers(
                Modifier.PUBLIC).returns(void.class);
        methodBuilder.addParameter(mContextClass, "context");
        methodBuilder.addCode("context.startActivity(buildIntent(context));");
        return methodBuilder.build();
    }

    /**
     * 构建startActivityForResult方法
     *
     * @return
     */
    private MethodSpec generateStartForResultMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("start").addModifiers(
                Modifier.PUBLIC).returns(void.class);
        methodBuilder.addParameter(mActivityClass, "activity");
        methodBuilder.addParameter(TypeName.INT, "requestCode");
        methodBuilder.addCode("activity.startActivityForResult(buildIntent(activity),requestCode);");
        return methodBuilder.build();
    }


    /**
     * 生成静态方法产生 mBindingClass
     *
     * @return
     */
    private MethodSpec generateBindMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind").addModifiers(
                Modifier.PUBLIC).addModifiers(Modifier.STATIC).returns(void.class);
        methodBuilder.addParameter(mHostClassName, "host");

        methodBuilder.addStatement(" Intent intent = host.getIntent()");

        for (String key : mFieldElementMap.keySet()) {
            FieldSpec spec = mFieldElementMap.get(key);
            String specType = spec.type.toString();
            if (boolean.class.getName().equals(specType) || Boolean.class.getName().equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getBooleanExtra(\"%s\",false);", key, key));
            } else if (byte.class.getName().equals(specType) || Byte.class.getName().equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getByteExtra(\"%s\", (byte) 0);", key, key));
            } else if (short.class.getName().equals(specType) || Short.class.getName().equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getShortExtra(\"%s\", (short) 0);", key, key));
            } else if (int.class.getName().equals(specType) || Integer.class.getName().equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getIntExtra(\"%s\", 0);", key, key));
            } else if (long.class.getName().equals(specType) || Long.class.getName().equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getLongExtra(\"%s\", 0L);", key, key));
            } else if (char.class.getName().equals(specType) || Character.class.getName().equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getCharExtra(\"%s\", '\\u0000');", key, key));
            } else if (float.class.getName().equals(specType) || Float.class.getName().equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getFloatExtra(\"%s\", 0F);", key, key));
            } else if (double.class.getName().equals(specType) || Double.class.getName().equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getDoubleExtra(\"%s\", 0D);", key, key));
            } else if (String.class.getName().equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getStringExtra(\"%s\");", key, key));
            } else if ("boolean[]".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getBooleanArrayExtra(\"%s\");", key, key));
            } else if ("byte[]".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getByteArrayExtra(\"%s\");", key, key));
            } else if ("short[]".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getShortArrayExtra(\"%s\");", key, key));
            } else if ("int[]".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getIntArrayExtra(\"%s\");", key, key));
            } else if ("long[]".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getLongArrayExtra(\"%s\");", key, key));
            } else if ("char[]".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getCharArrayExtra(\"%s\");", key, key));
            } else if ("float[]".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getFloatArrayExtra(\"%s\");", key, key));
            } else if ("double[]".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getDoubleArrayExtra(\"%s\");", key, key));
            } else if ("java.lang.String[]".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getStringArrayExtra(\"%s\");", key, key));
            } else if ("java.util.ArrayList<java.lang.Integer>".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getIntegerArrayListExtra(\"%s\");", key, key));
            } else if ("java.lang.CharSequence".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getCharSequenceExtra(\"%s\");", key, key));
            } else if ("java.lang.CharSequence[]".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getCharSequenceArrayExtra(\"%s\");", key, key));
            } else if ("java.util.ArrayList<java.lang.CharSequence>".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getCharSequenceArrayListExtra(\"%s\");", key, key));
            } else if ("java.util.ArrayList<java.lang.String>".equals(specType)) {
                methodBuilder.addCode(String.format("host.%s = intent.getStringArrayListExtra(\"%s\");", key, key));
            } else {
//                methodBuilder.addCode(String.format("if (host.%s instanceof $T){\n", key), ClassName.get("android.os", "Parcelable"));
//                methodBuilder.addCode(String.format("host.%s = ($T)intent.getParcelableExtra(\"%s\");\n", key, key), spec.type);
//                methodBuilder.addCode("}\n");
//                methodBuilder.addCode(String.format("else if (host.%s instanceof $T){\n", key), ClassName.get("java.io", "Serializable"));
//                methodBuilder.addCode(String.format("host.%s = ($T)intent.getSerializableExtra(\"%s\");\n", key, key), spec.type);
//                methodBuilder.addCode("}\n");
            }

            //"android.os.Parcelable[]"
            //"java.util.ArrayList<android.os.Parcelable>"
            //"java.io.Serializable"

//        Parcelable parcelableExtra = intent.getParcelableExtra(name);
//        Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra(name);
//        ArrayList<Parcelable> parcelableArrayListExtra = intent.getParcelableArrayListExtra(name);
//        Serializable serializableExtra = intent.getSerializableExtra(name);

            methodBuilder.addCode("\n");
        }
        return methodBuilder.build();
    }

}
