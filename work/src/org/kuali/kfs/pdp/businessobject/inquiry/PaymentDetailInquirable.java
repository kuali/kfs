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
package org.kuali.kfs.pdp.businessobject.inquiry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.service.ResearchParticipantPaymentValidationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.datadictionary.InquirySectionDefinition;
import org.kuali.rice.kns.inquiry.InquiryRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.kns.web.ui.SectionBridge;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class PaymentDetailInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        PaymentDetail paymentDetail = (PaymentDetail) businessObject;
        if (PdpPropertyConstants.PaymentDetail.PAYMENT_DETAIL_NUMBER_OF_PAYMENTS_IN_PAYMENT_GROUP.equals(attributeName) && ObjectUtils.isNotNull(paymentDetail.getPaymentGroupId())) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PaymentDetail.class.getName());
            params.put(KRADConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            params.put(KFSConstants.LOOKUP_READ_ONLY_FIELDS, PdpPropertyConstants.PaymentDetail.PAYMENT_DETAIL_PAYMENT_GROUP_ID);
            params.put(PdpPropertyConstants.PaymentDetail.PAYMENT_DETAIL_PAYMENT_GROUP_ID, UrlFactory.encode(String.valueOf(paymentDetail.getPaymentGroupId())));
            String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(PdpPropertyConstants.PaymentDetail.PAYMENT_DETAIL_PAYMENT_GROUP_ID, paymentDetail.getPaymentGroupId().toString());

            return getHyperLink(PaymentDetail.class, fieldList, url);
        }

        if (PdpPropertyConstants.PaymentDetail.PAYMENT_DETAIL_NUMBER_OF_PAYMENTS_IN_DISBURSEMENT.equals(attributeName) && ObjectUtils.isNotNull(paymentDetail.getPaymentGroup().getDisbursementNbr())) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PaymentDetail.class.getName());
            params.put(KRADConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            params.put(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER, UrlFactory.encode(String.valueOf(paymentDetail.getPaymentGroup().getDisbursementNbr())));
            String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER, paymentDetail.getPaymentGroup().getDisbursementNbr().toString());

            return getHyperLink(PaymentDetail.class, fieldList, url);
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }


    /**
     * Overrides superclass method so that we could mask the payee name field whenever the customer profile is
     * the one for Research Participant Upload and don't need to mask the payee name field for any other cases.
     *
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public List<Section> getSections(BusinessObject bo) {
        PaymentDetail paymentDetail = (PaymentDetail) bo;
        CustomerProfile customerProfile = paymentDetail.getPaymentGroup().getBatch().getCustomerProfile();
        List<Section> sections = new ArrayList<Section>();
        String researchParticipantCustomer = SpringContext.getBean(ParameterService.class).getParameterValueAsString(PaymentDetail.class, PdpConstants.RESEARCH_PARTICIPANT_CUSTOMER_PROFILE);

        List<String> payeeFieldsToBeMasked = new ArrayList<String>();
        payeeFieldsToBeMasked.add("paymentGroup.payeeName");
        payeeFieldsToBeMasked.add("paymentGroup.street");
        payeeFieldsToBeMasked.add("paymentGroup.city");
        payeeFieldsToBeMasked.add("paymentGroup.state");
        payeeFieldsToBeMasked.add("paymentGroup.zipCd");

        Map fieldOrigAttDef = new HashMap<String, AttributeSecurity>();

        InquiryRestrictions inquiryRestrictions = null;
        BusinessObjectEntry businessObjectEntry = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(PaymentDetail.class.getName());

        boolean isResearchParticipantPayment = (SpringContext.getBean(ResearchParticipantPaymentValidationService.class)).isResearchParticipantPayment(customerProfile);

        for (String field : payeeFieldsToBeMasked) {
            // Obtain and keep a copy of the original attributeSecurity of payeeName from the DD in the map to be retrieved later.
            AttributeDefinition attributeDefinition = businessObjectEntry.getAttributeDefinition(field);
            fieldOrigAttDef.put(field, attributeDefinition.getAttributeSecurity());

            if (!isResearchParticipantPayment) {
                // This is not the case for the Research Upload, so we don't need to apply mask on the payee name.
                attributeDefinition.setAttributeSecurity(null);
            }
            if (getBusinessObjectClass() == null) {
                throw new RuntimeException("Business object class not set in inquirable.");
            }
        }

        inquiryRestrictions = SpringContext.getBean(BusinessObjectAuthorizationService.class).getInquiryRestrictions(bo, GlobalVariables.getUserSession().getPerson());

        Collection<InquirySectionDefinition> inquirySections = getBusinessObjectDictionaryService().getInquirySections(getBusinessObjectClass());
        for (Iterator<InquirySectionDefinition> iter = inquirySections.iterator(); iter.hasNext();) {
            InquirySectionDefinition inquirySection = iter.next();
            if (!inquiryRestrictions.isHiddenSectionId(inquirySection.getId())) {
                Section section = SectionBridge.toSection(this, inquirySection, bo, inquiryRestrictions);
                sections.add(section);
            }
        }

        // setting back the attribute security just as in DD so that we don't override it with null.
        for (String field : payeeFieldsToBeMasked) {
            AttributeDefinition attributeDefinition = businessObjectEntry.getAttributeDefinition(field);
            AttributeSecurity originalAttributeSecurity = (AttributeSecurity) fieldOrigAttDef.get(field);
            attributeDefinition.setAttributeSecurity(originalAttributeSecurity);
        }

        return sections;
    }

}
