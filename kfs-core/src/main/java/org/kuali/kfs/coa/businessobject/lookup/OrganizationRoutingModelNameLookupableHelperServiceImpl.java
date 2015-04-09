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
package org.kuali.kfs.coa.businessobject.lookup;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.coa.businessobject.AccountDelegateGlobal;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This class overrides the getBackLocation, getReturnUrl, setFieldConversions and getActionUrls for
 * {@link OrganizationRoutingModelName}
 */
public class OrganizationRoutingModelNameLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private boolean initializingDelegate = true;

    /**
     * Overrides the base implementation to always return to {@link KFSConstants.MAINTENANCE_ACTION}
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getBackLocation()
     */
    @Override
    public String getBackLocation() {
        // it doesn't really matter what the backLocation is set to; we're
        // always going to return to the maintenance screen
        return KFSConstants.MAINTENANCE_ACTION;
    }

    /**
     * Overrides the base implementation to add in new parameters to the return url
     * <ul>
     * <li>{@link KFSConstants.DISPATCH_REQUEST_PARAMETER}</li>
     * <li>{@link KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE}</li>
     * <li>{@link KFSConstants.OVERRIDE_KEYS}</li>
     * </ul>
     * {@link KFSConstants.DISPATCH_REQUEST_PARAMETER}
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.rice.krad.bo.BusinessObject, java.util.Map,
     *      java.lang.String)
     */
    @Override
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        String originalBackLocation = this.backLocation;
        Properties parameters = getParameters(businessObject, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName(), returnKeys);
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AccountDelegateGlobal.class.getName());
        parameters.put(KFSConstants.OVERRIDE_KEYS, "modelName" + KFSConstants.FIELD_CONVERSIONS_SEPERATOR + "modelChartOfAccountsCode" + KFSConstants.FIELD_CONVERSIONS_SEPERATOR + "modelOrganizationCode");
        setBackLocation(KFSConstants.MAINTENANCE_ACTION);
        AnchorHtmlData htmlData = (AnchorHtmlData)getReturnAnchorHtmlData(businessObject, parameters, lookupForm, returnKeys, businessObjectRestrictions);
        setBackLocation(originalBackLocation); //set this to prevent breaking Account Delegate Model returnLocation
        return htmlData;
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
            // if we don't have any field conversions, then we must be
            // actually dealing with the model, instead of looking up the model
            // in order to initalize a new global account delegate
            //
            // yeah, it's a hack...but at least a semi-clever hack
            initializingDelegate = false;
        }
    }

    /**
     * Overrides base implementation to remove the action urls if we are initializing the delegate model
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        if (!initializingDelegate) {
            return super.getCustomActionUrls(businessObject, pkNames);
        }
        else {
            return super.getEmptyActionUrls();
        }
    }
}
