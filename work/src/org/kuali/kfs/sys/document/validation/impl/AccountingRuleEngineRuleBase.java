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

import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.document.validation.event.AddCheckEvent;
import org.kuali.kfs.fp.document.validation.event.DeleteCheckEvent;
import org.kuali.kfs.fp.document.validation.event.UpdateCheckEvent;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
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
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteWorkgroup;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.rule.event.ApproveDocumentEvent;
import org.kuali.rice.kns.rule.event.BlanketApproveDocumentEvent;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;

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
     * @see org.kuali.kfs.sys.document.validation.AccountingRuleEngineRule#validateForEvent(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    public boolean validateForEvent(AttributedDocumentEvent event) {
        FinancialSystemTransactionalDocumentEntry documentEntry = getDataDictionaryEntryForDocument((TransactionalDocument)event.getDocument());
        Map<Class, String> validationMap = documentEntry.getValidationMap();
        
        if (validationMap == null || !validationMap.containsKey(event.getClass())) {
            return true; // no validation?  just return true
        } else {
            String beanName = validationMap.get(event.getClass());
            Map<String, Validation> validationBeans = SpringContext.getBeansOfType(Validation.class);
            
            boolean isvalid = validationBeans.get(beanName).stageValidation(event);            
            return isvalid;
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
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomAddAdHocRoutePersonBusinessRules(org.kuali.rice.kns.document.Document, org.kuali.rice.kns.bo.AdHocRoutePerson)
     */
    @Override
    protected boolean processCustomAddAdHocRoutePersonBusinessRules(Document document, AdHocRoutePerson person) {
        boolean result = super.processCustomAddAdHocRoutePersonBusinessRules(document, person);
        
        result &= validateForEvent(new AttributedAddAdHocRoutePersonEvent(document, person));
        
        return result;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomAddAdHocRouteWorkgroupBusinessRules(org.kuali.rice.kns.document.Document, org.kuali.rice.kns.bo.AdHocRouteWorkgroup)
     */
    @Override
    protected boolean processCustomAddAdHocRouteWorkgroupBusinessRules(Document document, AdHocRouteWorkgroup workgroup) {
        boolean result = super.processCustomAddAdHocRouteWorkgroupBusinessRules(document, workgroup);
        
        result &= validateForEvent(new AttributedAddAdHocRouteWorkgroupEvent(document, workgroup));
        
        return result;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomAddNoteBusinessRules(org.kuali.rice.kns.document.Document, org.kuali.rice.kns.bo.Note)
     */
    @Override
    protected boolean processCustomAddNoteBusinessRules(Document document, Note note) {
        boolean result = super.processCustomAddNoteBusinessRules(document, note);
        
        result &= validateForEvent(new AttributedAddNoteEvent(document, note));
        
        return result;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.rule.event.ApproveDocumentEvent)
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
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);

        AttributedRouteDocumentEvent event = new AttributedRouteDocumentEvent(document);        
        result &= validateForEvent(event);
        
        return result;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean result = super.processCustomSaveDocumentBusinessRules(document);
        
        result &= validateForEvent(new AttributedSaveDocumentEvent(document));
        
        return result;
    }
    
    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#isDocumentAttributesValid(org.kuali.rice.kns.document.Document, boolean)
     */
    @Override
    public boolean isDocumentAttributesValid(Document document, boolean validateRequired) {
        FinancialSystemTransactionalDocumentEntry documentEntry = getDataDictionaryEntryForDocument((TransactionalDocument)document);
        Integer maxDictionaryValidationDepth = documentEntry.getMaxDictionaryValidationDepth();
        
        if(maxDictionaryValidationDepth != null) {
            this.setMaxDictionaryValidationDepth(maxDictionaryValidationDepth);
        }
        
        return super.isDocumentAttributesValid(document, validateRequired);
    }
}
