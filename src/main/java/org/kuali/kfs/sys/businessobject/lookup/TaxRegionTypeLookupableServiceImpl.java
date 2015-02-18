/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
