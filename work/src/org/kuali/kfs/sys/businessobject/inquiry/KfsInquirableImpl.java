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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.inquiry.KualiInquirableImpl;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;

/**
 * Base KFS Inquirable Implementation
 */
public class KfsInquirableImpl extends KualiInquirableImpl {

    /**
     * Helper method to build an inquiry url for a result field. Special implementation to not build an inquiry link if the value is
     * all dashes.
     * 
     * @param bo the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public String getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (PropertyUtils.isReadable(businessObject, attributeName)) {
            Object objFieldValue = ObjectUtils.getPropertyValue(businessObject, attributeName);
            String fieldValue = objFieldValue == null ? KFSConstants.EMPTY_STRING : objFieldValue.toString();

            if (StringUtils.containsOnly(fieldValue, KFSConstants.DASH)) {
                return KFSConstants.EMPTY_STRING;
            }
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}
