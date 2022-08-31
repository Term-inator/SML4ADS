package com.ecnu.adsmls.components.modal.impl;

import com.ecnu.adsmls.components.modal.NewFileModal;
import com.ecnu.adsmls.utils.FileSystem;

public class NewModelModal extends NewFileModal {
    public NewModelModal() {
        super("filename", FileSystem.Suffix.MODEL.value);
    }

    @Override
    protected void createWindow() {
        super.createWindow();
        this.setTitle("New Model");
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
