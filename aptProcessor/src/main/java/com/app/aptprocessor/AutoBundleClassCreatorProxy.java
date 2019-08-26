package com.app.aptprocessor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.HashSet;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * The type Bind view class creator proxy.
 */
public class AutoBundleClassCreatorProxy extends BaseClassCreatorProxy {
    private HashMap<String, VariableElement> mVariableElementMap = new HashMap();
    private ClassName mContextClass = ClassName.get("android.content", "Context");
    private ClassName mActivityClass = ClassName.get("android.app", "Activity");
    private ClassName mBundleClass = ClassName.get("android.os", "Bundle");
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
            mVariableElementMap.put(element.getSimpleName().toString(), element);
        }
    }

    /**
     * 创建Java代码
     * javapoet
     *
     * @return type spec
     */
    public TypeSpec generateJavaCode() {
        TypeSpec.Builder method = TypeSpec.classBuilder(mBindingClassName).addModifiers(
                Modifier.PUBLIC).addModifiers(Modifier.FINAL).addField(generateFields()).addMethod(
                generateConstructorMethods()).addMethods(generateSetFieldMethods()).addMethod(
                generateBuilderMethods()).addMethod(generateBundleMethods());

        if (isActivity) {
            method.addMethod(generateIntentMethods()).addMethod(generateStartMethods()).addMethod(
                    generateStartForResultMethods());
        } else {
            method.addMethod(generateCreateMethods());
        }
        method.addMethod(generateBindMethods());
        return method.build();
    }

    /**
     * 生成成员变量
     *
     * @return
     */
    private FieldSpec generateFields() {
        return FieldSpec.builder(mBundleClass, "mAutoBundle", Modifier.PRIVATE).build();
    }


    /**
     * 创建构造函数
     *
     * @return
     */
    private MethodSpec generateConstructorMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder().addModifiers(
                Modifier.PUBLIC);
        methodBuilder.addCode("mAutoBundle = new Bundle();\n");
        return methodBuilder.build();
    }

    /**
     * 创建set方法
     *
     * @return
     */
    private HashSet<MethodSpec> generateSetFieldMethods() {
        HashSet<MethodSpec> specs = new HashSet<>();
        for (String key : mVariableElementMap.keySet()) {

            VariableElement element = mVariableElementMap.get(key);
            TypeName typeName = ClassName.get(element.asType());
            String specType = typeName.toString();

            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(key).addModifiers(
                    Modifier.PUBLIC).returns(mBindingClass).addParameter(typeName, key);

            if (boolean.class.getName().equals(specType) || Boolean.class.getName().equals(
                    specType))
            {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putBoolean(\"%s\",%s);", key, key));
            } else if (byte.class.getName().equals(specType) || Byte.class.getName().equals(
                    specType))
            {
                methodBuilder.addCode(String.format("mAutoBundle.putByte(\"%s\", %s);", key, key));
            } else if (short.class.getName().equals(specType) || Short.class.getName().equals(
                    specType))
            {
                methodBuilder.addCode(String.format("mAutoBundle.putShort(\"%s\", %s);", key, key));
            } else if (int.class.getName().equals(specType) || Integer.class.getName().equals(
                    specType))
            {
                methodBuilder.addCode(String.format("mAutoBundle.putInt(\"%s\", %s);", key, key));
            } else if (long.class.getName().equals(specType) || Long.class.getName().equals(
                    specType))
            {
                methodBuilder.addCode(String.format("mAutoBundle.putLong(\"%s\", %s);", key, key));
            } else if (char.class.getName().equals(specType) || Character.class.getName().equals(
                    specType))
            {
                methodBuilder.addCode(String.format("mAutoBundle.putChar(\"%s\", %s);", key, key));
            } else if (float.class.getName().equals(specType) || Float.class.getName().equals(
                    specType))
            {
                methodBuilder.addCode(String.format("mAutoBundle.putFloat(\"%s\", %s);", key, key));
            } else if (double.class.getName().equals(specType) || Double.class.getName().equals(
                    specType))
            {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putDouble(\"%s\", %s);", key, key));
            } else if (String.class.getName().equals(specType)) {
                methodBuilder.addCode(String.format("mAutoBundle.putString(\"%s\",%s);", key, key));
            } else if ("boolean[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putBooleanArray(\"%s\",%s);", key, key));
            } else if ("byte[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putByteArray(\"%s\",%s);", key, key));
            } else if ("short[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putShortArray(\"%s\",%s);", key, key));
            } else if ("int[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putIntArray(\"%s\",%s);", key, key));
            } else if ("long[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putLongArray(\"%s\",%s);", key, key));
            } else if ("char[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putCharArray(\"%s\",%s);", key, key));
            } else if ("float[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putFloatArray(\"%s\",%s);", key, key));
            } else if ("double[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putDoubleArray(\"%s\",%s);", key, key));
            } else if ("java.lang.String[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putStringArray(\"%s\",%s);", key, key));
            } else if ("java.util.ArrayList<java.lang.Integer>".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putIntegerArrayList(\"%s\",%s);", key, key));
            } else if ("java.lang.CharSequence".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putCharSequence(\"%s\",%s);", key, key));
            } else if ("java.lang.CharSequence[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putCharSequenceArray(\"%s\",%s);", key, key));
            } else if ("java.util.ArrayList<java.lang.CharSequence>".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putCharSequenceArrayList(\"%s\",%s);", key,
                                key));
            } else if ("java.util.ArrayList<java.lang.String>".equals(specType)) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putStringArrayList(\"%s\",%s);", key, key));
            } else if ("android.util.Size".equals(specType)) {
                methodBuilder.addCode(
                        "if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {\n");
                methodBuilder.addCode(
                        String.format("mAutoBundle.putSize(\"%s\",%s);\n}\n", key, key));
            } else if ("android.util.SizeF".equals(specType)) {
                methodBuilder.addCode(
                        "if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {\n");
                methodBuilder.addCode(
                        String.format("mAutoBundle.putSizeF(\"%s\",%s);\n}\n", key, key));
            } else if (ProcessorUtils.isInterfacesOf(element, "android.os.Parcelable")) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putParcelable(\"%s\",%s);\n", key, key));
            } else if (ProcessorUtils.isInterfacesOf(element, "android.os.IBinder")) {
                methodBuilder.addCode(
                        "if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {\n");
                methodBuilder.addCode(
                        String.format("mAutoBundle.putBinder(\"%s\",%s);\n}\n", key, key));

            } else if (ProcessorUtils.isInterfacesOf(element, "java.io.Serializable")) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putSerializable(\"%s\",%s);\n", key, key));
            } else if (ProcessorUtils.isInterfacesOfList(element, "android.os.Parcelable")) {
                if (specType.startsWith("android.util.SparseArray")) {
                    methodBuilder.addCode(
                            String.format("mAutoBundle.putSparseParcelableArray(\"%s\",%s);\n", key,
                                    key));
                } else {
                    methodBuilder.addCode(
                            String.format("mAutoBundle.putParcelableArrayList(\"%s\",%s);\n", key,
                                    key));
                }
            } else if (ProcessorUtils.isInterfacesOfArray(element, "android.os.Parcelable")) {
                methodBuilder.addCode(
                        String.format("mAutoBundle.putParcelableArray(\"%s\",%s);\n", key, key));
            } else {
                ClassName gson = ClassName.get("com.google.gson", "Gson");
                methodBuilder.addCode("$T gson = new $T();\n", gson, gson);
                methodBuilder.addCode(String.format("String json = gson.toJson(%s);\n", key));
                methodBuilder.addCode(String.format("mAutoBundle.putString(\"%s\",json);", key));
            }


            methodBuilder.addCode("\nreturn this;\n");
            specs.add(methodBuilder.build());
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
        buildMethod.addStatement("intent.putExtras(mAutoBundle)");
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
     * 构建Bundle
     *
     * @return
     */
    private MethodSpec generateBundleMethods() {
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("buildBundle").addModifiers(
                Modifier.PUBLIC).returns(mBundleClass);
        //返回Bundle
        buildMethod.addStatement("return mAutoBundle");
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
     * 构建fragment的创建方法
     *
     * @return
     */
    private MethodSpec generateCreateMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("create").addModifiers(
                Modifier.PUBLIC).returns(mHostClassName);
        methodBuilder.addCode("$T fragment = new $T();\n", mHostClassName, mHostClassName);
        methodBuilder.addCode("fragment.setArguments(mAutoBundle);\n");
        methodBuilder.addCode("return fragment;\n");
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
        if (isActivity) {
            methodBuilder.addStatement("Intent intent = host.getIntent()");

            methodBuilder.addStatement("Bundle autoBundle = intent.getExtras()");
        } else {
            methodBuilder.addStatement("Bundle autoBundle = host.getArguments()");
        }
        methodBuilder.addCode("if(autoBundle == null) { return; }\n");
        for (String key : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(key);
            TypeName typeName = ClassName.get(element.asType());
            String specType = typeName.toString();

            if (boolean.class.getName().equals(specType) || Boolean.class.getName().equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getBoolean(\"%s\");", key, key));
            } else if (byte.class.getName().equals(specType) || Byte.class.getName().equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getByte(\"%s\");", key, key));
            } else if (short.class.getName().equals(specType) || Short.class.getName().equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getShort(\"%s\");", key, key));
            } else if (int.class.getName().equals(specType) || Integer.class.getName().equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getInt(\"%s\");", key, key));
            } else if (long.class.getName().equals(specType) || Long.class.getName().equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getLong(\"%s\");", key, key));
            } else if (char.class.getName().equals(specType) || Character.class.getName().equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getChar(\"%s\");", key, key));
            } else if (float.class.getName().equals(specType) || Float.class.getName().equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getFloat(\"%s\");", key, key));
            } else if (double.class.getName().equals(specType) || Double.class.getName().equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getDouble(\"%s\");", key, key));
            } else if (String.class.getName().equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getString(\"%s\");", key, key));
            } else if ("boolean[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getBooleanArray(\"%s\");", key, key));
            } else if ("byte[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getByteArray(\"%s\");", key, key));
            } else if ("short[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getShortArray(\"%s\");", key, key));
            } else if ("int[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getIntArray(\"%s\");", key, key));
            } else if ("long[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getLongArray(\"%s\");", key, key));
            } else if ("char[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getCharArray(\"%s\");", key, key));
            } else if ("float[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getFloatArray(\"%s\");", key, key));
            } else if ("double[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getDoubleArray(\"%s\");", key, key));
            } else if ("java.lang.String[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getStringArray(\"%s\");", key, key));
            } else if ("java.util.ArrayList<java.lang.Integer>".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getIntegerArrayList(\"%s\");", key,
                                key));
            } else if ("java.lang.CharSequence".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getCharSequence(\"%s\");", key, key));
            } else if ("java.lang.CharSequence[]".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getCharSequenceArray(\"%s\");", key,
                                key));
            } else if ("java.util.ArrayList<java.lang.CharSequence>".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getCharSequenceArrayList(\"%s\");", key,
                                key));
            } else if ("java.util.ArrayList<java.lang.String>".equals(specType)) {
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getStringArrayList(\"%s\");", key,
                                key));
            } else if ("android.util.Size".equals(specType)) {
                methodBuilder.addCode(
                        "if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {\n");
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getSize(\"%s\");\n}", key, key));
            } else if ("android.util.SizeF".equals(specType)) {
                methodBuilder.addCode(
                        "if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {\n");
                methodBuilder.addCode(
                        String.format("host.%s = autoBundle.getSizeF(\"%s\");\n}", key, key));
            } else if (ProcessorUtils.isInterfacesOf(element,
                    "android.os.Parcelable"))
            {
                methodBuilder.addCode(
                        String.format("host.%s = ($T)autoBundle.getParcelable(\"%s\");", key, key),
                        typeName);
            } else if (ProcessorUtils.isInterfacesOf(element, "android.os.IBinder")) {
                methodBuilder.addCode(
                        "if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {\n");
                methodBuilder.addCode(
                        String.format("host.%s = ($T)autoBundle.getBinder(\"%s\");\n}", key, key),
                        typeName);

            } else if (ProcessorUtils.isInterfacesOf(element,
                    "java.io.Serializable"))
            {
                methodBuilder.addCode(
                        String.format("host.%s = ($T)autoBundle.getSerializable(\"%s\");", key,
                                key), typeName);
            } else if (ProcessorUtils.isInterfacesOfList(element,
                    "android.os.Parcelable"))
            {
                if (specType.startsWith("android.util.SparseArray")) {
                    methodBuilder.addCode(
                            String.format("host.%s = autoBundle.getSparseParcelableArray(\"%s\");",
                                    key, key));
                } else {
                    methodBuilder.addCode(
                            String.format("host.%s = autoBundle.getParcelableArrayList(\"%s\");",
                                    key, key));
                }
            } else if (ProcessorUtils.isInterfacesOfArray(element,
                    "android.os.Parcelable"))
            {
                methodBuilder.addCode(
                        String.format("host.%s = ($T)autoBundle.getParcelableArray(\"%s\");", key,
                                key), typeName);
            }  else {
                ClassName gson = ClassName.get("com.google.gson", "Gson");
                methodBuilder.addCode("$T gson = new $T();\n", gson, gson);
                methodBuilder.addCode(
                        String.format("String json = autoBundle.getString(\"%s\");\n", key));
                methodBuilder.addCode(String.format("host.%s = gson.fromJson(json, $T.class);", key),typeName);
            }
            methodBuilder.addCode("\n");
        }
        return methodBuilder.build();
    }

}
