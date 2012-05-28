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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.Map;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.kfs.sys.document.validation.AccountingRuleEngineRule;
import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AttributedAddAdHocRoutePersonEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedAddAdHocRouteWorkgroupEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedAddNoteEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedApproveDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedBlanketApproveDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedRouteDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedSaveDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.rules.DocumentRuleBase;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.BlanketApproveDocumentEvent;

/**
 * A rule that uses the accounting rule engine to perform rule validations.
 */
public class AccountingRuleEngineRuleBase extends DocumentRuleBase implements AccountingRuleEngineRule {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingRuleEngineRuleBase.class);
    
    /**
     * Constructs a AccountingRuleEngineRuleBase.java.
     */
    public AccountingRuleEngineRuleBase() {
        super();
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.AccountingRuleEngineRule#validateForEvent(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    public boolean validateForEvent(AttributedDocumentEvent event) {
        final FinancialSystemTransactionalDocumentEntry documentEntry = getDataDictionaryEntryForDocument((TransactionalDocument)event.getDocument());
        final Map<Class, String> validationMap = documentEntry.getValidationMap();
        
        if (validationMap == null || !validationMap.containsKey(event.getClass())) {
            return true; // no validation?  just return true
        } else {
            final String beanName = validationMap.get(event.getClass());
            Validation validationBean = SpringContext.getBean(Validation.class, beanName);
            
            final boolean valid = validationBean.stageValidation(event);            
            return valid;
        }
    }
    
    /**
     * Returns the validation from the data dictionary for the document in the event
     * @param document the document to look up a data dictionary entry for
     * @return a document entry
     */
    protected FinancialSystemTransactionalDocumentEntry getDataDictionaryEntryForDocument(TransactionalDocument document) {
        return (FinancialSystemTransactionalDocumentEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDictionaryObjectEntry(document.getClass().getName());
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomAddAdHocRoutePersonBusinessRules(org.kuali.rice.krad.document.Document, org.kuali.rice.krad.bo.AdHocRoutePerson)
     */
    @Override
    protected boolean processCustomAddAdHocRoutePersonBusinessRules(Document document, AdHocRoutePerson person) {
        boolean result = super.processCustomAddAdHocRoutePersonBusinessRules(document, person);
        
        result &= validateForEvent(new AttributedAddAdHocRoutePersonEvent(document, person));
        
        return result;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomAddAdHocRouteWorkgroupBusinessRules(org.kuali.rice.krad.document.Document, org.kuali.rice.krad.bo.AdHocRouteWorkgroup)
     */
    @Override
    protected boolean processCustomAddAdHocRouteWorkgroupBusinessRules(Document document, AdHocRouteWorkgroup workgroup) {
        boolean result = super.processCustomAddAdHocRouteWorkgroupBusinessRules(document, workgroup);
        
        result &= validateForEvent(new AttributedAddAdHocRouteWorkgroupEvent(document, workgroup));
        
        return result;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomAddNoteBusinessRules(org.kuali.rice.krad.document.Document, org.kuali.rice.krad.bo.Note)
     */
    @Override
    protected boolean processCustomAddNoteBusinessRules(Document document, Note note) {
        boolean result = super.processCustomAddNoteBusinessRules(document, note);
        
        result &= validateForEvent(new AttributedAddNoteEvent(document, note));
        
        return result;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.krad.rule.event.ApproveDocumentEvent)
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
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);

        AttributedRouteDocumentEvent event = new AttributedRouteDocumentEvent(document);        
        result &= validateForEvent(event);
        
        return result;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean result = super.processCustomSaveDocumentBusinessRules(document);
        
        result &= validateForEvent(new AttributedSaveDocumentEvent(document));
        
        return result;
    }
    
    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#isDocumentAttributesValid(org.kuali.rice.krad.document.Document, boolean)
     */
    @Override
    public boolean isDocumentAttributesValid(Document document, boolean validateRequired) {
        FinancialSystemTransactionalDocumentEntry documentEntry = getDataDictionaryEntryForDocument((TransactionalDocument)document);
        Integer maxDictionaryValidationDepth = documentEntry.getMaxDictionaryValidationDepth();
        
        if(maxDictionaryValidationDepth != null) {
            this.setMaxDictionaryValidationDepth(maxDictionaryValidationDepth);
        }
        
        //refresh the document's reference objects..
        document.refreshNonUpdateableReferences();
        
        //refresh GLPE nonupdateable business object references....
        if (document instanceof GeneralLedgerPostingDocumentBase) {
            GeneralLedgerPostingDocumentBase glpeDocument = (GeneralLedgerPostingDocumentBase) document;
            for (GeneralLedgerPendingEntry glpe : glpeDocument.getGeneralLedgerPendingEntries()) {
                glpe.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
            }
        }
        
        return super.isDocumentAttributesValid(document, validateRequired);
    }
}
