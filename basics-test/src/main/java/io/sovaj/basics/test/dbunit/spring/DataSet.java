package io.sovaj.basics.test.dbunit.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which indicates that a test class or test method should load a data set, using dbunit behind the scenes,
 * before executing the test.
 * 
 * @author Mickael Dubois
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DataSet {
  /**
   * Dataset XML files location.
     * @return The list of dataset XML file location
   */
  String[] value() default {};

  String setupOperation() default "CLEAN_INSERT";

  String teardownOperation() default "NONE";

  /**
   * The datasource name from which the connections should be taken.
     * @return The datasoure name
   */
  String datasourceName() default "";
}
