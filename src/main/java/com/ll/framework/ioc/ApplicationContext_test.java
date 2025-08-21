package com.ll.framework.ioc;

import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import static com.ll.standard.util.Ut.str.lcfirst;

public class ApplicationContext_test {

    //세웅님 코드
    private final BeanScanner beanScanner;
    private final BeanRegistry beanRegistry;

    //세웅님 코드
    public ApplicationContext_test(String basePackage) {
        this.beanRegistry = new BeanRegistry();
        this.beanScanner = new BeanScanner(basePackage, beanRegistry);
    }

    //세웅님 코드
    public void init() {
        beanScanner.scan();
    }

    //공통 코드
    @SuppressWarnings("unchecked")
    public <T> T genBean(String beanName) {
        if (beanRegistry.containsBean(beanName)) {
            return beanRegistry.getBean(beanName);
        }

        Class<?> c = beanRegistry.getBeanClass(beanName);

        if (c == null) return null;

        try {
            Object bean = construct(c); //동엽님 코드
            beanRegistry.registerBean(beanName, bean);
            return (T) bean;

        } catch (Exception e) {
            return null;
        }
    }

    //동엽님 코드
    @SneakyThrows
    private <T> T construct(Class<T> cls) {
        Constructor<T> constructor = (Constructor<T>) cls.getDeclaredConstructors()[0];
        Object[] args = Arrays.stream(constructor.getParameterTypes())
                              .map(dep -> genBean(lcfirst(dep.getSimpleName())))
                              .toArray();

        return constructor.newInstance(args);
    }
}
