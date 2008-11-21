/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.AuthorizationConstants;

/**
 * Authorizer which deals with financial processing document issues, specifically sales tax lines on documents
 * This class utilizes the new accountingLine model.
 */
public class PurapAccountingLineAuthorizer extends AccountingLineAuthorizerBase {

    /**
     * Overrides the method in AccountingLineAuthorizerBase so that the add button would
     * have the line item number in addition to the rest of the insertxxxx String for
     * methodToCall when the user clicks on the add button.
     * 
     * @param accountingLine
     * @param accountingLineProperty
     * @return
     */
    @Override
    protected String getAddMethod(AccountingLine accountingLine, String accountingLineProperty) {
        final String infix = getActionInfixForNewAccountingLine(accountingLine, accountingLineProperty);
        String lineNumber = null;
        if (accountingLineProperty.equals(PurapPropertyConstants.ACCOUNT_DISTRIBUTION_NEW_SRC_LINE)) {
            lineNumber = "-2";
        }
        else {
        lineNumber = StringUtils.substringBetween(accountingLineProperty, "[", "]");
        }
        return "insert"+infix + "Line.line" + lineNumber + "." + "anchoraccounting"+infix+"Anchor";
    }
    
    /**
     * Overrides the method in AccountingLineAuthorizerBase so that the delete button would have both
     * the line item number and the accounting line number for methodToCall when the user clicks on
     * the delete button.
     * 
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getDeleteLineMethod(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getDeleteLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        String lineNumber = StringUtils.substringBetween(accountingLineProperty, "item[", "].sourceAccountingLine");
        if (lineNumber == null) {
            lineNumber = "-2";
        }
        String accountingLineNumber = StringUtils.substringBetween(accountingLineProperty, "sourceAccountingLine[", "]");
        return "delete"+infix+"Line.line"+ lineNumber + ":" + accountingLineNumber + ".anchoraccounting"+infix+"Anchor";
    }
    
    /**
     * Overrides the method in AccountingLineAuthorizerBase so that the balance inquiry button would 
     * have both the line item number and the accounting line number for methodToCall when the user 
     * clicks on the balance inquiry button.
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getBalanceInquiryMethod(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getBalanceInquiryMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForNewAccountingLine(accountingLine, accountingLineProperty);
        String lineNumber = StringUtils.substringBetween(accountingLineProperty, "item[", "].sourceAccountingLine");
        if (lineNumber == null) {
            lineNumber = "-2";
        }
        String accountingLineNumber = StringUtils.substringBetween(accountingLineProperty, "sourceAccountingLine[", "]");
        return "performBalanceInquiryFor"+infix+"Line.line"+ ":" + lineNumber + ":" + accountingLineNumber + ".anchoraccounting"+infix+ "existingLineLineAnchor"+accountingLineNumber;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getUnviewableBlocks(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean, org.kuali.rice.kim.bo.Person)
     */
    @Override
    public Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        Set unviewableBlocks = super.getUnviewableBlocks(accountingDocument, accountingLine, newLine, currentUser);
        if (showAmountOnly(accountingDocument)) {
            unviewableBlocks.add(KFSPropertyConstants.PERCENT);
        }
        else {
            unviewableBlocks.add(KFSPropertyConstants.AMOUNT);
        }
        return unviewableBlocks;
    }
    
    private boolean showAmountOnly(AccountingDocument accountingDocument) {
        Map editModes = SpringContext.getBean(AccountingLineRenderingService.class).getEditModes(accountingDocument);
        String showAmountOnlyValue = (String)(editModes.get(PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY));
        if (StringUtils.equals(showAmountOnlyValue, "TRUE")) {
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public String getEditModeForAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser, Map<String, String> editModesForDocument) {
        if (editModesForDocument.containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) {
            return AuthorizationConstants.EditMode.UNVIEWABLE;
        }
        
        if (editModesForDocument.containsKey(AuthorizationConstants.EditMode.FULL_ENTRY)) {
            return AuthorizationConstants.EditMode.FULL_ENTRY;
        }
        
        final boolean isAccountingLineEditable = this.isAccountingLineEditable(accountingDocument, accountingLine, currentUser);        
        return (!isAccountingLineEditable ? AuthorizationConstants.EditMode.VIEW_ONLY : AuthorizationConstants.EditMode.FULL_ENTRY);
    }
    
}

