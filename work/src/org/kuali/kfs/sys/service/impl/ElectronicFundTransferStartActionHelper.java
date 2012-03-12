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
package org.kuali.kfs.sys.service.impl;

import java.util.Map;
import java.util.Properties;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.service.ElectronicFundTransferActionHelper;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.kfs.sys.web.struts.ElectronicFundTransferForm;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * An action for Electronic Fund Transfer that simply redirects to either the claiming or non-claiming lookup.
 */
public class ElectronicFundTransferStartActionHelper implements ElectronicFundTransferActionHelper {
    private ElectronicPaymentClaimingService electronicPaymentClaimingService;

    /**
     * @see org.kuali.kfs.sys.service.ElectronicFundTransferActionHelper#performAction(org.kuali.kfs.sys.web.struts.ElectronicFundTransferForm, org.apache.struts.action.ActionMapping, java.util.Map)
     */
    public ActionForward performAction(ElectronicFundTransferForm form, ActionMapping mapping, Map parameterMap, String basePath) {
        return new ActionForward((form.hasAvailableClaimingDocumentStrategies() ? getClaimingLookupUrl(form, basePath) : getNonClaimingLookupUrl(form, basePath) ), true);
    }
    
    /**
     * @return URL for non-claiming EFT search
     */
    protected String getNonClaimingLookupUrl(ElectronicFundTransferForm form, String basePath) {        
        Properties props = getCommonLookupProperties(form);
        props.put(KRADConstants.HIDE_LOOKUP_RETURN_LINK, Boolean.toString(true));
        props.put(KRADConstants.RETURN_LOCATION_PARAMETER, basePath + "/" + getNonClaimingReturnLocation());
        props.put(KRADConstants.BACK_LOCATION, basePath + "/" + getNonClaimingReturnLocation());
        return UrlFactory.parameterizeUrl(basePath + "/kr/" + KRADConstants.LOOKUP_ACTION, props);
    }
    
    /**
     * @return URL for claiming EFT search
     */
    protected String getClaimingLookupUrl(ElectronicFundTransferForm form, String basePath) {
        Properties props = getCommonLookupProperties(form);
        props.put(KRADConstants.MULTIPLE_VALUE, Boolean.toString(true));
        props.put(KRADConstants.LOOKUP_ANCHOR, KRADConstants.ANCHOR_TOP_OF_FORM);
        props.put(KRADConstants.LOOKED_UP_COLLECTION_NAME, "claims");
        props.put(KRADConstants.RETURN_LOCATION_PARAMETER, basePath + "/" + getClaimingReturnLocation());
        props.put(KRADConstants.BACK_LOCATION, basePath + "/" + getClaimingReturnLocation());
        return UrlFactory.parameterizeUrl(basePath + "/kr/" + KRADConstants.MULTIPLE_VALUE_LOOKUP_ACTION, props);
    }
    
    /**
     * @return a set of Properties common to both claiming and non-claiming lookup
     */
    protected Properties getCommonLookupProperties(ElectronicFundTransferForm form) {
        Properties props = new Properties();
        props.put(KRADConstants.SUPPRESS_ACTIONS, Boolean.toString(true));
        props.put(KRADConstants.DOC_FORM_KEY, "88888888");
        props.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, ElectronicPaymentClaim.class.getName());
        props.put(KRADConstants.METHOD_TO_CALL_ATTRIBUTE, "start");
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

