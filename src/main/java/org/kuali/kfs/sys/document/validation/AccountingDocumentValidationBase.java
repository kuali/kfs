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
package org.kuali.kfs.sys.document.validation;

import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public abstract class AccountingDocumentValidationBase extends GenericValidation {
    private static ParameterService parameterService;

    protected AccountingDocument accountingDocumentForValidation;

    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    protected Collection<AccountingLine> getAllAccountingLines() {
        Collection<AccountingLine> allAccountingLines = new ArrayList<AccountingLine>();
        allAccountingLines.addAll(accountingDocumentForValidation.getSourceAccountingLines());
        allAccountingLines.addAll(accountingDocumentForValidation.getTargetAccountingLines());
        return allAccountingLines;
    }

    protected ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return parameterService;
    }
}
