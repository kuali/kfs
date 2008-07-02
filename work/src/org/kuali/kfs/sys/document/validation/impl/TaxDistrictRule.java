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

import org.apache.struts.taglib.tiles.GetTag;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.TaxDistrict;
import org.kuali.kfs.sys.businessobject.TaxDistrictRate;
import org.kuali.kfs.sys.context.SpringContext;

public class TaxDistrictRule extends KfsMaintenanceDocumentRuleBase {

    @Override
    public boolean processCustomAddCollectionLineBusinessRules(
            MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        TaxDistrictRate taxDistrictRate = (TaxDistrictRate)bo;
        boolean success = true;
        if(taxDistrictRate!=null)
        {
            if(taxDistrictRate.getEffectiveDate()!=null)
            {
                DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
                Date currentDate = dateTimeService.getCurrentDate();
                int comparison = taxDistrictRate.getEffectiveDate().compareTo(currentDate);
                if(comparison==0 || comparison<0)
                {
                    GlobalVariables.getErrorMap().putError("effectiveDate", 
                            KFSKeyConstants.ERROR_DOCUMENT_TAX_DISTRICT_CANT_ADD_PAST_OR_CURRENT_DATE_FOR_TAX_DISTRICT);
                    success = false;
                }
            }    
            if(taxDistrictRate.getTaxRate()!=null)
            {
                if(taxDistrictRate.getTaxRate().intValue()>1 || taxDistrictRate.getTaxRate().intValue()<0)
                {
                    GlobalVariables.getErrorMap().putError("effectiveDate", 
                            KFSKeyConstants.ERROR_DOCUMENT_TAX_DISTRICT_TAX_RATE_BETWEEN0AND1);
                    success = false;
                }
            }
        }
        return success;
    }

}