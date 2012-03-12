/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.document.authorization;

import java.util.Date;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class TaxRegionMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);

        TaxRegion taxRegion = (TaxRegion) document.getNewMaintainableObject().getBusinessObject();
        if (taxRegion != null) {
            Date currentDate = SpringContext.getBean(DateTimeService.class).getCurrentDate();
            
            int index = 0;
            for (TaxRegionRate taxRegionRate : taxRegion.getTaxRegionRates()) {
                int comparison = taxRegionRate.getEffectiveDate().compareTo(currentDate);

                if (comparison <= 0) {
                    readOnlyPropertyNames.add("taxDistrictRates[" + index + "].taxRate");
                }

                index++;
            }
        }

        return readOnlyPropertyNames;
    }
}
