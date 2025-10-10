package common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)//в какой момент обрабатываем
@Target(ElementType.METHOD)//аннотация навешивается на что? На метод(тест)
public @interface UserSession {
    int value() default 1;
    int auth() default 1;
}
