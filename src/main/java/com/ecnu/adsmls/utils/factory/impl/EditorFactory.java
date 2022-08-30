package com.ecnu.adsmls.utils.factory.impl;

import com.ecnu.adsmls.components.editor.Editor;
import com.ecnu.adsmls.utils.FileSystem;
import com.ecnu.adsmls.utils.factory.Factory;

import java.io.File;

public class EditorFactory extends Factory<FileSystem.Suffix, Editor> {
    public EditorFactory() {
        this.args = new Class[]{String.class, File.class};
        String packageName = "com.ecnu.adsmls.components.editor";
        this.classNameMap.put(FileSystem.Suffix.MODEL, packageName + ".modeleditor.ModelEditor");
        this.classNameMap.put(FileSystem.Suffix.TREE, packageName + ".treeeditor.TreeEditor");
        this.classNameMap.put(FileSystem.Suffix.WEATHER, packageName + ".weathereditor.WeatherEditor");
        this.classNameMap.put(FileSystem.Suffix.REQUIREMENT, packageName + ".requirementeditor.RequirementEditor");
    }
}
