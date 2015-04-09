/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE, ElementType.METHOD })
/** 
 * This annotation is effectively a marker.  Beans which access data should 
 * either be Transactional or not.  To ensure that the developer has considered
 * this when writing service beans, the public mehtods of the service must be 
 * annotated as either Transactional or NonTransactional.  If the class is 
 * annotated, then it is assumed that all of the methods have that annotation
 * and no method internal to the class should have a Transactional/NonTransactional
 * annotation.  Since Spring provides the Transactional annotation, it is only
 * necessary to provide the NonTransactional annotation inside KFS.
 * 
 * This annotation has no effect in the application at runtime.  It is only used
 * by unit tests which seek to enforce/confirm that the preceeding policy is
 * being applied.
 * 
 */
public @interface NonTransactional {

}
