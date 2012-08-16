/*
 * Copyright 2012 The Kuali Foundation.
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
