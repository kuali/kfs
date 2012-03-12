/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashControlDocumentHasDetailsValidation extends GenericValidation {

    private CashControlDocument cashControlDocument;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        boolean isValid = true;
        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        if (cashControlDocument.getCashControlDetails().isEmpty()) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.CASH_CONTROL_DETAILS, ArKeyConstants.ERROR_NO_LINES_TO_PROCESS);
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        return isValid;
    }

    public CashControlDocument getCashControlDocument() {
        return cashControlDocument;
    }

    public void setCashControlDocument(CashControlDocument cashControlDocument) {
        this.cashControlDocument = cashControlDocument;
    }

}
