/**
 * 
 */
package at.ac.tuwien.ase09.test.pageabstraction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 22.10.2014
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface NamingContainer {
    String value() default "";
}
