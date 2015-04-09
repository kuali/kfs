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
package org.kuali.kfs.fp.document.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.PaymentDocumentationLocation;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.CodeDescriptionFormatterBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * The values are the same as the disbursementVoucherDocumentationLocationCode in the PaymentDocumentationLocation
 * class.
 */
public class DocumentationLocationCodeDescriptionFormatter extends CodeDescriptionFormatterBase {
    @Override
    protected String getDescriptionOfBO(PersistableBusinessObject bo) {
        return ((PaymentDocumentationLocation) bo).getPaymentDocumentationLocationName();
    }

    @Override
    protected Map<String, PersistableBusinessObject> getValuesToBusinessObjectsMap(Set values) {
        Map<String, PersistableBusinessObject> map = new HashMap<String, PersistableBusinessObject>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE_PROPERTY_NAME, values);
        Collection<PaymentDocumentationLocation> coll = SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(PaymentDocumentationLocation.class, criteria, "versionNumber", true);
        // by sorting on ver #, we can guarantee that the most recent value will remain in the map (assuming the iterator returns
        // BOs in order)
        for (PaymentDocumentationLocation dvdl : coll) {
            map.put(dvdl.getPaymentDocumentationLocationCode(), dvdl);
        }
        return map;
    }
}
