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
package org.kuali.kfs.vnd.businessobject.inquiry;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class adds in some new sections for {@link Org} inquiries, specifically Org Hierarchy Org Review Hierarchy
 */
public class VendorInquirable extends KfsInquirableImpl {
    
    /**
     * Overrides the helper method to build an inquiry url for a result field. 
     * For the Vendor URL field, returns the url address as the inquiry URL, so that Vendor URL functions as a hyperlink. 
     * 
     * @param bo the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (businessObject instanceof VendorDetail && attributeName.equalsIgnoreCase("vendorUrlAddress")) {
            Object objFieldValue = ObjectUtils.getPropertyValue(businessObject, attributeName);
            String fieldValue = objFieldValue == null ? KFSConstants.EMPTY_STRING : objFieldValue.toString();
            return new AnchorHtmlData("http://" + fieldValue, KRADConstants.EMPTY_STRING, ((VendorDetail)businessObject).getVendorName()+" ("+fieldValue+")");
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}
