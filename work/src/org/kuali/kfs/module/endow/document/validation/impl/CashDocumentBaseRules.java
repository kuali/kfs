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
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;

public class CashDocumentBaseRules  extends EndowmentTransactionLinesDocumentBaseRules {
    
    protected boolean isSecurityCodeEmpty(EndowmentTransactionalDocument document,boolean isSource)
    {
       EndowmentTransactionSecurity tranSecurity = getEndowmentTransactionSecurity(document,isSource);
 
        if( StringUtils.isEmpty(tranSecurity.getSecurityID()) )
            return true;
        else
            return false;

    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line) {

        boolean isValid = super.processAddTransactionLineRules(document, line);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            isValid &= validateCashTransactionLine(line, -1);
        }

        return isValid;

    }
    
    /**
     * This method should be overridden by children rule classes as a hook to implement document specific business rule checks for
     * the general validating a transaction line.
     * 
     * @param line
     * @param index
     * @return boolean True if the rules checks passed, false otherwise.
     */
    protected boolean validateCashTransactionLine(EndowmentTransactionLine line, int index) { 
        return true;
    }

}
