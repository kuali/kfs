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
