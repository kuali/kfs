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
package org.kuali.kfs.module.ar.businessobject.inquiry;

import java.util.Properties;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionOnDemandLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Inquirable implementation for Dunning Letter Distribution On-Demand Lookup.
 */
public class DunningLetterDistributionOnDemandLookupResultInquirableImpl extends KfsInquirableImpl {

    /**
     * Helper method to build an inquiry URLs for DunningLetterDistributionOnDemandLookupResult
     * 
     * @param businessObject the business object instance to build the urls for
     * @param attributeName the attribute name which links to an inquirable
     * @return String url to inquiry
     */
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName) {

        AnchorHtmlData inquiryHref = new AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING);
        if (KFSPropertyConstants.PROPOSAL_NUMBER.equals(attributeName)) {
            String baseUrl = KFSConstants.INQUIRY_ACTION;
            inquiryHref.setHref("kr/inquiry.do?businessObjectClassName=org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward&proposalNumber=" + ObjectUtils.getPropertyValue((DunningLetterDistributionOnDemandLookupResult) businessObject, attributeName) + "&methodToCall=start");
        }
        else if (ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER.equals(attributeName)) {
            String baseUrl = KFSConstants.INQUIRY_ACTION;
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Customer.class.getName());
            parameters.put(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, ObjectUtils.getPropertyValue((DunningLetterDistributionOnDemandLookupResult) businessObject, attributeName));

            inquiryHref.setHref(UrlFactory.parameterizeUrl(baseUrl, parameters));
        }
        else if (KFSPropertyConstants.AGENCY_NUMBER.equals(attributeName)) {
            String baseUrl = KFSConstants.INQUIRY_ACTION;
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, ContractsAndGrantsCGBAgency.class.getName());
            parameters.put(KFSPropertyConstants.AGENCY_NUMBER, ObjectUtils.getPropertyValue((DunningLetterDistributionOnDemandLookupResult) businessObject, attributeName));

            inquiryHref.setHref(UrlFactory.parameterizeUrl(baseUrl, parameters));
        }
        else if (ArPropertyConstants.DunningCampaignFields.DUNNING_CAMPAIGN_ID.equals(attributeName)) {
            String baseUrl = KFSConstants.INQUIRY_ACTION;
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, DunningCampaign.class.getName());
            parameters.put(ArPropertyConstants.DunningCampaignFields.DUNNING_CAMPAIGN_ID, ObjectUtils.getPropertyValue((DunningLetterDistributionOnDemandLookupResult) businessObject, attributeName));

            inquiryHref.setHref(UrlFactory.parameterizeUrl(baseUrl, parameters));
        }
        else if (ArPropertyConstants.DunningCampaignFields.DUNNING_CAMPAIGN_ID.equals(attributeName)) {
            String baseUrl = KFSConstants.INQUIRY_ACTION;
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, DunningCampaign.class.getName());
            parameters.put(ArPropertyConstants.DunningCampaignFields.DUNNING_CAMPAIGN_ID, ObjectUtils.getPropertyValue((DunningLetterDistributionOnDemandLookupResult) businessObject, attributeName));

            inquiryHref.setHref(UrlFactory.parameterizeUrl(baseUrl, parameters));
        }
        else if (ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER.equals(attributeName)) {

            String documentNumber = ObjectUtils.getPropertyValue((ContractsGrantsInvoiceDocument) businessObject, attributeName).toString();
            inquiryHref.setHref(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + documentNumber + "&command=displayDocSearchView");
        }
        return inquiryHref;
    }
}