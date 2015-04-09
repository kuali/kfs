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
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.pdp.businessobject.PayeeType;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Customer inquiry for "Payee ACH Account Lookup" results.
 */
public class PayeeAchAccountInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        
        HtmlData htmlData = null;
        
        //
        // Creates a customized inquiry link for the 'payeeName' attribute.
        //
        if (PdpPropertyConstants.PAYEE_NAME.equals(attributeName)) {
            Properties parameters = new Properties();

            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PayeeACHAccount.class.getName());

            KualiInteger generatedIdentifier = (KualiInteger) ObjectUtils.getPropertyValue(businessObject, PdpPropertyConstants.ACH_ACCOUNT_GENERATED_IDENTIFIER);
            parameters.put(PdpPropertyConstants.ACH_ACCOUNT_GENERATED_IDENTIFIER, generatedIdentifier.toString());

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(PdpPropertyConstants.ACH_ACCOUNT_GENERATED_IDENTIFIER, generatedIdentifier.toString());
            
            htmlData = getHyperLink(PayeeACHAccount.class, fieldList, UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, parameters));
        }
        
        //
        // Creates a customized inquiry link for the 'payeeIdentifierTypeCode' attribute.
        //
        else if (PdpPropertyConstants.PAYEE_IDENTIFIER_TYPE_CODE.equals(attributeName)) {
            Properties parameters = new Properties();

            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PayeeType.class.getName());

            String payeeIdentifierCode = (String) ObjectUtils.getPropertyValue(businessObject, PdpPropertyConstants.PAYEE_IDENTIFIER_TYPE_CODE);
            parameters.put(PdpPropertyConstants.PAYEE_CODE, payeeIdentifierCode);

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(PdpPropertyConstants.PAYEE_CODE, payeeIdentifierCode);
            
            htmlData = getHyperLink(PayeeType.class, fieldList, UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, parameters));
        }
        
        //
        // Default.
        //
        else {
            htmlData = super.getInquiryUrl(businessObject, attributeName, forceInquiry);            
        }
        
        return htmlData;
    }

}
