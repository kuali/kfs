/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject.inquiry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Base KFS Inquirable Implementation
 */
public class KfsInquirableImpl extends KualiInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KfsInquirableImpl.class);
    /**
     * Helper method to build an inquiry url for a result field. Special implementation to not build an inquiry link if the value is
     * all dashes.
     * 
     * @param bo the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        try {
            if (PropertyUtils.isReadable(businessObject, attributeName)) {
                Object objFieldValue = ObjectUtils.getPropertyValue(businessObject, attributeName);
                String fieldValue = objFieldValue == null ? KFSConstants.EMPTY_STRING : objFieldValue.toString();
    
                if (StringUtils.containsOnly(fieldValue, KFSConstants.DASH)) {
                    return new AnchorHtmlData();
                }
            }
    
            return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
        } catch ( Exception ex ) {
            LOG.error( "Unable to determine inquiry link for BO Class: " + businessObject.getClass() + " and property " + attributeName );
            LOG.debug( "Unable to determine inquiry link for BO Class: " + businessObject.getClass() + " and property " + attributeName, ex );
            return new AnchorHtmlData();
        }
    }

}
