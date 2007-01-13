/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.web.struts.form;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.financial.bo.AdvanceDepositDetail;
import org.kuali.module.financial.document.AdvanceDepositDocument;

/**
 * This class is the struts form for Advance Deposit document.
 * 
 * 
 */
public class AdvanceDepositForm extends KualiTransactionalDocumentFormBase {
    private AdvanceDepositDetail newAdvanceDeposit;

    /**
     * Constructs a AdvanceDepositForm.java.
     */
    public AdvanceDepositForm() {
        super();
        setDocument(new AdvanceDepositDocument());
        setNewAdvanceDeposit(new AdvanceDepositDetail());
    }

    /**
     * @return AdvanceDepositDocument
     */
    public AdvanceDepositDocument getAdvanceDepositDocument() {
        return (AdvanceDepositDocument) getDocument();
    }

    /**
     * @return AdvanceDepositDetail
     */
    public AdvanceDepositDetail getNewAdvanceDeposit() {
        return newAdvanceDeposit;
    }

    /**
     * @param newAdvanceDeposit
     */
    public void setNewAdvanceDeposit(AdvanceDepositDetail newAdvanceDeposit) {
        this.newAdvanceDeposit = newAdvanceDeposit;
    }

    /**
     * Overrides the parent to call super.populate and then tells each line to check the associated data dictionary and modify the
     * values entered to follow all the attributes set for the values of the accounting line.
     * 
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        //
        // now run through all of the accounting lines and make sure they've been uppercased and populated appropriately
        SpringServiceLocator.getBusinessObjectDictionaryService().performForceUppercase(getNewAdvanceDeposit());

        List<AdvanceDepositDetail> advancedDeposits = getAdvanceDepositDocument().getAdvanceDeposits();
        for (AdvanceDepositDetail detail : advancedDeposits) {
            SpringServiceLocator.getBusinessObjectDictionaryService().performForceUppercase(detail);
        }
    }
}