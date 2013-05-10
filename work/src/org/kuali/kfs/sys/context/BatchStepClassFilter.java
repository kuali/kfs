/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.context;

import java.lang.reflect.Proxy;

import org.kuali.kfs.sys.batch.Step;
import org.springframework.aop.ClassFilter;

/**
 * Class filter used to match classes that executes batch job/steps, used to configure batch transaction settings
 */
public class BatchStepClassFilter implements ClassFilter {

    /**
     * Matches on Step implementations
     * 
     * @see org.springframework.aop.ClassFilter#matches(java.lang.Class)
     */
    public boolean matches(Class clazz) {
        if (Step.class.isAssignableFrom(clazz)) {
            if (!Proxy.isProxyClass(clazz) && !isCglibProxyClass(clazz)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether the specified class is a CGLIB-generated class.
     * 
     * @param clazz the class to check
     */
    public static boolean isCglibProxyClass(Class clazz) {
        return (clazz != null && clazz.getName().indexOf("$$") != -1);
    }

}
