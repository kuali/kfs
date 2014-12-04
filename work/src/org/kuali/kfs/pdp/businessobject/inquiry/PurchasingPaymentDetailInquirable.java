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
package org.kuali.kfs.pdp.businessobject.inquiry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PurchasingPaymentDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class PurchasingPaymentDetailInquirable extends PaymentDetailInquirable {

    /**
     * @see org.kuali.kfs.pdp.businessobject.inquiry.PaymentDetailInquirable#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        PaymentDetail paymentDetail = (PaymentDetail) businessObject;
        if (PdpPropertyConstants.PaymentDetail.PAYMENT_DETAIL_NUMBER_OF_PAYMENTS_IN_PAYMENT_GROUP.equals(attributeName) && ObjectUtils.isNotNull(paymentDetail.getPaymentGroupId())) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PurchasingPaymentDetail.class.getName());
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
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PurchasingPaymentDetail.class.getName());
            params.put(KRADConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            params.put(KFSConstants.LOOKUP_READ_ONLY_FIELDS, PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER);
            params.put(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER, UrlFactory.encode(String.valueOf(paymentDetail.getPaymentGroup().getDisbursementNbr())));
            String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER, paymentDetail.getPaymentGroup().getDisbursementNbr().toString());

            return getHyperLink(PaymentDetail.class, fieldList, url);
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}
