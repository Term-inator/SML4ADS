package com.ecnu.adsmls.components.modal;

import com.ecnu.adsmls.utils.FileSystem;

public class NewDirectoryModal extends NewFileModal {
    public NewDirectoryModal() {
        super("directory name", FileSystem.Suffix.DIR.value);
    }

    @Override
    protected void createWindow() {
        super.createWindow();
        this.window.setTitle("New Directory");
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
}
