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
package org.kuali.kfs.module.cam.businessobject.lookup;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.struts.form.LookupForm;

/**
 * This class overrides the getReturnUrl, setFieldConversions and getActionUrls for
 * {@link OrganizationRoutingModelName}
 */
public class AssetAcquisitionLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
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
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.rice.kns.bo.BusinessObject, java.util.Map,
     *      java.lang.String)
     */
    @Override
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List returnKeys) {
        Properties parameters = getParameters(
                businessObject, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName(), returnKeys);
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AssetGlobal.class.getName());
        parameters.put(KFSConstants.OVERRIDE_KEYS, CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE);
        parameters.put(KFSConstants.REFRESH_CALLER, CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE+"::"+((AssetAcquisitionType) businessObject).getAcquisitionTypeCode());
        setBackLocation(KFSConstants.MAINTENANCE_ACTION);
        return getReturnAnchorHtmlData(businessObject, parameters, lookupForm, returnKeys);
    }

    /**
     * Overrides base implementation to determine whether or not we are dealing with looking up the model or editing it
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#setFieldConversions(java.util.Map)
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
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        if (!initializingAssetRetirement) {
            return super.getCustomActionUrls(businessObject, pkNames);
        }
        else {
            return super.getEmptyActionUrls();
        }
    }
}