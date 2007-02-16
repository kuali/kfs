/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.financial.document.DistributionOfIncomeAndExpenseDocument;

/**
 * This class is the action form for Distribution of Income and Expense.
 */
public class DistributionOfIncomeAndExpenseForm extends KualiAccountingDocumentFormBase {
    /**
     * Constructs a DistributionOfIncomeAndExpenseForm.java.
     */
    public DistributionOfIncomeAndExpenseForm() {
        super();
        setDocument(new DistributionOfIncomeAndExpenseDocument());
    }

    /**
     * @return Returns the DistributionOfIncomeAndExpenseDocument.
     */
    public DistributionOfIncomeAndExpenseDocument getDistributionOfIncomeAndExpenseDocument() {
        return (DistributionOfIncomeAndExpenseDocument) getDocument();
    }

    /**
     * @param distributionOfIncomeAndExpenseDocument
     */
    public void setDistributionOfIncomeAndExpenseDocument(DistributionOfIncomeAndExpenseDocument distributionOfIncomeAndExpenseDocument) {
        setDocument(distributionOfIncomeAndExpenseDocument);
    }
}
