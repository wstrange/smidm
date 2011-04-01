package com.my2do.idm.connector.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to mark a field as being mapped to a connector attribute
 *
 * We use this annotation to generate getter/setters that know how to marshall to/from a
 * a POJO field and an ICF Connector Attribute.
 * For example, mapping the
 * attribute "uid" to a object field "var uid:String"
 */


@Retention(value = RetentionPolicy.RUNTIME)
public @interface ConnectorAttribute {
    String name();
    boolean isCreatable() default true;
    boolean isRequired() default false;
    boolean isMultiValued() default true;
    boolean isReadable() default true;
    boolean isUpdateable() default true;
}
