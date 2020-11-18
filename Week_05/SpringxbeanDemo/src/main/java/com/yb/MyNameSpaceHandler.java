package com.yb;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/***
 * 解析school标签的handler
 * 需要在META-INFO/Spring.handlers和META-INFO/Spring.schemas中设置该handler
 *
 * @auther yb
 * @date 2020/11/17 19:30
 */
public class MyNameSpaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        // 解析school标签的handler
        registerBeanDefinitionParser("school", new SchoolBeanDefinitionParser());
    }
}
