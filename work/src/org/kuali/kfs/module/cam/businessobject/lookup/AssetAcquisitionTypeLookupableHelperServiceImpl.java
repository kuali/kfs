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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;

/**
 * This class overrides the getReturnUrl, setFieldConversions and getActionUrls for {@link OrganizationRoutingModelName}
 */
public class AssetAcquisitionTypeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private boolean initializingAssetGlobal = true;


    /**
     * Overrides the base implementation to add in new parameters to the return url
     * <ul>
     * <li>{@link KFSConstants.DISPATCH_REQUEST_PARAMETER}</li>
     * <li>{@link KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE}</li>
     * <li>{@link KFSConstants.OVERRIDE_KEYS}</li>
     * </ul>
     * {@link KFSConstants.DISPATCH_REQUEST_PARAMETER}
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.rice.kns.bo.BusinessObject,
     *      java.util.Map, java.lang.String)
     */
    @Override
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        AssetAcquisitionType assetAcquisitionType = (AssetAcquisitionType) businessObject;
        
        if (initializingAssetGlobal && !assetAcquisitionType.isActive()) {
            // no return URL if we are initializing asset global and the record is inactive
            return getEmptyAnchorHtmlData();
        } else {
            Properties parameters = getParameters(businessObject, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName(), returnKeys);
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AssetGlobal.class.getName());
            parameters.put(KFSConstants.OVERRIDE_KEYS, CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE);
            parameters.put(KFSConstants.REFRESH_CALLER, CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE + "::" + assetAcquisitionType.getAcquisitionTypeCode());
            setBackLocation(KFSConstants.MAINTENANCE_ACTION);
            return getReturnAnchorHtmlData(businessObject, parameters, lookupForm, returnKeys, businessObjectRestrictions);
        }
    }

    /**
     * A couple of acquisition type code won't be listed in Asset Global and we need to apply this constraint from lookup. They are:
     *  P-Pre-asset tagging - this acquisition type code is assigned in CAB to assets that are created with a pre-tagging record.
     *  C-Fabrication - this acquisition type is assigned to assets created via the fabrication document. 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<? extends BusinessObject> searchResults = super.getSearchResults(fieldValues);
        if (searchResults == null || searchResults.isEmpty() || !initializingAssetGlobal) {
            return searchResults;
        }
        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(AssetAcquisitionType.class);
        Long matchingResultsCount = null;
        List<AssetAcquisitionType> newList = new ArrayList<AssetAcquisitionType>();
        for (BusinessObject businessObject : searchResults) {
            AssetAcquisitionType acquisition = (AssetAcquisitionType) businessObject;
            if (ObjectUtils.isNotNull(acquisition) && !CamsConstants.Asset.ACQUISITION_TYPE_CODE_C.equalsIgnoreCase(acquisition.getAcquisitionTypeCode()) && !CamsConstants.AssetGlobal.PRE_TAGGING_ACQUISITION_TYPE_CODE.equalsIgnoreCase(acquisition.getAcquisitionTypeCode())) {
                newList.add(acquisition);
            }

        }
        matchingResultsCount = Long.valueOf(newList.size());
        if (matchingResultsCount.intValue() <= searchResultsLimit.intValue()) {
            matchingResultsCount = new Long(0);
        }
        return new CollectionIncomplete(newList, matchingResultsCount);
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
            initializingAssetGlobal = false;
        }
    }

    /**
     * Overrides base implementation to remove the action urls if we are initializing the asset retirement reason
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject,
     *      List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        if (!initializingAssetGlobal) {
            return super.getCustomActionUrls(businessObject, pkNames);
        }
        else {
            return super.getEmptyActionUrls();
        }
    }
}