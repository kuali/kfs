/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.context;

import java.lang.reflect.Method;

import org.kuali.core.util.cache.MethodCacheInterceptor;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class SpringAOPUsageTest extends KualiTestBase {
    public void testParameterCacheClearing() throws Exception {
        String value = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.FINANCIAL_SYSTEM_ALL.class, KFSConstants.CoreApcParms.SERVICE_BUS_ACCESS_GROUP_PARM);
        removeCachedMethod(ParameterService.class.getMethod("getParameterValue", new Class[] { Class.class, String.class }), new Object[] { ParameterConstants.FINANCIAL_SYSTEM_ALL.class, KFSConstants.CoreApcParms.SERVICE_BUS_ACCESS_GROUP_PARM });
    }

    private void removeCachedMethod(Method method, Object[] arguments) {
        MethodCacheInterceptor methodCacheInterceptor = SpringContext.getBean(MethodCacheInterceptor.class);
        if (methodCacheInterceptor.containsCacheKey(methodCacheInterceptor.buildCacheKey(method.toString(), arguments))) {
            String cacheKey = methodCacheInterceptor.buildCacheKey(method.toString(), arguments);
            System.out.println(cacheKey);
            methodCacheInterceptor.removeCacheKey(cacheKey);
            assertFalse(methodCacheInterceptor.containsCacheKey(methodCacheInterceptor.buildCacheKey(method.toString(), arguments)));
        }
    }
}
