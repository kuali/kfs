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
package org.kuali.kfs.lookup;

import org.kuali.core.lookup.KualiLookupableImpl;
import org.kuali.kfs.bo.FinancialSystemUser;

/**
 *  Overrides the base lookupable to force the FinancialSystemUser class in wherever this used.
 */
public class FinancialSystemUserLookupableImpl extends KualiLookupableImpl {

    /** Always assume that the BO class is FinancialSystemUser, ignoring the passed in class. */
    @Override
    public void setBusinessObjectClass(Class boClass) {
        super.setBusinessObjectClass(FinancialSystemUser.class);
    }
}
