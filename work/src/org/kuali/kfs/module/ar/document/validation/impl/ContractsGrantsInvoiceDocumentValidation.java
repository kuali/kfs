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
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Document level validation for Contracts and Grants Invoice Document.
 */
public class ContractsGrantsInvoiceDocumentValidation extends GenericValidation {

    private ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        KualiDecimal amount = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled().subtract(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBilledToDate());

        if (!contractsGrantsInvoiceDocument.isInvoiceReversal()) {
            if (KualiDecimal.ZERO.equals(amount) || amount.isNegative()) {
                GlobalVariables.getMessageMap().putError("newTotalBilled", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the contractsGrantsInvoiceDocument attribute.
     * 
     * @return Returns the contractsGrantsInvoiceDocument.
     */
    public ContractsGrantsInvoiceDocument getContractsGrantsInvoiceDocument() {
        return contractsGrantsInvoiceDocument;
    }

    /**
     * Sets the contractsGrantsInvoiceDocument attribute value.
     * 
     * @param contractsGrantsInvoiceDocument The contractsGrantsInvoiceDocument to set.
     */
    public void setContractsGrantsInvoiceDocument(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        this.contractsGrantsInvoiceDocument = contractsGrantsInvoiceDocument;
    }


}
