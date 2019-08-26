package com.app.core;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ViewBinding {
    public static void bind(Activity activity) {
        Class<? extends Activity> aClass = activity.getClass();
        try {
            Class<?> bindViewClass = Class.forName(aClass.getName() + "ViewBinding");
            Method method = bindViewClass.getMethod("bind", aClass);
            method.invoke(null, activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void bind(View view) {
        Class<? extends View> aClass = view.getClass();
        try {
            Class<?> bindViewClass = Class.forName(aClass.getName() + "ViewBinding");
            Method method = bindViewClass.getMethod("bind", aClass);
            method.invoke(null, view);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void bind(Object object, View view) {
        Class<?> aClass = object.getClass();
        try {
            Class<?> bindViewClass = Class.forName(aClass.getName() + "ViewBinding");
            Method method = bindViewClass.getMethod("bind", aClass, View.class);
            method.invoke(null, object, view);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    public static View inject(Fragment fragment, ViewGroup view) {
        Class<?> aClass = fragment.getClass();
        try {
            Class<?> bindViewClass = Class.forName(aClass.getName() + "ViewBinding");
            Method method = bindViewClass.getMethod("bind", aClass, ViewGroup.class);

            Object invoke = method.invoke(null, fragment, view);
            Method method2 = bindViewClass.getMethod("inject");

            return (View) method2.invoke(invoke);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static View inject(androidx.fragment.app.Fragment fragment, ViewGroup view) {
        Class<?> aClass = fragment.getClass();
        try {
            Class<?> bindViewClass = Class.forName(aClass.getName() + "ViewBinding");
            Method method = bindViewClass.getMethod("bind", aClass, ViewGroup.class);

            Object invoke = method.invoke(null, fragment, view);

            Method method2 = bindViewClass.getMethod("inject");
            return (View) method2.invoke(invoke);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
