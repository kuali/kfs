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

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationController;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;

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
        final FinancialSystemTransactionalDocumentPresentationController presentationController = getPresentationController(accountingDocument);
        final FinancialSystemTransactionalDocumentAuthorizerBase authorizer = getDocumentAuthorizer(accountingDocument);
        if (presentationController == null || authorizer == null) {
            throw new RuntimeException("Null presentation controller or document authorizer for document "+accountingDocument.getClass().getName());
        }
        Set<String> editModes = presentationController.getEditModes(accountingDocument);
        editModes = authorizer.getEditModes(accountingDocument, GlobalVariables.getUserSession().getPerson(), editModes);
        return editModes.contains(PurapAuthorizationConstants.PaymentRequestEditMode.SHOW_AMOUNT_ONLY);
    }
    
    /**
     * 
     * @param accountingDocument
     * @return
     */
    private FinancialSystemTransactionalDocumentPresentationController getPresentationController(AccountingDocument accountingDocument) {
        final Class<? extends DocumentPresentationController> presentationControllerClass = ((TransactionalDocumentEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDictionaryObjectEntry(accountingDocument.getClass().getName())).getDocumentPresentationControllerClass();
        FinancialSystemTransactionalDocumentPresentationController presentationController = null;
        try {
            presentationController = (FinancialSystemTransactionalDocumentPresentationController)presentationControllerClass.newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Cannot instantiate instance of presentation controller for "+accountingDocument.getClass().getName(), ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Cannot instantiate instance of presentation controller for "+accountingDocument.getClass().getName(), iae);
        }
        return presentationController;
    }
    
    /**
     * 
     * @param accountingDocument
     * @return
     */
    private FinancialSystemTransactionalDocumentAuthorizerBase getDocumentAuthorizer(AccountingDocument accountingDocument) {
        final Class<? extends DocumentAuthorizer> documentAuthorizerClass = ((TransactionalDocumentEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDictionaryObjectEntry(accountingDocument.getClass().getName())).getDocumentAuthorizerClass();
        FinancialSystemTransactionalDocumentAuthorizerBase documentAuthorizer = null;
        try {
            documentAuthorizer = (FinancialSystemTransactionalDocumentAuthorizerBase)documentAuthorizerClass.newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Cannot instantiate instance of document authorizer for "+accountingDocument.getClass().getName(), ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Cannot instantiate instance of document authorizer for "+accountingDocument.getClass().getName(), iae);
        }
        return documentAuthorizer;
    }
    
    @Override
    public boolean isGroupEditable(AccountingDocument accountingDocument, 
                                   List<? extends AccountingLineRenderingContext> accountingLineRenderingContexts, 
                                   Person currentUser) {
        
        boolean isEditable = super.isGroupEditable(accountingDocument, accountingLineRenderingContexts, currentUser);
        
        if (isEditable){
            isEditable = allowAccountingLinesAreEditable(accountingDocument,accountingLineRenderingContexts.get(0).getAccountingLine());
        }
        
        return isEditable;
    }
    
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, 
                                                  AccountingLine accountingLine, 
                                                  String accountingLineCollectionProperty, 
                                                  String fieldName) {
        
        boolean isEditable = super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty,fieldName);
        
        if (isEditable){
            isEditable = allowAccountingLinesAreEditable(accountingDocument,accountingLine);
        }
        
        return isEditable;
    }
    
    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, 
                                                 AccountingLine accountingLine, 
                                                 String accountingLineCollectionProperty) {
        
        boolean isEditable = super.determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty);
        
        if (isEditable){
            isEditable = allowAccountingLinesAreEditable(accountingDocument,accountingLine);
        }
        
        return isEditable;
    }
    
    /**
     * This method checks whether the accounting lines are editable for a specific item type.
     * 
     */
    protected final boolean allowAccountingLinesAreEditable(AccountingDocument accountingDocument,
                                                            AccountingLine accountingLine){
        
        PurApAccountingLine purapAccount = (PurApAccountingLine)accountingLine;
        Class clazz = getPurapDocumentClass(accountingDocument);
        if (clazz == null){
            return true;
        }
        
        List<String> restrictedItemTypesList = SpringContext.getBean(ParameterService.class).getParameterValues(clazz, PurapParameterConstants.PURAP_ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT);
        
        if (restrictedItemTypesList != null && purapAccount.getPurapItem() != null){
            return !restrictedItemTypesList.contains(purapAccount.getPurapItem().getItemTypeCode());    
        }else{
            return true;
        }
        
    }
    
    private Class getPurapDocumentClass(AccountingDocument accountingDocument){
        if (accountingDocument instanceof RequisitionDocument){
            return RequisitionDocument.class;
        }else if (accountingDocument instanceof PurchaseOrderDocument){
            return PurchaseOrderDocument.class;
        }else if (accountingDocument instanceof PaymentRequestDocument){
            return PaymentRequestDocument.class;
        }else if (accountingDocument instanceof VendorCreditMemoDocument){
            return VendorCreditMemoDocument.class;
        }else{
            return null;
        }
    }
    
}

