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
package org.kuali.module.cams.rules;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.Pretag;
import org.kuali.module.cams.bo.PretagDetail;

/**
 * This class represents the business rules for the maintenance of {@link AccountGlobal} business objects
 */
public class PretagRule extends MaintenanceDocumentRuleBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PretagRule.class);

    public PretagRule() {
        super();
    }

    /**
     * This method calls checkCampusTagNumber whenever a new {@link PretagDetail} is added to Pretag
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.core.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.core.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        Pretag pretag = (Pretag) document.getNewMaintainableObject().getBusinessObject();
        PretagDetail detail = (PretagDetail) bo;
        boolean success = true;

        detail.setPurchaseOrderNumber(pretag.getPurchaseOrderNumber());
        detail.setLineItemNumber(pretag.getLineItemNumber());
        
        success &= checkTotalDetailCount(pretag);
        success &= checkCampusTagNumber(detail);
         
        return success;
    }
    /**
     * This method ensures that each {@link pretagDetail} tag number does not exist in Asset table
     * 
     * @param dtl
     * @return true if the detail tag doesn't exist in Asset
     */
    public boolean checkTotalDetailCount(Pretag pretag) {
        boolean success = true;

        BigDecimal totalNumerOfDetails = new BigDecimal(pretag.getPretagDetails().size());
        if (pretag.getQuantityInvoiced() == null){
            putFieldError("campusTagNumber", CamsKeyConstants.ERROR_PRE_TAG_DETAIL_EXCESS, "0");
            success &= false;
        } else {
            if (pretag.getQuantityInvoiced().compareTo(totalNumerOfDetails) == 0) {
                putFieldError("campusTagNumber", CamsKeyConstants.ERROR_PRE_TAG_DETAIL_EXCESS, pretag.getPretagDetails().size() + "");
                success &= false;
            }
        }
        return success;
    }
//    intValue
    /**
     * This method ensures that each {@link pretagDetail} tag number does not exist in Asset table
     * 
     * @param dtl
     * @return true if the detail tag doesn't exist in Asset
     */
    public boolean checkCampusTagNumber(PretagDetail dtl) {
        boolean success = true;

        getDictionaryValidationService().validateBusinessObject(dtl);
        if (StringUtils.isNotBlank(dtl.getCampusTagNumber())) {
            Map tagMap = new HashMap();
            tagMap.put("campusTagNumber", dtl.getCampusTagNumber());
            if (getBoService().countMatching(Asset.class, tagMap) != 0) {
                putFieldError("campusTagNumber", CamsKeyConstants.ERROR_PRE_TAG_NUMBER, dtl.getCampusTagNumber());
                success &= false;
            }
        }

        return success;
    }
    
}
