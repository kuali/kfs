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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.document.EquipmentLoanOrReturnDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;


/**
 * Uses defaults.
 */
public class EquipmentLoanOrReturnDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnDocumentAuthorizer.class);

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public Map getEditMode(Document document, Person user) {
        Map<String, String> editModeMap = super.getEditMode(document, user);
        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) document;
        
        // setup new loan document
        if (equipmentLoanOrReturnDocument.isNewLoan()) {
            editModeMap.put(CamsConstants.EquipmentLoanOrReturnEditMode.DISPLAY_NEW_LOAN_TAB, "TRUE");
        }
        else {
            editModeMap.put(CamsConstants.EquipmentLoanOrReturnEditMode.DISPLAY_NEW_LOAN_TAB, "FALSE");
        }
        
        // setup return loan document
        if (equipmentLoanOrReturnDocument.isReturnLoan()) {
            editModeMap.put(CamsConstants.EquipmentLoanOrReturnEditMode.DISPLAY_RETURN_LOAN_FIELDS_READ_ONLY, "TRUE");
        }
        else {
            editModeMap.put(CamsConstants.EquipmentLoanOrReturnEditMode.DISPLAY_RETURN_LOAN_FIELDS_READ_ONLY, "FALSE");
        }
        
        return editModeMap;
    }
}

