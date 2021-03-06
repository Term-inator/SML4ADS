package com.ecnu.adsmls.utils.register;

import com.ecnu.adsmls.utils.register.exception.RequirementException;

import java.util.Map;

public interface Requirement {
    /**
     * 检查 value 是否满足要求
     *
     * @param value
     * @return
     */
    void check(Map<String, String> context, String value) throws RequirementException;

    /** TODO
     * 显示错误信息
     * @return
     */
//    String error();
}
