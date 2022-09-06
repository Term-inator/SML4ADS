package com.ecnu.adsmls.utils.factory.impl;

import com.ecnu.adsmls.components.modal.NewFileModal;
import com.ecnu.adsmls.utils.FileSystem;
import com.ecnu.adsmls.utils.factory.Factory;


public class NewFileModalFactory extends Factory<FileSystem.Suffix, NewFileModal> {
    public NewFileModalFactory() {
        String packageName = "com.ecnu.adsmls.components.modal.impl";
        this.classNameMap.put(FileSystem.Suffix.MODEL, packageName + ".NewModelModal");
        this.classNameMap.put(FileSystem.Suffix.TREE, packageName + ".NewTreeModal");
        this.classNameMap.put(FileSystem.Suffix.WEATHER, packageName + ".NewWeatherModal");
        this.classNameMap.put(FileSystem.Suffix.REQUIREMENT, packageName + ".NewRequirementModal");
        this.classNameMap.put(FileSystem.Suffix.RULE, packageName + ".NewRuleModal");
        this.classNameMap.put(FileSystem.Suffix.DIR, packageName + ".NewDirModal");
    }
}
