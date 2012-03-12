/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.sys.batch.Step;
import org.kuali.rice.krad.service.impl.KualiModuleServiceImpl;

public class KfsKualiModuleServiceImpl extends KualiModuleServiceImpl {


    /**
     * KFS has additional components related to our steps.  So, when resolving the component,
     * we need to allow objects of the "Step" type to be used as well.
     *
     * @see org.kuali.rice.krad.service.impl.KualiModuleServiceImpl#getComponentCode(java.lang.Class)
     */
    @Override
    public String getComponentCode(Class<?> documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("documentClass must not be null");
        }
        if ( Step.class.isAssignableFrom(documentClass) ) {
            return documentClass.getSimpleName();
        }
        // otherwise, we can restore the default behavior
        return super.getComponentCode(documentClass);
    }
}
