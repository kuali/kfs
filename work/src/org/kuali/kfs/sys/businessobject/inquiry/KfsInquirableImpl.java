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
package org.kuali.kfs.inquiry;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.inquiry.KualiInquirableImpl;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.format.Formatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.GLConstants;
import org.kuali.rice.KNSServiceLocator;

public class KfsInquirableImpl extends KualiInquirableImpl {

    /**
     * Helper method to build an inquiry url for a result field.
     * 
     * @param bo the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public String getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {

        // If the field is subAccountNumber, financialSubObjectCode or projectCode and the value is dashes, don't give a url
        if ("subAccountNumber".equals(attributeName) || "financialSubObjectCode".equals(attributeName) || "projectCode".equals(attributeName)) {
            Object objFieldValue = ObjectUtils.getPropertyValue(businessObject, attributeName);
            String fieldValue = objFieldValue == null ? "" : objFieldValue.toString();

            if ("subAccountNumber".equals(attributeName) && fieldValue.equals(KFSConstants.getDashSubAccountNumber())) {
                return "";
            }
            if ("financialSubObjectCode".equals(attributeName) && fieldValue.equals(KFSConstants.getDashFinancialSubObjectCode())) {
                return "";
            }
            if ("projectCode".equals(attributeName) && fieldValue.equals(KFSConstants.getDashProjectCode())) {
                return "";
            }
        }
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }
    
}
