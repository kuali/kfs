/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.service.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.kuali.core.dao.DocumentDao;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.SaveDocumentEvent;
import org.kuali.core.rule.event.SaveEvent;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.dao.BudgetConstructionDao;
import org.kuali.module.budget.service.BudgetDocumentService;
import org.kuali.rice.config.ConfigurationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.kuali.core.workflow.service.WorkflowDocumentService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Implements the BudgetDocumentService interface.
 * Methods here operate on objects associated with the Budget Construction document such as BudgetConstructionHeader
 * 
 */
@Transactional
public class BudgetDocumentServiceImpl implements BudgetDocumentService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetDocumentServiceImpl.class);
    private BudgetConstructionDao budgetConstructionDao;
    private DocumentDao documentDao;
    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;

    /**
     * @see org.kuali.module.budget.service.BudgetDocumentService#getByCandidateKey(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer)
     */
    public BudgetConstructionHeader getByCandidateKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear) {
        return budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
    }


    /**
     * @see org.kuali.module.budget.service.BudgetDocumentService#saveDocument(org.kuali.core.document.Document)
     * 
     * similar to DocumentService.saveDocument()
     */
    public Document saveDocument(Document document) throws WorkflowException, ValidationException {

        checkForNulls(document);

// TODO cleanup commented code in this method when verified it's not needed
//        if (kualiDocumentEventClass == null) {
//            throw new IllegalArgumentException("invalid (null) kualiDocumentEventClass");
//        }
//
//        // if event is not an instance of a SaveDocumentEvent or a SaveOnlyDocumentEvent
//        if (!SaveEvent.class.isAssignableFrom(kualiDocumentEventClass)) {
//            throw new ConfigurationException("The KualiDocumentEvent class '" + kualiDocumentEventClass.getName() + "' does not implement the class '" + SaveEvent.class.getName() + "'");
//        }

        document.prepareForSave();

        // validateAndPersistDocumentAndSaveAdHocRoutingRecipients(document, generateKualiDocumentEvent(document, kualiDocumentEventClass));

        // validates and saves the local objects not workflow objects
        // this eventually calls BudgetConstructionRules.processSaveDocument() which overrides the method in DocumentRuleBase
        validateAndPersistDocument(document, new SaveDocumentEvent(document));

        // this seems to be the workflow related stuff only needed during a final save from save button or close
        // and user clicks yes when prompted to save or not.
        documentService.prepareWorkflowDocument(document);
        workflowDocumentService.save(document.getDocumentHeader().getWorkflowDocument(), null);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        
        return document;
    }
    
    /**
     * @see org.kuali.module.budget.service.BudgetDocumentService#saveDocumentNoWorkflow(org.kuali.core.document.Document)
     * 
     * TODO use this for saves before calc benefits service, monthly spread service, salary setting, monthly calls
     * add to interface
     * this should leave out any calls to workflow related methods maybe call this from saveDocument(doc, eventclass) above
     * instead of duplicating all the calls up to the point of workflow related calls
     */
    public Document saveDocumentNoWorkflow(Document document) throws ValidationException {
        
        return document;
    }

    /**
     * Does sanity checks for null document object and null documentNumber
     * 
     * @param document
     */
    protected void checkForNulls(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        else if (document.getDocumentNumber() == null) {
            throw new IllegalStateException("invalid (null) documentHeaderId");
        }
    }

    /**
     * Runs validation and persists a document to the database.
     * 
     * TODO This method is just like the one in DocumentServiceImpl. This method exists because the DocumentService Interface does not
     * define this method, so we can't call it.  Not sure if this is an oversite or by design.  If the interface gets adjusted, fix to
     * call it, otherwise leave this and remove the marker.
     *   
     * @param document
     * @param event
     * @throws WorkflowException
     * @throws ValidationException
     */
    public void validateAndPersistDocument(Document document, KualiDocumentEvent event) throws WorkflowException, ValidationException {
        if (document == null) {
            LOG.error("document passed to validateAndPersist was null");
            throw new IllegalArgumentException("invalid (null) document");
        }
        LOG.info("validating and preparing to persist document " + document.getDocumentNumber());
        
        // runs business rules event.validate() and creates rule instance and runs rule method recursively
        document.validateBusinessRules(event);
        
        // calls overriden method for specific document for anything that needs to happen before the save
        // currently nothing for BC document
        document.prepareForSave(event);

        // save the document to the database
        try {
            LOG.info("storing document " + document.getDocumentNumber());
            documentDao.save(document);
        }
        catch (OptimisticLockingFailureException e) {
            LOG.error("exception encountered on store of document " + e.getMessage());
            throw e;
        }
        
        document.postProcessSave(event);


    }

    /**
     * @see org.kuali.module.budget.service.BudgetDocumentService#getPendingBudgetConstructionAppointmentFundingRequestSum(org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger)
     */
    public KualiInteger getPendingBudgetConstructionAppointmentFundingRequestSum(PendingBudgetConstructionGeneralLedger salaryDetailLine) {
        return budgetConstructionDao.getPendingBudgetConstructionAppointmentFundingRequestSum(salaryDetailLine);
    }


    /**
     * Sets the budgetConstructionDao attribute value.
     * 
     * @param budgetConstructionDao The budgetConstructionDao to set.
     */
    public void setBudgetConstructionDao(BudgetConstructionDao budgetConstructionDao) {
        this.budgetConstructionDao = budgetConstructionDao;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the workflowDocumentService attribute value.
     * 
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * Sets the documentDao attribute value.
     * @param documentDao The documentDao to set.
     */
    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

}
