package com.ecnu.adsmls.components.modal.impl;

import com.ecnu.adsmls.components.modal.NewFileModal;
import com.ecnu.adsmls.utils.FileSystem;

public class NewDirectoryModal extends NewFileModal {
    public NewDirectoryModal() {
        super("directory name", FileSystem.Suffix.DIR.value);
    }

    @Override
    protected void createWindow() {
        super.createWindow();
        this.setTitle("New Directory");
    }

    @Override
    protected void check() {
        super.check();
        this.checkFilename();
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
