/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.List;
import java.util.Properties;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.UrlFactory;

public class TaxRegionTypeLookupableServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * Overriding the return URL to create a new Tax Region document.  If CREATE_TAX_REGION_FROM_LOOKUP_INDICATOR
     * exists, that means that the lookup is coming from the "create new" button on the tax region lookup.
     * If so, create customer link to tax region maint doc. Otherwise, just use the normal returnUrl.
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.Map, java.lang.String, java.util.List)
     */
    @SuppressWarnings("rawtypes")
    // KFSMI-5158
    @Override
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions restrictions) {

        final String docTypeName = getTaxRegionDocumentTypeName();
        if (docTypeName.equals(lookupForm.getFormKey())) { 
            Properties parameters = getParameters(businessObject, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName(), returnKeys);
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TaxRegion.class.getName());
            parameters.put(KFSConstants.OVERRIDE_KEYS, KFSConstants.TaxRegionConstants.TAX_REGION_TYPE_CODE);
            parameters.put(KFSConstants.REFRESH_CALLER, KFSConstants.TaxRegionConstants.TAX_REGION_TYPE_CODE + "::" + ((TaxRegionType) businessObject).getTaxRegionTypeCode());
            
            final String href = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);
            final String returnUrlAnchorLabel =
                SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TITLE_RETURN_URL_PREPENDTEXT_PROPERTY);
            AnchorHtmlData anchor = new AnchorHtmlData(href, HtmlData.getTitleText(returnUrlAnchorLabel, businessObject, returnKeys, restrictions));
            anchor.setDisplayText(returnUrlAnchorLabel);
           return anchor;
       }
       else {
           return super.getReturnUrl(businessObject, lookupForm, returnKeys, restrictions);
       }
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = super.getCustomActionUrls(businessObject, pkNames);
        
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TaxRegion.class.getName());
        parameters.put(KFSConstants.OVERRIDE_KEYS, KFSConstants.TaxRegionConstants.TAX_REGION_TYPE_CODE);
        parameters.put(KFSConstants.REFRESH_CALLER, KFSConstants.TaxRegionConstants.TAX_REGION_TYPE_CODE + "::" + ((TaxRegionType) businessObject).getTaxRegionTypeCode());
        parameters.put(KFSConstants.TaxRegionConstants.TAX_REGION_TYPE_CODE, ((TaxRegionType) businessObject).getTaxRegionTypeCode());
        String href = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);
        
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, "start", "create tax region");
        htmlDataList.add(anchorHtmlData);
        
        return htmlDataList;
    }
    
    private String getTaxRegionDocumentTypeName() {
        return getDataDictionaryService().getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(TaxRegion.class).getDocumentTypeName();
    } 
}
