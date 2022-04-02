package com.ecnu.adsmls.utils.register;

import java.util.*;

public abstract class FunctionRegister {
    public abstract void init();

    public abstract void register(String type, List<FunctionParam> params);

    public enum DataType {
        INT, DOUBLE, STRING
    }

    public enum Necessity {
        REQUIRED, OPTIONAL
    }

    // 上下文，关注兄弟参数
    private Map<String, String> context = new HashMap<>();

    public class FunctionParam {
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
                        break;
                    }
                    case DOUBLE: {
                        Double.parseDouble(value);
                        break;
                    }
                    case STRING: {
                        // do nothing
                        break;
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
            return true;
        }

        private boolean checkRequirements(String value) {
            context.put(this.paramName, value);
            for(Requirement requirement : this.requirements) {
                if(!requirement.check(context, value)) {
                    return false;
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

        @Override
        public String toString() {
            return "FunctionParam{" +
                    "paramName='" + paramName + '\'' +
                    ", dataType=" + dataType +
                    '}';
        }
    }
}
