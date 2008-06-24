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
package org.kuali.kfs.module.ar.businessobject.lookup;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.kfs.coa.businessobject.defaultvalue.ValueFinderUtil;
import org.kuali.kfs.module.ar.util.ARUtil;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;

public class CustomerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    
    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject businessObject) {

        FinancialSystemUser user = ValueFinderUtil.getCurrentFinancialSystemUser();
        if (ARUtil.isUserInArSupervisorGroup(user)) {
            return super.getActionUrls(businessObject);
        }
        else
            return "";

    }

}
