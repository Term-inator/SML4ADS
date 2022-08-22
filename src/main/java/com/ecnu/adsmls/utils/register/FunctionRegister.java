package com.ecnu.adsmls.utils.register;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class FunctionRegister {
    protected List<Function> functions = new ArrayList<>();

    public abstract void init();

    public List<String> getFunctionNames() {
        return functions.stream().map(Function::getFunctionName).collect(Collectors.toList());
    }

    public Function getFunction(String functionName) {
        for (Function function : functions) {
            if (Objects.equals(function.getFunctionName(), functionName)) {
                return function;
            }
        }
        return null;
    }
}
