/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.authorization;

import java.util.Date;

import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * This class implements authorization for Tax District Maintenance Document.
 */
public class TaxRegionDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {

    /**
     * If a effective date for a tax rate is not in future, make it read only.
     *   
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase#addMaintenanceDocumentRestrictions(org.kuali.rice.kns.document.MaintenanceDocument, org.kuali.rice.kim.bo.Person)
     */
    @Override
    public void addMaintenanceDocumentRestrictions(MaintenanceDocumentAuthorizations auths,
            MaintenanceDocument document, Person user) {
        auths.clearAllRestrictions();
        TaxRegion taxRegion = (TaxRegion)document.getNewMaintainableObject().getBusinessObject();
        if(taxRegion!=null)
        {
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            Date currentDate = dateTimeService.getCurrentDate();
            int index = 0;
            int comparison = 0;
            for(TaxRegionRate taxRegionRate: taxRegion.getTaxRegionRates()){
                comparison = taxRegionRate.getEffectiveDate().compareTo(currentDate);
                if(comparison==0 || comparison<0)
                    auths.addReadonlyAuthField("taxDistrictRates[" + index + "].taxRate");
                index++;
            }
        }
    }

}
