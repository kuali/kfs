/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.ClassCodeType;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class AssetIncreaseDocumentRules extends EndowmentTransactionLinesDocumentBaseRules {

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processSaveDocument(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean processSaveDocument(Document document) {
        boolean isValid = super.processSaveDocument(document);

        if (isValid) {
            AssetIncreaseDocument assetIncreaseDoc = (AssetIncreaseDocument) document;
            EndowmentTransactionSecurity endowmentTransactionSecurity = assetIncreaseDoc.getTargetTransactionSecurity();

            isValid &= validateSecurityClassCodeTypeNotLiability(endowmentTransactionSecurity);
        }
        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processRouteDocument(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean processRouteDocument(Document document) {
        boolean isValid = super.processRouteDocument(document);

        if (isValid) {
            AssetIncreaseDocument assetIncreaseDoc = (AssetIncreaseDocument) document;
            EndowmentTransactionSecurity endowmentTransactionSecurity = assetIncreaseDoc.getTargetTransactionSecurity();
        }
        return isValid;
    }

    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument transLine, EndowmentTransactionLine line) {

        boolean isValid = super.processAddTransactionLineRules(transLine, line);

        if (isValid) {
            isValid &= validateTransactionLineUnitsGreaterThanZero(line);
        }

        return isValid;
    }
    
    /**
     * Validates that the security class code type is not Liability.
     * 
     * @param endowmentTransactionSecurity
     * @return true is valid, false otherwise
     */
    private boolean validateSecurityClassCodeTypeNotLiability(EndowmentTransactionSecurity endowmentTransactionSecurity) {
        boolean isValid = true;
        Security security = endowmentTransactionSecurity.getSecurity();
        if (ObjectUtils.isNotNull(security)) {
            ClassCode classCode = security.getClassCode();
            if (ObjectUtils.isNotNull(classCode)) {
                String classCodeType = classCode.getClassCodeType();
                if (EndowConstants.ClassCodeTypes.LIABILITY.equalsIgnoreCase(classCodeType)) {
                    isValid = false;

                    putFieldError(EndowPropertyConstants.TRANSACTION_TARGET_SECURITY_PREFIX + EndowPropertyConstants.TRANSACTION_SECURITY_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_NOT_LIABILITY);
                }
            }
        }

        return isValid;

    }

    /**
     * Validates that the transaction line's units is greater than zero.
     * 
     * @param endowmentTransactionLine
     * @return true if valid, false otherwise
     */
    private boolean validateTransactionLineUnitsGreaterThanZero(EndowmentTransactionLine endowmentTransactionLine) {
        boolean isValid = true;
        KualiDecimal units = endowmentTransactionLine.getTransactionUnits();

        if (units != null && units.isLessEqual(KualiDecimal.ZERO)) {
            isValid = false;

            putFieldError(EndowPropertyConstants.TARGET_TRANSACTION_LINE_PREFIX + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_UNITS_GREATER_THAN_ZERO);
        }

        return isValid;
    }

}
