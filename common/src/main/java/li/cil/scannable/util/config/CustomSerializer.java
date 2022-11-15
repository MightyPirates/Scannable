package li.cil.scannable.util.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CustomSerializer {
    String serializer() default "";

    String deserializer() default "";
}
