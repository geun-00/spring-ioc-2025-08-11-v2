package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Component;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApplicationContext {

    private final String basePackage;
    private static final Map<String, Object> BEAN_MAP = new HashMap<>();

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
    }

    public void init() {
        Reflections reflections = new Reflections(basePackage);

        //컴포넌트 대상 클래스들
        Set<Class<?>> beanTargetClasses = reflections.getTypesAnnotatedWith(Component.class);

        for (Class<?> clazz : beanTargetClasses) {
            //클래스 타입만
            if (!clazz.isAnnotation()) {
                registerBean(clazz);
            }
        }
    }

    private Object registerBean(Class<?> clazz) {
        String beanName = clazz.getSimpleName().toLowerCase();

        if (BEAN_MAP.containsKey(beanName)) {
            return BEAN_MAP.get(beanName);
        }

        try {
            Constructor<?> constructor = clazz.getConstructors()[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();

            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = registerBean(parameterTypes[i]);
            }

            Object instance = constructor.newInstance(args);
            BEAN_MAP.put(beanName, instance);

            return instance;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T genBean(String beanName) {
        return (T) BEAN_MAP.get(beanName.toLowerCase());
    }
}
