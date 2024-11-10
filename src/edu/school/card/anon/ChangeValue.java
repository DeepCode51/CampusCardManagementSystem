package edu.school.card.anon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字典值转换注解
 * @date 2020/11/24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ChangeValue {

    /**
     * 字段所在列
     * @return
     */
    int index() default 0;

    /**
     * 转换模式
     * @return
     */
    String pattern() default "";

}
