/*
 * Copyright (c) 2011 - Warren Strange
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.my2do.idm.connector.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to mark a field as being mapped to a connector attribute
 *
 * We use this annotation to generate getter/setters that know how to marshall to/from a
 * a POJO field and an ICF Connector Attribute.
 * For example, mapping the
 * attribute "uid" to a objects field "var uid:String"
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
