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
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.dao.BudgetConstructionDao;
import org.kuali.module.budget.service.BudgetDocumentService;
import org.kuali.rice.config.ConfigurationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.kuali.core.workflow.service.WorkflowDocumentService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class implements the BudgetDocumentService interface Methods here operate on objects associated with the Budget Construction
 * document such as BudgetConstructionHeader
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

    public Document saveDocument(Document document) throws WorkflowException, ValidationException {
        return saveDocument(document, SaveDocumentEvent.class);
    }

    public Document saveDocument(Document document, Class kualiDocumentEventClass) throws WorkflowException, ValidationException {

        checkForNulls(document);
        if (kualiDocumentEventClass == null) {
            throw new IllegalArgumentException("invalid (null) kualiDocumentEventClass");
        }

        // if event is not an instance of a SaveDocumentEvent or a SaveOnlyDocumentEvent
        if (!SaveEvent.class.isAssignableFrom(kualiDocumentEventClass)) {
            throw new ConfigurationException("The KualiDocumentEvent class '" + kualiDocumentEventClass.getName() + "' does not implement the class '" + SaveEvent.class.getName() + "'");
        }

        document.prepareForSave();

        // validateAndPersistDocumentAndSaveAdHocRoutingRecipients(document, generateKualiDocumentEvent(document,
        // kualiDocumentEventClass));
        validateAndPersistDocument(document, generateKualiDocumentEvent(document, kualiDocumentEventClass));

        documentService.prepareWorkflowDocument(document);
        workflowDocumentService.save(document.getDocumentHeader().getWorkflowDocument(), null);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());

        return document;
    }

    protected void checkForNulls(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        else if (document.getDocumentNumber() == null) {
            throw new IllegalStateException("invalid (null) documentHeaderId");
        }
    }

    public void validateAndPersistDocument(Document document, KualiDocumentEvent event) throws WorkflowException, ValidationException {
        if (document == null) {
            LOG.error("document passed to validateAndPersist was null");
            throw new IllegalArgumentException("invalid (null) document");
        }
        LOG.info("validating and preparing to persist document " + document.getDocumentNumber());
        
        document.validateBusinessRules(event);
        document.prepareForSave(event);

        // save the document
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

    private KualiDocumentEvent generateKualiDocumentEvent(Document document, Class eventClass) throws ConfigurationException {
        String potentialErrorMessage = "Found error trying to generate Kuali Document Event using event class '" + eventClass.getName() + "' for document " + document.getDocumentNumber();
        try {
            Constructor usableConstructor = null;
            List<Object> paramList = null;
            for (Constructor currentConstructor : eventClass.getConstructors()) {
                paramList = new ArrayList<Object>();
                for (Class parameterClass : currentConstructor.getParameterTypes()) {
                    if (Document.class.isAssignableFrom(parameterClass)) {
                        usableConstructor = currentConstructor;
                        paramList.add(document);
                    }
                    else {
                        paramList.add(null);
                    }
                }
                if (ObjectUtils.isNotNull(usableConstructor)) {
                    break;
                }
            }
            if (ObjectUtils.isNull(usableConstructor)) {
                throw new RuntimeException("Cannot find a constructor for class '" + eventClass.getName() + "' that takes in a document parameter");
            }
            else {
                usableConstructor.newInstance(paramList.toArray());
                return (KualiDocumentEvent) usableConstructor.newInstance(paramList.toArray());
            }
        }
        catch (SecurityException e) {
            throw new ConfigurationException(potentialErrorMessage, e);
        }
        catch (IllegalArgumentException e) {
            throw new ConfigurationException(potentialErrorMessage, e);
        }
        catch (InstantiationException e) {
            throw new ConfigurationException(potentialErrorMessage, e);
        }
        catch (IllegalAccessException e) {
            throw new ConfigurationException(potentialErrorMessage, e);
        }
        catch (InvocationTargetException e) {
            throw new ConfigurationException(potentialErrorMessage, e);
        }
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
