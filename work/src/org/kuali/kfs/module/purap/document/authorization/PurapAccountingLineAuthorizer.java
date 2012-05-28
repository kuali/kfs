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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationController;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.DocumentPresentationController;
import org.kuali.rice.krad.util.GlobalVariables;

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
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getUnviewableBlocks(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        Set<String> unviewableBlocks = super.getUnviewableBlocks(accountingDocument, accountingLine, newLine, currentUser);
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
        return editModes.contains(PurapAuthorizationConstants.PaymentRequestEditMode.FULL_DOCUMENT_ENTRY_COMPLETED);
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
    protected FinancialSystemTransactionalDocumentAuthorizerBase getDocumentAuthorizer(AccountingDocument accountingDocument) {
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
            if (accountingLineRenderingContexts.size() == 0) {
                return false;
            }
            isEditable = allowAccountingLinesAreEditable(accountingDocument,accountingLineRenderingContexts.get(0).getAccountingLine());
        }
        
        return isEditable;
    }
    
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, 
                                                  AccountingLine accountingLine, 
                                                  String accountingLineCollectionProperty, 
                                                  String fieldName,
                                                  boolean editablePage) {
        
        boolean isEditable = super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty,fieldName,editablePage);
        
        if (isEditable){
            isEditable = allowAccountingLinesAreEditable(accountingDocument,accountingLine);
        }
        
        return isEditable;
    }
    
    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, 
                                                 AccountingLine accountingLine, 
                                                 String accountingLineCollectionProperty,
                                                 boolean currentUserIsDocumentInitiator, 
                                                 boolean pageIsEditable) {
        
        boolean isEditable = super.determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUserIsDocumentInitiator, pageIsEditable);
        
        if (isEditable){
            isEditable = allowAccountingLinesAreEditable(accountingDocument,accountingLine);
        }
        
        return (isEditable && pageIsEditable);
    }
    
    /**
     * This method checks whether the accounting lines are editable for a specific item type.
     * 
     */
    protected boolean allowAccountingLinesAreEditable(AccountingDocument accountingDocument,
                                                            AccountingLine accountingLine){
        
        PurApAccountingLine purapAccount = (PurApAccountingLine)accountingLine;
        @SuppressWarnings("rawtypes")
        Class clazz = getPurapDocumentClass(accountingDocument);
        if (clazz == null){
            return true;
        }
        
        //if not calculated yet then the line is editable
        PurchasingDocument purDoc = (PurchasingDocument) accountingDocument;
        if (!purDoc.isCalculated()) {
            return true;
        }
        
        Collection<String> restrictedItemTypesList = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(clazz, PurapParameterConstants.PURAP_ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT) );

        // This call determines a new special case in which an item marked for trade-in cannot have editable accounting lines
        // once the calculate button image is clicked, even if the accounting line has not been saved yet.
        boolean retval = true;
        retval = isEditableBasedOnTradeInRestriction(accountingDocument, accountingLine);
        
        if (restrictedItemTypesList != null && purapAccount.getPurapItem() != null){
            return (!restrictedItemTypesList.contains(((PurApItem) purapAccount.getPurapItem()).getItemTypeCode()) && retval);
        } else if (restrictedItemTypesList != null && purapAccount.getPurapItem() == null) {
            return retval;
        } else {
            return true;
        }
    }

    /**
     * Find the item to which an accounting line belongs. Convenience/Utility method.
     * 
     * Some methods that require an accounting line with a purApItem attached were getting accounting lines
     * passed in that did not yet have a purApItem. I needed a way to match the accounting line to the 
     * proper item.
     * 
     * @param accountingDocument the document holding both the accounting line and the item to which the
     * accounting line is attached
     * @param accountingLine the accounting line of interest, for which a containing item should be found
     * @return the item to which the incoming accounting line is attached
     */
    protected PurApItem findTheItemForAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        PurApItem retval = null;
        List<PurApItem> listItems = null;

        scan: {
            if (accountingDocument instanceof PurchasingAccountsPayableDocumentBase) {
                listItems = ((PurchasingAccountsPayableDocumentBase) accountingDocument).getItems();

                // loop through all items in the document to see if the item has an accounting line that
                // matches the one passed in
                for (PurApItem oneItem : listItems) {
                    List<PurApAccountingLine> acctLines = oneItem.getSourceAccountingLines();
                    for (PurApAccountingLine oneAcctLine : acctLines) {
                        // we want to compare the exact same memory location, not the contents
                        if (oneAcctLine == accountingLine) {
                            retval = oneItem;

                            // we found it, so I can stop altogether.
                            break scan;
                        }
                    }
                }
            }
        }

        return retval;
    }

    /**
     * Handles a restriction on accounting lines assigned to trade-in items.
     * If the accounting Line is for a trade-in item type, and the accounting line has contents, 
     * the user is not allowed to change the contents of the calculated values. 
     * 
     * The trade-in may not yet have a sequence number, so the old way of relying solely on sequence 
     * number (in method super.approvedForUnqualifiedEditing() is incomplete, and needs this extra check 
     * for trade-ins.
     * 
     * @param accountingLine the accounting line being examined
     * @return whether the accounting line is editable according to the trade-in/non-empty restriction
     */
    private boolean isEditableBasedOnTradeInRestriction(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean retval = true;
        // if the accounting Line is for a trade-In, and the line has contents, the user is not allowed to
        // change the contents of the calculated values
        if ((accountingLine != null) && (accountingLine instanceof PurApAccountingLine)) {
            PurApItem purApItem = (((PurApAccountingLine) accountingLine)).getPurapItem();

            // this small block is to allow for another way to get to the purApItem if the
            // incoming accounting line does not yet have a purApItem attached. Calling it is not
            // completely necessary any more, unless/until the functional team members decide to
            // add more item types to the read-only accounting lines list.
            if (purApItem == null) {
                purApItem = findTheItemForAccountingLine(accountingDocument, accountingLine);
            }

            if (purApItem != null) {
                String itemTypeCode = purApItem.getItemTypeCode();
                if (itemTypeCode.toUpperCase().equalsIgnoreCase(PurapParameterConstants.PURAP_ITEM_TYPE_TRDI)) {
                    // does the line have any contents? if so, then the user cannot edit them
                    if ((accountingLine.getChartOfAccountsCode() != null) || (accountingLine.getAccountNumber() != null) || (accountingLine.getFinancialObjectCode() != null)) {
                        retval = false;
                    }
                }
                // there has been a call to "if (purApItem.getItemAssignedToTradeInIndicator()) {" here
                // that required the earlier use of findTheItemForAccountingLine()
            }
        }
        return retval;
    }
    
    @SuppressWarnings("rawtypes")
    private Class getPurapDocumentClass(AccountingDocument accountingDocument){
        if (accountingDocument instanceof RequisitionDocument){
            return RequisitionDocument.class;
        }else if (accountingDocument instanceof PurchaseOrderDocument){
            return PurchaseOrderDocument.class;
        }else if (accountingDocument instanceof PaymentRequestDocument){
            return PaymentRequestDocument.class;
        }else{
            return null;
        }
    }

    /**
     * Determines if the given line is editable, no matter what a KIM check would say about line editability.  In the default case,
     * any accounting line is editable - minus KIM check - when the document is PreRoute, or if the line is a new line
     * 
     * This overriding implementation is required because the Purap module has a new restriction that requires
     * that an accounting line for a Trade-In item cannot be manually editable, even if not yet saved ("not yet saved" means it has 
     * no sequence number). Therefore, the base implementation that determines editability on the sequence number alone has to be 
     * preceded by a check that declares ineligible for editing if it is a trade-in. 
     * 
     * @see org.kuali.kfs.module.purap.document.authorization.PurapAccountingLineAuthorizer#allowAccountingLinesAreEditable(AccountingDocument, AccountingLine)
     *
     * @param accountingDocument the accounting document the line is or wants to be associated with
     * @param accountingLine the accounting line itself
     * @param accountingLineCollectionProperty the collection the accounting line is or would be part of
     * @param currentUserIsDocumentInitiator is the current user the initiator of the document?
     * @return true if the line as a whole can be edited without the KIM check, false otherwise
     */
    @Override
    protected boolean approvedForUnqualifiedEditing(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, boolean currentUserIsDocumentInitiator) {
        boolean retval = true;

        retval = isEditableBasedOnTradeInRestriction(accountingDocument, accountingLine);

        if (retval) {
            retval = super.approvedForUnqualifiedEditing(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUserIsDocumentInitiator);
        }
        return retval;
    }
}

