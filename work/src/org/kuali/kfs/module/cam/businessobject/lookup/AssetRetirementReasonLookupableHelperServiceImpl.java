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
package org.kuali.module.cams.lookup;

import java.util.Map;
import java.util.Properties;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetRetirementGlobal;

/**
 * This class overrides the getReturnUrl, setFieldConversions and getActionUrls for
 * {@link OrganizationRoutingModelName}
 */
public class AssetRetirementReasonLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private boolean initializingAssetRetirement = true;


    /**
     * Overrides the base implementation to add in new parameters to the return url
     * <ul>
     * <li>{@link KFSConstants.DISPATCH_REQUEST_PARAMETER}</li>
     * <li>{@link KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE}</li>
     * <li>{@link KFSConstants.OVERRIDE_KEYS}</li>
     * </ul>
     * {@link KFSConstants.DISPATCH_REQUEST_PARAMETER}
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.core.bo.BusinessObject, java.util.Map,
     *      java.lang.String)
     */
    @Override
    public String getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl) {
        Properties parameters = getParameters(businessObject, fieldConversions, lookupImpl);
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AssetRetirementGlobal.class.getName());
        parameters.put(KFSConstants.OVERRIDE_KEYS, CamsPropertyConstants.AssetRetirementGlobal.RETIREMENT_REASON_CODE);
        return UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);
    }

    /**
     * Overrides base implementation to determine whether or not we are dealing with looking up the model or editing it
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#setFieldConversions(java.util.Map)
     */
    @Override
    public void setFieldConversions(Map fieldConversions) {
        super.setFieldConversions(fieldConversions);
        if (fieldConversions == null || fieldConversions.size() == 0) {
            initializingAssetRetirement = false;
        }
    }

    /**
     * Overrides base implementation to remove the action urls if we are initializing the asset retirement reason
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject businessObject) {
        if (!initializingAssetRetirement) {
            return super.getActionUrls(businessObject);
        }
        else {
            return "";
        }
    }
}
