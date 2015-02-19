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
package org.kuali.kfs.pdp.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.lookup.AbstractPayeeLookupableHelperServiceImpl;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;

public class PayeeACHAccountLookupableHelperServiceImpl extends AbstractPayeeLookupableHelperServiceImpl {
    
    /**
     * @see AbstractPayeeLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction
     * Allows copy on a PayeeACHAccount record only if edit is allowed on the record.
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (allowsMaintenanceEditAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceNewOrCopyAction() && allowsMaintenanceEditAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceDeleteAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_DELETE_METHOD_TO_CALL, pkNames));
        }
        return htmlDataList;
    }

    /**
     * @see AbstractPayeeLookupableHelperServiceImpl#getInquiryUrl
     * For payeeName, creates an inquiry link for the PayeeACHAccount record only if the user is allowed to inquire the record.
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        // for properties other than payeeName, or if the user is allowed to inquire the record, return inquiry link as done in super
        // NOTE: Since we don't have separate permission for inquiring PayeeACHAccount, we regard the permission for inquiry as equivalent to the permission for edit.
        if (!StringUtils.equals(PdpPropertyConstants.PAYEE_NAME, propertyName) || allowsMaintenanceEditAction(bo)) 
            return super.getInquiryUrl(bo, propertyName);

        // otherwise return empty inquiry link 
        return new HtmlData.AnchorHtmlData();
    }
}
