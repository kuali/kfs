/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject.lookup;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.lookup.LookupableHelperService;

public class LookupableSpringContext {
    public static Lookupable getLookupable(String beanId) {
        return SpringContext.getBeansOfType(Lookupable.class).get(beanId);
    }

    public static LookupableHelperService getLookupableHelperService(String beanId) {
        return SpringContext.getBeansOfType(LookupableHelperService.class).get(beanId);
    }

//    public static WorkflowLookupable getWorkflowLookupable(String beanId) {
//        return SpringContext.getBeansOfType(WorkflowLookupable.class).get(beanId);
//    }
}
