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
package org.kuali.kfs.service.impl;

import java.util.Map;
import java.util.Properties;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.bo.ElectronicPaymentClaim;
import org.kuali.kfs.service.ElectronicFundTransferActionHelper;
import org.kuali.kfs.service.ElectronicPaymentClaimingService;
import org.kuali.kfs.web.struts.form.ElectronicFundTransferForm;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * An action for Electronic Fund Transfer that simply redirects to either the claiming or non-claiming lookup.
 */
public class ElectronicFundTransferStartActionHelper implements ElectronicFundTransferActionHelper {
    private ElectronicPaymentClaimingService electronicPaymentClaimingService;

    /**
     * @see org.kuali.kfs.service.ElectronicFundTransferActionHelper#performAction(org.kuali.kfs.web.struts.form.ElectronicFundTransferForm, org.apache.struts.action.ActionMapping, java.util.Map)
     */
    public ActionForward performAction(ElectronicFundTransferForm form, ActionMapping mapping, Map parameterMap, String basePath) {
        return new ActionForward((electronicPaymentClaimingService.isUserMemberOfClaimingGroup(GlobalVariables.getUserSession().getUniversalUser()) ? getClaimingLookupUrl(form, basePath) : getNonClaimingLookupUrl(form, basePath) ), true);
    }
    
    /**
     * @return URL for non-claiming EFT search
     */
    protected String getNonClaimingLookupUrl(ElectronicFundTransferForm form, String basePath) {        
        Properties props = getCommonLookupProperties(form);
        props.put(KNSConstants.HIDE_LOOKUP_RETURN_LINK, Boolean.toString(true));
        props.put(KNSConstants.RETURN_LOCATION_PARAMETER, basePath + "/" + getNonClaimingReturnLocation());
        props.put(KNSConstants.BACK_LOCATION, basePath + "/" + getNonClaimingReturnLocation());
        return UrlFactory.parameterizeUrl(basePath + "/kr/" + KNSConstants.LOOKUP_ACTION, props);
    }
    
    /**
     * @return URL for claiming EFT search
     */
    protected String getClaimingLookupUrl(ElectronicFundTransferForm form, String basePath) {
        Properties props = getCommonLookupProperties(form);
        props.put(KNSConstants.MULTIPLE_VALUE, Boolean.toString(true));
        props.put(KNSConstants.LOOKUP_ANCHOR, KNSConstants.ANCHOR_TOP_OF_FORM);
        props.put(KNSConstants.LOOKED_UP_COLLECTION_NAME, "claims");
        props.put(KNSConstants.RETURN_LOCATION_PARAMETER, basePath + "/" + getClaimingReturnLocation());
        props.put(KNSConstants.BACK_LOCATION, basePath + "/" + getClaimingReturnLocation());
        return UrlFactory.parameterizeUrl(basePath + "/kr/" + KNSConstants.MULTIPLE_VALUE_LOOKUP_ACTION, props);
    }
    
    /**
     * @return a set of Properties common to both claiming and non-claiming lookup
     */
    protected Properties getCommonLookupProperties(ElectronicFundTransferForm form) {
        Properties props = new Properties();
        props.put(KNSConstants.SUPPRESS_ACTIONS, Boolean.toString(true));
        props.put(KNSConstants.DOC_FORM_KEY, "88888888");
        props.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, ElectronicPaymentClaim.class.getName());
        props.put(KNSConstants.METHOD_TO_CALL_ATTRIBUTE, "start");
        return props;
    }
    
    /**
     * @return the location where the search should return to for claiming
     */
    protected String getClaimingReturnLocation() {
        return "electronicFundTransfer.do";
    }
    
    /**
     * @return the location where the search should return to for non-claiming - ie, the portal!
     */
    protected String getNonClaimingReturnLocation() {
        return "portal.do";
    }
    
    /**
     * Sets the electronicPaymentClaimingService attribute value.
     * @param electronicPaymentClaimingService The electronicPaymentClaimingService to set.
     */
    public void setElectronicPaymentClaimingService(ElectronicPaymentClaimingService electronicPaymentClaimingService) {
        this.electronicPaymentClaimingService = electronicPaymentClaimingService;
    }
    
}
