package com.ecnu.adsmls.utils.register;

import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;

import java.util.*;

public class Function {
    public enum DataType {
        INT, DOUBLE, STRING
    }

    public enum Necessity {
        REQUIRED, OPTIONAL
    }

    private String functionName;

    private List<FunctionParam> params = new ArrayList<>();
    // 上下文，关注兄弟参数
    private Map<String, String> context = new HashMap<>();

    public Function(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void addParam(String paramName, DataType dataType, Necessity necessity, Requirement ...requirements) {
        this.params.add(new FunctionParam(paramName, dataType, necessity, requirements));
    }

    public void updateContext(String paramName, String value) {
        this.context.put(paramName, value);
    }

    public List<FunctionParam> getParams() {
        return params;
    }

    public void check() throws EmptyParamException, DataTypeException, RequirementException {
        for(FunctionParam functionParam : params) {
            String value = this.context.get(functionParam.getParamName());
            try {
                functionParam.check(this.context, value);
            }
            catch (EmptyParamException e) {
                this.context.clear();
                throw new EmptyParamException(e);
            }
            catch (DataTypeException e) {
                this.context.clear();
                throw new DataTypeException(e);
            }
            catch (RequirementException e) {
                this.context.clear();
                throw new RequirementException(e);
            }
        }
        this.context.clear();
    }

    @Override
    public int hashCode() {
        return this.functionName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FunctionParam) {
            Function other = (Function) obj;
            return Objects.equals(this.functionName, other.functionName);
        }
        return false;
    }
}
