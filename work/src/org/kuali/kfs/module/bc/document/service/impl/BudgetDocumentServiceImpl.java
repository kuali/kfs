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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.kuali.core.dao.DocumentDao;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.SaveDocumentEvent;
import org.kuali.core.rule.event.SaveEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.BCParameterKeyConstants;
import org.kuali.module.budget.BCPropertyConstants;
import org.kuali.module.budget.BCConstants.AccountSalarySettingOnlyCause;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.dao.BudgetConstructionDao;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.service.BenefitsCalculationService;
import org.kuali.module.budget.service.BudgetDocumentService;
import org.kuali.module.budget.service.BudgetParameterService;
import org.kuali.module.integration.bo.LaborLedgerBenefitsCalculation;
import org.kuali.module.integration.service.LaborModuleService;
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
    private BenefitsCalculationService benefitsCalculationService;
    private BusinessObjectService businessObjectService;
    private LaborModuleService laborModuleService;
    private ParameterService parameterService;
    private BudgetParameterService budgetParameterService;

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
    public Document saveDocument(BudgetConstructionDocument budgetConstructionDocument) throws WorkflowException, ValidationException {
        
        this.saveDocumentNoWorkflow(budgetConstructionDocument);

        // this seems to be the workflow related stuff only needed during a final save from save button or close
        // and user clicks yes when prompted to save or not.
        documentService.prepareWorkflowDocument(budgetConstructionDocument);
        workflowDocumentService.save(budgetConstructionDocument.getDocumentHeader().getWorkflowDocument(), null);
        GlobalVariables.getUserSession().setWorkflowDocument(budgetConstructionDocument.getDocumentHeader().getWorkflowDocument());
        
        // save any messages up to this point and put them back in after logDocumentAction()
        // this is a hack to get around the problem where messageLists gets cleared
        // that is PostProcessorServiceImpl.doActionTaken(ActionTakenEventVO), establishGlobalVariables(), which does GlobalVariables.clear()
        // not sure why this doesn't trash the GlobalVariables.getErrorMap()
        ArrayList messagesSoFar = GlobalVariables.getMessageList();

        budgetConstructionDocument.getDocumentHeader().getWorkflowDocument().logDocumentAction("Document Updated");
        
        // putting messages back in
        GlobalVariables.getMessageList().addAll(messagesSoFar);
        
        return budgetConstructionDocument;
    }
    
    /**
     * @see org.kuali.module.budget.service.BudgetDocumentService#saveDocumentNoWorkflow(org.kuali.core.document.Document)
     * 
     * TODO use this for saves before calc benefits service, monthly spread service, salary setting, monthly calls
     * add to interface
     * this should leave out any calls to workflow related methods maybe call this from saveDocument(doc, eventclass) above
     * instead of duplicating all the calls up to the point of workflow related calls
     */
    public Document saveDocumentNoWorkflow(BudgetConstructionDocument bcDoc) throws ValidationException {
        
        checkForNulls(bcDoc);

        bcDoc.prepareForSave();

        // validate and save the local objects not workflow objects
        // this eventually calls BudgetConstructionRules.processSaveDocument() which overrides the method in DocumentRuleBase
        validateAndPersistDocument(bcDoc, new SaveDocumentEvent(bcDoc));
        
        // TODO move this to its own method and call from the BudgetConstructionAction actions that need it, save(if flagged), calcbene(regardless of flag)
        // calc benefits if needed
        
//        if (bcDoc.isBenefitsCalcNeeded() || bcDoc.isMonthlyBenefitsCalcNeeded()){
//
//            // allow benefits calculation if document's account is not salary setting only lines
//            AccountSalarySettingOnlyCause retVal = budgetParameterService.isSalarySettingOnlyAccount(bcDoc);
//            if (retVal == AccountSalarySettingOnlyCause.MISSING_PARAM || retVal == AccountSalarySettingOnlyCause.NONE){
//                
//                if (bcDoc.isBenefitsCalcNeeded()){
//                    bcDoc.setBenefitsCalcNeeded(false);
//
//                    // pbgl lines are saved at this point, calc benefits
//                    benefitsCalculationService.calculateAnnualBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber());
//                    
//                    // gets the current set of fringe lines from the DB and adds/updates lines in the doc as apropos
//                    this.reloadBenefitsLines(bcDoc);
//                    
//                    // write global message on calc success
//                    GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BENEFITS_CALCULATED);
//                }
//                
//                if (bcDoc.isMonthlyBenefitsCalcNeeded()){
//                    bcDoc.setMonthlyBenefitsCalcNeeded(false);
//
//                    // pbgl lines are saved at this point, calc benefits
//                    benefitsCalculationService.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber());
//                    
//                    // write global message on calc success
//                    GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BENEFITS_MONTHLY_CALCULATED);
//                }
//
//            }
//        }
        return bcDoc;
    }

    public void calculateBenefitsIfNeeded (BudgetConstructionDocument bcDoc){
        
        if (bcDoc.isBenefitsCalcNeeded() || bcDoc.isMonthlyBenefitsCalcNeeded()){

            // allow benefits calculation if document's account is not salary setting only lines
            AccountSalarySettingOnlyCause retVal = budgetParameterService.isSalarySettingOnlyAccount(bcDoc);
            if (retVal == AccountSalarySettingOnlyCause.MISSING_PARAM || retVal == AccountSalarySettingOnlyCause.NONE){
                
                if (bcDoc.isBenefitsCalcNeeded()){
                    this.calculateAnnualBenefits(bcDoc);
//                    bcDoc.setBenefitsCalcNeeded(false);
//
//                    // pbgl lines are saved at this point, calc benefits
//                    benefitsCalculationService.calculateAnnualBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber());
//                    
//                    // gets the current set of fringe lines from the DB and adds/updates lines in the doc as apropos
//                    this.reloadBenefitsLines(bcDoc);
//                    
//                    // write global message on calc success
//                    GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BENEFITS_CALCULATED);
                }
                
                if (bcDoc.isMonthlyBenefitsCalcNeeded()){
                    this.calculateMonthlyBenefits(bcDoc);
//                    bcDoc.setMonthlyBenefitsCalcNeeded(false);
//
//                    // pbgl lines are saved at this point, calc benefits
//                    benefitsCalculationService.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber());
//                    
//                    // write global message on calc success
//                    GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BENEFITS_MONTHLY_CALCULATED);
                }

            }
        }
    }

    public void calculateBenefits(BudgetConstructionDocument bcDoc){

        this.calculateAnnualBenefits(bcDoc);
        this.calculateMonthlyBenefits(bcDoc);
    }
    
    public void calculateAnnualBenefits(BudgetConstructionDocument bcDoc){
        
        bcDoc.setBenefitsCalcNeeded(false);

        // pbgl lines are saved at this point, calc benefits
        benefitsCalculationService.calculateAnnualBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber());
        
        // gets the current set of fringe lines from the DB and adds/updates lines in the doc as apropos
        this.reloadBenefitsLines(bcDoc);
        
        // write global message on calc success
        GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BENEFITS_CALCULATED);
    }

    public void calculateMonthlyBenefits(BudgetConstructionDocument bcDoc){
        
        bcDoc.setMonthlyBenefitsCalcNeeded(false);

        // pbgl lines are saved at this point, calc benefits
        benefitsCalculationService.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber());
        
        // write global message on calc success
        GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BENEFITS_MONTHLY_CALCULATED);
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
    public void validateAndPersistDocument(Document document, KualiDocumentEvent event) throws ValidationException {
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
     * Reloads benefits target accounting lines. Usually called right after an annual benefits calculation
     * and the display needs updated with a fresh copy from the database.  All old row versions are removed
     * and database row versions are inserted in the list in the correct order.
     * 
     * @param bcDoc
     */
    private void reloadBenefitsLines(BudgetConstructionDocument bcDoc){
        
        // get list of potential fringe objects to use as an in query param
        Map fieldValues = new HashMap();
        fieldValues.put("universityFiscalYear", bcDoc.getUniversityFiscalYear());
        fieldValues.put("chartOfAccountsCode", bcDoc.getChartOfAccountsCode());
        List<String> fringeObjects = new ArrayList();
        List benefitsCalculation = (List<LaborLedgerBenefitsCalculation>) businessObjectService.findMatching(laborModuleService.getLaborLedgerBenefitsCalculationClass(), fieldValues);
        for (Iterator iter = benefitsCalculation.iterator(); iter.hasNext();) {
            LaborLedgerBenefitsCalculation element = (LaborLedgerBenefitsCalculation) iter.next();
            fringeObjects.add(element.getPositionFringeBenefitObjectCode());
        }
        List<PendingBudgetConstructionGeneralLedger> dbPBGLFringeLines = budgetConstructionDao.getDocumentPBGLFringeLines(bcDoc.getDocumentNumber(),fringeObjects);
        List<PendingBudgetConstructionGeneralLedger> docPBGLExpLines = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines();

        // holds the request sums of removed, added records and used to adjust the document expenditure request total
        KualiInteger docRequestTotals = KualiInteger.ZERO;
        KualiInteger dbRequestTotals = KualiInteger.ZERO;
        
        //remove the current set of fringe lines
        ListIterator docLines = docPBGLExpLines.listIterator();
        while (docLines.hasNext()){
            PendingBudgetConstructionGeneralLedger docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
            if (fringeObjects.contains(docLine.getFinancialObjectCode())){
                docRequestTotals = docRequestTotals.add(docLine.getAccountLineAnnualBalanceAmount());
                docLines.remove();
            }
        }
        
        //add the dbset of fringe lines, if any
        if (dbPBGLFringeLines != null && !dbPBGLFringeLines.isEmpty()){

            if (docPBGLExpLines == null || docPBGLExpLines.isEmpty()){
                docPBGLExpLines.addAll(dbPBGLFringeLines);
            } else {
                ListIterator dbLines = dbPBGLFringeLines.listIterator();
                docLines = docPBGLExpLines.listIterator();
                PendingBudgetConstructionGeneralLedger dbLine = (PendingBudgetConstructionGeneralLedger) dbLines.next();
                PendingBudgetConstructionGeneralLedger docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
                boolean dbDone = false;
                boolean docDone = false;
                while (!dbDone){
                    if (docDone || docLine.getFinancialObjectCode().compareToIgnoreCase(dbLine.getFinancialObjectCode()) > 0){
                        if (!docDone){
                            docLine = (PendingBudgetConstructionGeneralLedger) docLines.previous();
                        }
                        dbRequestTotals = dbRequestTotals.add(dbLine.getAccountLineAnnualBalanceAmount());
                        dbLine.setPersistedAccountLineAnnualBalanceAmount(dbLine.getAccountLineAnnualBalanceAmount());
                        docLines.add(dbLine);
                        if (!docDone){
                            docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
                        }
                        if (dbLines.hasNext()){
                            dbLine = (PendingBudgetConstructionGeneralLedger) dbLines.next();
                        } else {
                            dbDone = true;
                        }
                    } else {
                        if (docLines.hasNext()){
                            docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
                        } else {
                            docDone = true;
                        }
                    }
                }
            }
        }
        
        // adjust the request total for the removed and added recs
        bcDoc.setExpenditureAccountLineAnnualBalanceAmountTotal(bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal().add(dbRequestTotals.subtract(docRequestTotals)));
        
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


    /**
     * Sets the benefitsCalculationService attribute value.
     * @param benefitsCalculationService The benefitsCalculationService to set.
     */
    public void setBenefitsCalculationService(BenefitsCalculationService benefitsCalculationService) {
        this.benefitsCalculationService = benefitsCalculationService;
    }


    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * Sets the laborModuleService attribute value.
     * @param laborModuleService The laborModuleService to set.
     */
    public void setLaborModuleService(LaborModuleService laborModuleService) {
        this.laborModuleService = laborModuleService;
    }


    /**
     * Sets the budgetParameterService attribute value.
     * @param budgetParameterService The budgetParameterService to set.
     */
    public void setBudgetParameterService(BudgetParameterService budgetParameterService) {
        this.budgetParameterService = budgetParameterService;
    }


    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
