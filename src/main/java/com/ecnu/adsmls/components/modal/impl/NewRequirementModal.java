package com.ecnu.adsmls.components.modal.impl;

import com.ecnu.adsmls.components.modal.NewFileModal;
import com.ecnu.adsmls.utils.FileSystem;

public class NewRequirementModal extends NewFileModal {
    public NewRequirementModal() {
        super("filename", FileSystem.Suffix.REQUIREMENT.value);
    }

    @Override
    protected void createWindow() {
        super.createWindow();
        this.setTitle("New Requirement");
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
