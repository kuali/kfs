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
package org.kuali.module.ar.lookup;

import java.util.List;

import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;

/**
 * Override of lookupable helper service for AR System Information objects
 */
public class SystemInformationLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * Override to add the lockboxNumber to the return keys
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getReturnKeys()
     */
    @Override
    public List getReturnKeys() {
        List returnKeys = super.getReturnKeys();
        returnKeys.add("lockboxNumber");
        return returnKeys;
    }
    
}
