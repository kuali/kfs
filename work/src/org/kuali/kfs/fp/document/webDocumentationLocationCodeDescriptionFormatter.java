/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.bo.DisbursementVoucherDocumentationLocation;

/**
 * The values are the same as the disbursementVoucherDocumentationLocationCode in the DisbursementVoucherDocumentationLocation
 * class.
 * 
 * 
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
        Collection<DisbursementVoucherDocumentationLocation> coll = SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(DisbursementVoucherDocumentationLocation.class, criteria, "versionNumber", true);
        // by sorting on ver #, we can guarantee that the most recent value will remain in the map (assuming the iterator returns
        // BOs in order)
        for (DisbursementVoucherDocumentationLocation dvdl : coll) {
            map.put(dvdl.getDisbursementVoucherDocumentationLocationCode(), dvdl);
        }
        return map;
    }
}
