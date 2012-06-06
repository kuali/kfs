/*
 * Copyright 2012 The Kuali Foundation.
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
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

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
