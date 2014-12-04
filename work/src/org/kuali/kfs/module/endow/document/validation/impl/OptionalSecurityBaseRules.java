/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument;
import org.kuali.rice.krad.document.Document;

abstract class OptionalSecurityBaseRules extends EndowmentTransactionLinesDocumentBaseRules {

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean value = validateSecurityAndRegistrationRules(document);
        return value && super.processCustomSaveDocumentBusinessRules(document);
    }

    /**
     * checks the security and registration fields for valid values for both cash documents.
     * 
     * @param document - security related documents (Cash and TOF documents)
     */
    protected boolean validateSecurityAndRegistrationRules(Document document) {
        
        EndowmentTransactionalDocument securityDocument = (EndowmentTransactionalDocument)document; 
        
        final boolean isSource = isSourceDocument();
        boolean valid = true;
        
        // Checks if Security field is not empty, security code will be validated      
        if (!isSecurityCodeEmpty(securityDocument, isSource)) {
            valid = validateSecurityCode((EndowmentSecurityDetailsDocument)securityDocument, isSource);

            // Checks for registration code validity
            // Empty registration  code is legitimate
            if (!isRegistrationCodeEmpty(securityDocument, isSource)) {
                final boolean isValidRegistrationCode = validateRegistrationCode((EndowmentSecurityDetailsDocument)securityDocument, isSource);
                valid &= isValidRegistrationCode;
                // Checks if registration code is active
                if (isValidRegistrationCode){
                    valid &= isRegistrationCodeActive((EndowmentSecurityDetailsDocument)securityDocument, isSource);
                }
            }
        }else{
            //without security, we should not have registration
            if (!isRegistrationCodeEmpty(securityDocument, isSource)) {
                putFieldError(getEndowmentTransactionSecurityPrefix((EndowmentTransactionalDocument)securityDocument, isSource) + EndowPropertyConstants.TRANSACTION_REGISTRATION_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_REGISTRATION_CODE_ENTERED_WITHOUT_SECURITY);
                valid = false;
            }
        }
        
        return valid;
    }
    
    /**
     * Override the EndowmentTransactionalDocumentBaseRule and NOT show the error
     * 
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionalDocumentBaseRule#isSecurityCodeEmpty(org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument, boolean)
     */
    @Override
    protected boolean isSecurityCodeEmpty(EndowmentTransactionalDocument document, boolean isSource) {
        EndowmentTransactionSecurity tranSecurity = getEndowmentTransactionSecurity(document, isSource);
        return StringUtils.isEmpty(tranSecurity.getSecurityID());
    }
    

    /**
     * Override the EndowmentTransactionalDocumentBaseRule and NOT show the error
     * 
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionalDocumentBaseRule#isRegistrationCodeEmpty(org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument, boolean)
     */
    @Override
    protected boolean isRegistrationCodeEmpty(EndowmentTransactionalDocument document, boolean isSource) {
        EndowmentTransactionSecurity tranSecurity = getEndowmentTransactionSecurity(document, isSource);
        return StringUtils.isEmpty(tranSecurity.getRegistrationCode());
    }
    
    /**
     * abstract function to determine if it should use it as source or target 
     * 
     * NOTE: This will be defined by the extended rule class, but it really should have been set in the Document
     * 
     * @return
     */
    abstract boolean isSourceDocument(); 
}
