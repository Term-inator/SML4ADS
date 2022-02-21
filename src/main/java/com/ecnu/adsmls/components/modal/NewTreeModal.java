package com.ecnu.adsmls.components.modal;

import com.ecnu.adsmls.utils.FileSystem;

public class NewTreeModal extends NewFileModal {

    public NewTreeModal() {
        super("filename", FileSystem.Suffix.TREE.value);
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

    private void checkFilename() {

    }
}
