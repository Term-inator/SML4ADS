package com.ecnu.adsmls.components.modal.impl;

import com.ecnu.adsmls.components.modal.NewFileModal;
import com.ecnu.adsmls.utils.FileSystem;

public class NewTreeModal extends NewFileModal {

    public NewTreeModal() {
        super("filename", FileSystem.Suffix.TREE.value);
    }

    @Override
    protected void createWindow() {
        super.createWindow();
        this.setTitle("New Tree");
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
