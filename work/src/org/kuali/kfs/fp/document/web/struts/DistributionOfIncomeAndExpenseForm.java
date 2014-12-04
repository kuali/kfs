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
package org.kuali.kfs.fp.document.web.struts;

import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;

/**
 * This class is the action form for Distribution of Income and Expense.
 */
public class DistributionOfIncomeAndExpenseForm extends CapitalAccountingLinesFormBase {
    
    /**
     * Constructs a DistributionOfIncomeAndExpenseForm.java.
     */
    public DistributionOfIncomeAndExpenseForm() {
        super();
        
        this.capitalAccountingLine.setCanCreateAsset(true); //This document can only edit asset information
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "DI";
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
