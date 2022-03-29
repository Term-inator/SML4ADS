package com.ecnu.adsmls.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class FunctionRegister {
    public enum DataType {
        INT, DOUBLE, STRING
    }

    public enum Necessity {
        REQUIRED, OPTIONAL
    }

    public enum Requirement {
        POS, NOT_POS, NEG, NOT_NEG, NOT_ZERO
    }

    public static class FunctionParam {
        String paramName;
        DataType dataType;
        Necessity necessity;
        Set<Requirement> requirements = new HashSet<>();

        public FunctionParam(String paramName, DataType dataType, Necessity necessity, Requirement ...requirements) {
            this.paramName = paramName;
            this.dataType = dataType;
            this.necessity = necessity;
            this.requirements.addAll(Arrays.asList(requirements));
        }

        public String getParamName() {
            return paramName;
        }

        public DataType getDataType() {
            return dataType;
        }

        public Necessity getNecessity() {
            return necessity;
        }

        private boolean checkType(String value) {
            try {
                switch (this.dataType) {
                    case INT: {
                        Integer.parseInt(value);
                    }
                    case DOUBLE: {
                        Double.parseDouble(value);
                    }
                    case STRING: {
                        // do nothing
                    }
                    default: {
                        return false;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private boolean checkRequirements(String value) {
            for(Requirement requirement : this.requirements) {
                // 是数字
                if(Objects.equals(this.dataType, DataType.INT) ||
                        Objects.equals(this.dataType, DataType.DOUBLE)) {
                    double eval = Double.parseDouble(value);
                    switch (requirement) {
                        case POS: {
                            return eval > 0;
                        }
                        case NOT_POS: {
                            return !(eval > 0);
                        }
                        case NEG: {
                            return eval < 0;
                        }
                        case NOT_NEG: {
                            return !(eval < 0);
                        }
                        case NOT_ZERO: {
                            return eval != 0;
                        }
                        default: {
                            return false;
                        }
                    }
                }
                else {
                    return true;
                }
            }
            return true;
        }

        public boolean check(String value) {
            if(Objects.equals(value, "")) {
                return Objects.equals(this.necessity, Necessity.OPTIONAL);
            }
            else {
                return this.checkType(value) && this.checkRequirements(value);
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
    }
}
