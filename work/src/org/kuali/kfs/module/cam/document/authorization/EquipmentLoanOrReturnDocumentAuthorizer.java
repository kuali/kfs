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
package org.kuali.module.cams.document.authorization;

import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;


/**
 * Uses defaults.
 */
public class EquipmentLoanOrReturnDocumentAuthorizer extends TransactionalDocumentAuthorizerBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnDocumentAuthorizer.class);

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public Map getEditMode(Document document, UniversalUser user) {
        Map<String, String> editModeMap = super.getEditMode(document, user);
        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) document;

        if (ObjectUtils.isNotNull(equipmentLoanOrReturnDocument.getExpectedReturnDate())) {
            editModeMap.put(CamsConstants.EquipmentLoanOrReturnEditMode.DISPLAY_NEW_LOAN_TAB, "FALSE");
        }
        else {
            editModeMap.put(CamsConstants.EquipmentLoanOrReturnEditMode.DISPLAY_NEW_LOAN_TAB, "TRUE");
        }

        return editModeMap;
    }

}
