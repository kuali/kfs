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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.rice.kns.document.Document;
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

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line) {

        AssetIncreaseDocument assetIncreaseDoc = (AssetIncreaseDocument) document;
        EndowmentTransactionSecurity endowmentTransactionSecurity = assetIncreaseDoc.getTargetTransactionSecurity();
        boolean isValid = super.processAddTransactionLineRules(document, line);

        if (isValid) {

            isValid &= validateSecurityNotEmty(endowmentTransactionSecurity);
            if (isValid) {
                isValid &= validateSecurityClassCodeTypeNotLiability(endowmentTransactionSecurity);
            }
            isValid &= validateTransactionUnitsGreaterThanZero(line, EndowPropertyConstants.TARGET_TRANSACTION_LINE_PREFIX);
        }

        return isValid;
    }

    /**
     * Validates that the security is not empty.
     * 
     * @param document
     * @return true if valid, false otherwise
     */
    private boolean validateSecurityNotEmty(EndowmentTransactionSecurity endowmentTransactionSecurity) {
        boolean isValid = true;

        if (StringUtils.isEmpty(endowmentTransactionSecurity.getSecurityID())) {
            isValid = false;
            putFieldError(EndowPropertyConstants.TRANSACTION_TARGET_SECURITY_PREFIX + EndowPropertyConstants.TRANSACTION_SECURITY_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_REQUIRED);
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
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#validateTransactionLine(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, int)
     */
    @Override
    protected boolean validateTransactionLine(EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase, EndowmentTransactionLine line, int index) {

        boolean isValid = super.validateTransactionLine(endowmentTransactionLinesDocumentBase, line, index);
        EndowmentTargetTransactionLine targetTransactionLine = (EndowmentTargetTransactionLine) line;

        if (isValid) {
            isValid &= cashEndowTranCheck(endowmentTransactionLinesDocumentBase, targetTransactionLine, getErrorPrefix(targetTransactionLine, index));
            isValid &= validateTransactionUnitsGreaterThanZero(line, getErrorPrefix(targetTransactionLine, index));
        }

        return isValid;
    }

}
