package com.ecnu.adsmls.utils.register;

import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;

import java.util.*;

public class FunctionParam {
    String paramName;
    Function.DataType dataType;
    Function.Necessity necessity;
    Set<Requirement> requirements = new HashSet<>();

    public FunctionParam(String paramName, Function.DataType dataType, Function.Necessity necessity, Requirement ...requirements) {
        this.paramName = paramName;
        this.dataType = dataType;
        this.necessity = necessity;
        this.requirements.addAll(Arrays.asList(requirements));
    }

    public String getParamName() {
        return paramName;
    }

    public Function.DataType getDataType() {
        return dataType;
    }

    public Function.Necessity getNecessity() {
        return necessity;
    }

    private void checkType(String value) throws DataTypeException {
        switch (this.dataType) {
            case INT: {
                try {
                    Integer.parseInt(value);
                }
                catch (Exception ignored) {
                    throw new DataTypeException(this.paramName + " should be Integer.");
                }
                break;
            }
            case DOUBLE: {
                try {
                    Double.parseDouble(value);
                }
                catch (Exception ignored) {
                    throw new DataTypeException(this.paramName + " should be Double.");
                }
                break;
            }
            case STRING: {
                // do nothing
                break;
            }
            default: {
                // empty case
            }
        }
    }

    private boolean checkRequirements(Map<String, String> context, String value) throws RequirementException {
        context.put(this.paramName, value);
        for(Requirement requirement : this.requirements) {
            requirement.check(context, value);
        }
        return true;
    }

    public void check(Map<String, String> context, String value) throws EmptyParamException, DataTypeException, RequirementException {
        if(value == null || value.isEmpty()) {
            if(!Objects.equals(this.necessity, Function.Necessity.OPTIONAL)) {
                throw new EmptyParamException(this.paramName + " is required.");
            }
        }
        else {
            this.checkType(value);
            try {
                this.checkRequirements(context, value);
            }
            catch (RequirementException e) {
                String errMsg = e.getMessage();
                throw new RequirementException(this.paramName + errMsg);
            }
        }
    }

    @Override
    public int hashCode() {
        return this.paramName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FunctionParam) {
            FunctionParam other = (FunctionParam) obj;
            return Objects.equals(this.paramName, other.paramName);
        }
        return false;
    }

    @Override
    public String toString() {
        return "FunctionParam{" +
                "paramName='" + paramName + '\'' +
                ", dataType=" + dataType +
                '}';
    }
}
