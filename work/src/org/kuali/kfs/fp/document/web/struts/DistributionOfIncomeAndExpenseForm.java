/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.web.struts;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;

/**
 * This class is the action form for Distribution of Income and Expense.
 */
public class DistributionOfIncomeAndExpenseForm extends KualiAccountingDocumentFormBase implements CapitalAssetEditable{
    
    private CapitalAssetInformation capitalAssetInformation;
    
    /**
     * Constructs a DistributionOfIncomeAndExpenseForm.java.
     */
    public DistributionOfIncomeAndExpenseForm() {
        super();
        this.setCapitalAssetInformation(new CapitalAssetInformation());
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

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#getCapitalAssetInformation()
     */
    public CapitalAssetInformation getCapitalAssetInformation() {
        return capitalAssetInformation;
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#setCapitalAssetInformation(org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     */
    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }
}
