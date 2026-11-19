package ru.obabok.oui.config.annotation;

import ru.obabok.oui.model.ModuleScreenFactory;
import ru.obabok.oui.util.DefaultModuleScreenFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {
    String name() default "";
    String description() default "";

    Class<? extends ModuleScreenFactory> screenFactory() default DefaultModuleScreenFactory.class;
}
