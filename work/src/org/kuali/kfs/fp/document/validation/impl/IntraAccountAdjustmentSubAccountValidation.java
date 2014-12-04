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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * validate the sub accounts which have values in CS and ICR sections
 */
public class IntraAccountAdjustmentSubAccountValidation extends GenericValidation {
    private Logger logger = Logger.getLogger(IntraAccountAdjustmentSubAccountValidation.class);
    private AccountingDocument accountingDocumentForValidation;
    private AccountingLine accountingLineForValidation;


    @Override
    public boolean validate(AttributedDocumentEvent event) {
        AccountingLineBase accountingLineBase= (AccountingLineBase) this.getAccountingLineForValidation();
        A21SubAccount a21SubAccount=null;
        if(ObjectUtils.isNotNull(accountingLineBase) && ObjectUtils.isNotNull(accountingLineBase.getSubAccountNumber()) && ObjectUtils.isNotNull(accountingLineBase.getSubAccount())){
            a21SubAccount=accountingLineBase.getSubAccount().getA21SubAccount();
             if (ObjectUtils.isNotNull(a21SubAccount)&&(ObjectUtils.isNotNull(a21SubAccount.getCostShareChartOfAccountCode())||
                    ObjectUtils.isNotNull(a21SubAccount.getCostShareSourceAccountNumber()) ||(a21SubAccount.getA21ActiveIndirectCostRecoveryAccounts().size() > 0) ||
                            ObjectUtils.isNotNull(a21SubAccount.getCostShareSourceSubAccount()))) {
                GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.IntraAccountAdjustment.ERROR_CS_ICR_SUBACCOUNTS_NOT_ALLOWED);
                return false;
            }

        }
       return true;
    }


    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }


    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }


    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }


    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }


}
