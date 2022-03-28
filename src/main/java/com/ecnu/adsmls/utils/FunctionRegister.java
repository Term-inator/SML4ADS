package com.ecnu.adsmls.utils;

import javafx.scene.chart.PieChart;
import javafx.util.Pair;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public abstract class FunctionRegister {
    public enum DataType {
        INT, DOUBLE, STRING
    }

    public enum Necessity {
        REQUIRED, OPTIONAL
    }

    public static class FunctionParam {
        String paramName;
        DataType dataType;
        Necessity necessity;

        public FunctionParam(String paramName, DataType dataType, Necessity necessity) {
            this.paramName = paramName;
            this.dataType = dataType;
            this.necessity = necessity;
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
