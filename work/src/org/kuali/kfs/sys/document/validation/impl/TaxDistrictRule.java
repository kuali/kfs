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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.Date;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This class implements add collection line business rule for tax district rate.
 */
public class TaxDistrictRule extends KfsMaintenanceDocumentRuleBase {

    /**
     * Rules:
     * 1) Effective date should be a future date.
     * 2) Tax rate should be a numeric value between 0 and 1 (inclusive).
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(
            MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        TaxRegionRate taxRegionRate = (TaxRegionRate)bo;
        boolean success = true;
        if(taxRegionRate!=null)
        {
            if(taxRegionRate.getEffectiveDate()!=null)
            {
                DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
                Date currentDate = dateTimeService.getCurrentDate();
                int comparison = taxRegionRate.getEffectiveDate().compareTo(currentDate);
                if(comparison==0 || comparison<0)
                {
                    GlobalVariables.getErrorMap().putError("effectiveDate", 
                            KFSKeyConstants.ERROR_DOCUMENT_TAX_DISTRICT_CANT_ADD_PAST_OR_CURRENT_DATE_FOR_TAX_DISTRICT);
                    success = false;
                }
            }    
            if(taxRegionRate.getTaxRate()!=null)
            {
                if(taxRegionRate.getTaxRate().intValue()>1 || taxRegionRate.getTaxRate().intValue()<0)
                {
                    GlobalVariables.getErrorMap().putError("taxRate", 
                            KFSKeyConstants.ERROR_DOCUMENT_TAX_DISTRICT_TAX_RATE_BETWEEN0AND1);
                    success = false;
                }
            }
        }
        return success;
    }

}