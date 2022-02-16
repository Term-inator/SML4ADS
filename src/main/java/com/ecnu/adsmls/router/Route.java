package com.ecnu.adsmls.router;

/**
 * 需要跳转的页面的 Controller 要实现该接口
 */
public interface Route {
    /**
     * 加载页面间传递的参数
     */
    void loadParams();
}
