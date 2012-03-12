/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.fp.businessobject.DisbursementVoucherDocumentationLocation;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.CodeDescriptionFormatterBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * The values are the same as the disbursementVoucherDocumentationLocationCode in the DisbursementVoucherDocumentationLocation
 * class.
 */
public class DocumentationLocationCodeDescriptionFormatter extends CodeDescriptionFormatterBase {
    @Override
    protected String getDescriptionOfBO(PersistableBusinessObject bo) {
        return ((DisbursementVoucherDocumentationLocation) bo).getDisbursementVoucherDocumentationLocationName();
    }

    @Override
    protected Map<String, PersistableBusinessObject> getValuesToBusinessObjectsMap(Set values) {
        Map<String, PersistableBusinessObject> map = new HashMap<String, PersistableBusinessObject>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE_PROPERTY_NAME, values);
        Collection<DisbursementVoucherDocumentationLocation> coll = SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(DisbursementVoucherDocumentationLocation.class, criteria, "versionNumber", true);
        // by sorting on ver #, we can guarantee that the most recent value will remain in the map (assuming the iterator returns
        // BOs in order)
        for (DisbursementVoucherDocumentationLocation dvdl : coll) {
            map.put(dvdl.getDisbursementVoucherDocumentationLocationCode(), dvdl);
        }
        return map;
    }
}
