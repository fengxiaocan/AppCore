package com.app.apptest.data;

import com.app.aptannotation.BindDataClass;

@BindDataClass
public class ClassBean {

    protected String title;

    public String getTitle() {
        return title;
    }

    public ClassBean setTitle(String title) {
        this.title = title;
        return this;
    }
}
