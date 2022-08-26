package com.ecnu.adsmls.components.modal;

import com.ecnu.adsmls.utils.FileSystem;

public class NewWeatherModal extends NewFileModal {
    public NewWeatherModal() {
        super("filename", FileSystem.Suffix.WEATHER.value);
    }

    @Override
    protected void createWindow() {
        super.createWindow();
        this.setTitle("New Weather");
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
