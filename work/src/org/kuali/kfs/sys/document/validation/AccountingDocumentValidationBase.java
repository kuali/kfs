/*
 * Copyright 2012 The Kuali Foundation.
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
