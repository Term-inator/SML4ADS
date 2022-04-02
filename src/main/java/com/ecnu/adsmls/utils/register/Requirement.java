package com.ecnu.adsmls.utils.register;

import java.util.Map;

public interface Requirement {
    /**
     * 检查 value 是否满足要求
     * @param value
     * @return
     */
    boolean check(Map<String, String> context, String value);

    /** TODO
     * 显示错误信息
     * @return
     */
//    String error();
}
