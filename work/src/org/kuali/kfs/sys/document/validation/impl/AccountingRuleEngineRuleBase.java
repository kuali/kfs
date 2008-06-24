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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.Map;

import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.bo.Note;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rule.event.BlanketApproveDocumentEvent;
import org.kuali.core.rules.DocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.document.validation.AddCheckRule;
import org.kuali.kfs.fp.document.validation.DeleteCheckRule;
import org.kuali.kfs.fp.document.validation.UpdateCheckRule;
import org.kuali.kfs.fp.document.validation.event.AttributedAddCheckEvent;
import org.kuali.kfs.fp.document.validation.event.AttributedDeleteCheckEvent;
import org.kuali.kfs.fp.document.validation.event.AttributedUpdateCheckEvent;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.validation.AddPurchasingAccountsPayableItemRule;
import org.kuali.kfs.module.purap.document.validation.event.AddAttributedPurchasingAccountsPayableItemEvent;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.datadictionary.KFSTransactionalDocumentEntry;
import org.kuali.kfs.sys.document.validation.AccountingRuleEngineRule;
import org.kuali.kfs.sys.document.validation.AddAccountingLineRule;
import org.kuali.kfs.sys.document.validation.DeleteAccountingLineRule;
import org.kuali.kfs.sys.document.validation.ReviewAccountingLineRule;
import org.kuali.kfs.sys.document.validation.UpdateAccountingLineRule;
import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AttributedAddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedAddAdHocRoutePersonEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedAddAdHocRouteWorkgroupEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedAddNoteEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedApproveDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedBlanketApproveDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDeleteAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedReviewAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedRouteDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedSaveDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedUpdateAccountingLineEvent;

/**
 * A rule that uses the accounting rule engine to perform rule validations.
 */
public class AccountingRuleEngineRuleBase extends DocumentRuleBase implements AccountingRuleEngineRule, AddAccountingLineRule, DeleteAccountingLineRule, UpdateAccountingLineRule, ReviewAccountingLineRule, AddPurchasingAccountsPayableItemRule, AddCheckRule, DeleteCheckRule, UpdateCheckRule {

    /**
     * @see org.kuali.kfs.sys.document.validation.AccountingRuleEngineRule#validateForEvent(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    public boolean validateForEvent(AttributedDocumentEvent event) {
        KFSTransactionalDocumentEntry documentEntry = getDataDictionaryEntryForDocument((TransactionalDocument)event.getDocument());
        Map<Class, String> validationMap = documentEntry.getValidationMap();
        
        if (validationMap == null || !validationMap.containsKey(event.getClass())) {
            return true; // no validation?  just return true
        } else {
            String beanName = validationMap.get(event.getClass());
            Map<String, Validation> validationBeans = SpringContext.getBeansOfType(Validation.class);
            return validationBeans.get(beanName).stageValidation(event);
        }
    }
    
    /**
     * Returns the validation from the data dictionary for the document in the event
     * @param document the document to look up a data dictionary entry for
     * @return a document entry
     */
    protected KFSTransactionalDocumentEntry getDataDictionaryEntryForDocument(TransactionalDocument document) {
        return (KFSTransactionalDocumentEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDictionaryObjectEntry(document.getClass().getName());
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomAddAdHocRoutePersonBusinessRules(org.kuali.core.document.Document, org.kuali.core.bo.AdHocRoutePerson)
     */
    @Override
    protected boolean processCustomAddAdHocRoutePersonBusinessRules(Document document, AdHocRoutePerson person) {
        boolean result = super.processCustomAddAdHocRoutePersonBusinessRules(document, person);
        
        result &= validateForEvent(new AttributedAddAdHocRoutePersonEvent(document, person));
        
        return result;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomAddAdHocRouteWorkgroupBusinessRules(org.kuali.core.document.Document, org.kuali.core.bo.AdHocRouteWorkgroup)
     */
    @Override
    protected boolean processCustomAddAdHocRouteWorkgroupBusinessRules(Document document, AdHocRouteWorkgroup workgroup) {
        boolean result = super.processCustomAddAdHocRouteWorkgroupBusinessRules(document, workgroup);
        
        result &= validateForEvent(new AttributedAddAdHocRouteWorkgroupEvent(document, workgroup));
        
        return result;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomAddNoteBusinessRules(org.kuali.core.document.Document, org.kuali.core.bo.Note)
     */
    @Override
    protected boolean processCustomAddNoteBusinessRules(Document document, Note note) {
        boolean result = super.processCustomAddNoteBusinessRules(document, note);
        
        result &= validateForEvent(new AttributedAddNoteEvent(document, note));
        
        return result;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean result = super.processCustomApproveDocumentBusinessRules(approveEvent);
        
        if (approveEvent instanceof BlanketApproveDocumentEvent) {
            result &= validateForEvent(new AttributedBlanketApproveDocumentEvent(approveEvent.getErrorPathPrefix(), approveEvent.getDocument()));
        } else {
            result &= validateForEvent(new AttributedApproveDocumentEvent(approveEvent.getErrorPathPrefix(), approveEvent.getDocument()));
        }
        
        return result;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);
        
        result &= validateForEvent(new AttributedRouteDocumentEvent(document));
        
        return result;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean result = super.processCustomSaveDocumentBusinessRules(document);
        
        result &= validateForEvent(new AttributedSaveDocumentEvent(document));
        
        return result;
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.AddAccountingLineRule#processAddAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean processAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        return validateForEvent(new AttributedAddAccountingLineEvent("", financialDocument, accountingLine));
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.UpdateAccountingLineRule#processUpdateAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean processUpdateAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return validateForEvent(new AttributedUpdateAccountingLineEvent("", financialDocument, originalAccountingLine, updatedAccountingLine));
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.AddPurchasingAccountsPayableItemRule#processAddItemBusinessRules(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    public boolean processAddItemBusinessRules(AccountingDocument financialDocument, PurApItem item) {
        return validateForEvent(new AddAttributedPurchasingAccountsPayableItemEvent("", financialDocument, item));
    }

    /**
     * While this method yet lives...so should it never get called
     * @see org.kuali.kfs.sys.document.validation.AccountingLineRule#isAmountValid(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        return true;
    }

    /**
     * While this method yet lives...so should it never get called
     * @see org.kuali.kfs.sys.document.validation.AccountingLineRule#isFundGroupAllowed(java.lang.Class, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean isFundGroupAllowed(Class documentClass, AccountingLine accountingLine) {
        return true;
    }

    /**
     * While this method yet lives...so should it never get called
     * @see org.kuali.kfs.sys.document.validation.AccountingLineRule#isObjectCodeAllowed(java.lang.Class, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean isObjectCodeAllowed(Class documentClass, AccountingLine accountingLine) {
        return true;
    }

    /**
     * While this method yet lives...so should it never get called
     * @see org.kuali.kfs.sys.document.validation.AccountingLineRule#isObjectConsolidationAllowed(java.lang.Class, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean isObjectConsolidationAllowed(Class documentClass, AccountingLine accountingLine) {
        return true;
    }

    /**
     * While this method yet lives...so should it never get called
     * @see org.kuali.kfs.sys.document.validation.AccountingLineRule#isObjectLevelAllowed(java.lang.Class, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean isObjectLevelAllowed(Class documentClass, AccountingLine accountingLine) {
        return true;
    }

    /**
     * While this method yet lives...so should it never get called
     * @see org.kuali.kfs.sys.document.validation.AccountingLineRule#isObjectSubTypeAllowed(java.lang.Class, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean isObjectSubTypeAllowed(Class documentClass, AccountingLine accountingLine) {
        return true;
    }

    /**
     * While this method yet lives...so should it never get called
     * @see org.kuali.kfs.sys.document.validation.AccountingLineRule#isObjectTypeAllowed(java.lang.Class, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean isObjectTypeAllowed(Class documentClass, AccountingLine accountingLine) {
        return true;
    }

    /**
     * While this method yet lives...so should it never get called
     * @see org.kuali.kfs.sys.document.validation.AccountingLineRule#isSubFundGroupAllowed(java.lang.Class, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean isSubFundGroupAllowed(Class documentClass, AccountingLine accountingLine) {
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.DeleteAccountingLineRule#processDeleteAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean)
     */
    public boolean processDeleteAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine, boolean lineWasAlreadyDeletedFromDocument) {
        return validateForEvent(new AttributedDeleteAccountingLineEvent("", financialDocument, accountingLine, lineWasAlreadyDeletedFromDocument));
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.ReviewAccountingLineRule#processReviewAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean processReviewAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        return validateForEvent(new AttributedReviewAccountingLineEvent("", financialDocument, accountingLine));
    }

    /**
     * @see org.kuali.kfs.fp.document.validation.AddCheckRule#processAddCheckBusinessRules(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.fp.businessobject.Check)
     */
    public boolean processAddCheckBusinessRules(AccountingDocument financialDocument, Check check) {
        return validateForEvent(new AttributedAddCheckEvent("", "", financialDocument, check));
    }

    /**
     * @see org.kuali.kfs.fp.document.validation.DeleteCheckRule#processDeleteCheckBusinessRules(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.fp.businessobject.Check)
     */
    public boolean processDeleteCheckBusinessRules(AccountingDocument financialDocument, Check check) {
        return validateForEvent(new AttributedDeleteCheckEvent("", "", financialDocument, check));
    }

    /**
     * @see org.kuali.kfs.fp.document.validation.UpdateCheckRule#processUpdateCheckRule(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.fp.businessobject.Check)
     */
    public boolean processUpdateCheckRule(AccountingDocument financialDocument, Check check) {
        return validateForEvent(new AttributedUpdateCheckEvent("", "", financialDocument, check));
    }
}
