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
package org.kuali.module.chart.lookup;

import java.util.Map;
import java.util.Properties;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.util.UrlFactory;
import org.kuali.module.chart.bo.DelegateChangeContainer;

public class OrganizationRoutingModelNameLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getBackLocation()
     */
    @Override
    public String getBackLocation() {
        // it doesn't really matter what the backLocation is set to; we're
        // always going to return to the maintenance screen
        return Constants.MAINTENANCE_ACTION;
    }

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.core.bo.BusinessObject, java.util.Map, java.lang.String)
     */
    @Override
    public String getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl) {
        Properties parameters = getParameters(businessObject, fieldConversions, lookupImpl);
        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, Constants.MAINTENANCE_NEWWITHEXISTING_ACTION);
        parameters.put(Constants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, DelegateChangeContainer.class.getName());
        parameters.put(Constants.OVERRIDE_KEYS, "modelName"+Constants.FIELD_CONVERSIONS_SEPERATOR+"modelChartOfAccountsCode"+Constants.FIELD_CONVERSIONS_SEPERATOR+"modelOrganizationCode");
        return UrlFactory.parameterizeUrl(getBackLocation(), parameters);
    }
    
    

}
