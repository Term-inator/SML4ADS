package com.ecnu.adsmls.components.editor.impl;

import com.ecnu.adsmls.components.editor.TreeLink;

public abstract class Transition extends TreeLink {
    public Transition(long id) {
        super(id);
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public void inactive() {
        super.inactive();
    }
}
