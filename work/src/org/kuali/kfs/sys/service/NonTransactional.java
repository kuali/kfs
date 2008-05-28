/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE, ElementType.METHOD })
/** 
 * This annotation is effectively a marker.  Beans which access data should 
 * either be Transactional or not.  To ensure that the developer has considered
 * this when writing service beans, the mehtods of the service must be 
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
