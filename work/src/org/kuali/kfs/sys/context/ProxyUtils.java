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

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class ProxyUtils {

    public static Object getTargetIfProxied(Object object) {
        if (AopUtils.isAopProxy(object) && object instanceof Advised) {
            Advised advised = (Advised) object;
            try {
                Object target = advised.getTargetSource().getTarget();

                return target;
            }
            catch (Exception ex) {
                throw new RuntimeException("Unable to get class for proxy: " + ex.getMessage(), ex);
            }
        }

        return object;
    }

}
