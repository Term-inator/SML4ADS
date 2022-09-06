package com.ecnu.adsmls.components.modal.impl;

import com.ecnu.adsmls.components.modal.NewFileModal;
import com.ecnu.adsmls.utils.FileSystem;

public class NewRuleModal extends NewFileModal {

    public NewRuleModal() {
        super("filename", FileSystem.Suffix.RULE.value);
    }

    @Override
    protected void createWindow() {
        super.createWindow();
        this.setTitle("New Rule");
    }

    @Override
    protected void check() {
        super.check();
    }

    @Override
    protected void update() {
        super.update();
    }

    @Override
    protected void then() {
        super.then();
    }

    @Override
    protected void checkFilename() {
        // empty
        // super.checkFilename() is called in NewFileModal.check()
    }
}

