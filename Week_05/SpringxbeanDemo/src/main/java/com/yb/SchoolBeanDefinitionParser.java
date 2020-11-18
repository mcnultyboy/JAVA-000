package com.yb;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import java.util.Optional;

/***
 * 实现XML中的数据的手动加载到BeanDefinitionBuilder中
 *
 * @auther yb
 * @date 2020/11/17 19:27
 */
public class SchoolBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return School.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        // 获取xml配置的属性
        String name = element.getAttribute("name");
        String addr = element.getAttribute("addr");

        // 给bean赋值
        Optional.ofNullable(name).ifPresent((value) ->{
            builder.addPropertyValue("name", value);
        });
        Optional.ofNullable(addr).ifPresent((value) ->{
            builder.addPropertyValue("addr", value);
        });
    }
}
