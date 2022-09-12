package wiki.lever.modal.annotation;

import java.lang.annotation.*;

/**
 * 2022/09/12 11:38:24
 *
 * @author xy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * Operate module,
     * eg: menu name.
     *
     * @return module name
     */
    String operateModule() default "";

    /**
     * Operate type,
     * eg: query, upload.
     *
     * @return type
     */
    String operateType() default "";

    /**
     * Operate name,
     * eg: api description.
     *
     * @return name
     */
    String operateName() default "";
}