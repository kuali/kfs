/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.integration.ld.LaborLedgerBenefitsCalculation;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.MonthSpreadDeleteType;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountReports;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao;
import org.kuali.kfs.module.bc.document.service.BenefitsCalculationService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.BudgetParameterService;
import org.kuali.kfs.module.bc.document.validation.event.DeleteMonthlySpreadEvent;
import org.kuali.kfs.module.bc.document.validation.impl.BudgetConstructionRuleUtil;
import org.kuali.kfs.module.bc.document.web.struts.BudgetConstructionForm;
import org.kuali.kfs.module.bc.document.web.struts.MonthlyBudgetForm;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the BudgetDocumentService interface. Methods here operate on objects associated with the Budget Construction document
 * such as BudgetConstructionHeader
 */
public class BudgetDocumentServiceImpl implements BudgetDocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetDocumentServiceImpl.class);

    private BudgetConstructionDao budgetConstructionDao;
    private DocumentDao documentDao;
    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;
    private BenefitsCalculationService benefitsCalculationService;
    private BusinessObjectService businessObjectService;
    private KualiModuleService kualiModuleService;
    private ParameterService parameterService;
    private BudgetParameterService budgetParameterService;
    private FiscalYearFunctionControlService fiscalYearFunctionControlService;
    private OptionsService optionsService;
    private PersistenceService persistenceService;
    private OrganizationService organizationService;
    private String defaultLaborBenefitRateCategoryCode;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getByCandidateKey(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer)
     */
    @Transactional
    public BudgetConstructionHeader getByCandidateKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear) {
        return budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#saveDocument(org.kuali.rice.krad.document.Document)
     *      similar to DocumentService.saveDocument()
     */
    @Transactional
    public Document saveDocument(BudgetConstructionDocument budgetConstructionDocument) throws WorkflowException, ValidationException {

        // user did explicit save here so mark as touched
        budgetConstructionDocument.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.ENROUTE);

        this.saveDocumentNoWorkflow(budgetConstructionDocument);

        SpringContext.getBean(SessionDocumentService.class).addDocumentToUserSession(GlobalVariables.getUserSession(), budgetConstructionDocument.getDocumentHeader().getWorkflowDocument());

        // save any messages up to this point and put them back in after logDocumentAction()
        // this is a hack to get around the problem where messageLists gets cleared
        // that is PostProcessorServiceImpl.doActionTaken(ActionTakenEventDTO), establishGlobalVariables(), which does
        // GlobalVariables.clear()
        // not sure why this doesn't trash the GlobalVariables.getMessageMap()
        MessageList messagesSoFar = KNSGlobalVariables.getMessageList();

        budgetConstructionDocument.getDocumentHeader().getWorkflowDocument().logAnnotation("Document Updated");

        // putting messages back in
        KNSGlobalVariables.getMessageList().addAll(messagesSoFar);

        return budgetConstructionDocument;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#saveDocumentNoWorkflow(org.kuali.rice.krad.document.Document)
     */
    @Transactional
    public Document saveDocumentNoWorkflow(BudgetConstructionDocument bcDoc) throws ValidationException {
        return this.saveDocumentNoWorkFlow(bcDoc, MonthSpreadDeleteType.NONE, true);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#saveDocumentNoWorkFlow(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.kfs.module.bc.BCConstants.MonthSpreadDeleteType, boolean)
     */
    @Transactional
    public Document saveDocumentNoWorkFlow(BudgetConstructionDocument bcDoc, MonthSpreadDeleteType monthSpreadDeleteType, boolean doMonthRICheck) throws ValidationException {

        checkForNulls(bcDoc);

        bcDoc.prepareForSave();

        // validate and save the local objects not workflow objects
        // this eventually calls BudgetConstructionRules.processSaveDocument() which overrides the method in DocumentRuleBase
        if (doMonthRICheck) {
            validateAndPersistDocument(bcDoc, new SaveDocumentEvent(bcDoc));
        }
        else {
            validateAndPersistDocument(bcDoc, new DeleteMonthlySpreadEvent(bcDoc, monthSpreadDeleteType));
        }
        return bcDoc;
    }

    @Transactional
    public void saveMonthlyBudget(MonthlyBudgetForm monthlyBudgetForm, BudgetConstructionMonthly budgetConstructionMonthly) {

        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) GlobalVariables.getUserSession().retrieveObject(monthlyBudgetForm.getReturnFormKey());
        BudgetConstructionDocument bcDoc = budgetConstructionForm.getBudgetConstructionDocument();

        // handle any override situation
        // getting here assumes that the line is not a salary detail line and that overrides are allowed
        KualiInteger changeAmount = KualiInteger.ZERO;
        KualiInteger monthTotalAmount = budgetConstructionMonthly.getFinancialDocumentMonthTotalLineAmount();
        KualiInteger pbglRequestAmount = budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger().getAccountLineAnnualBalanceAmount();
        if (!monthTotalAmount.equals(pbglRequestAmount)) {

            changeAmount = monthTotalAmount.subtract(pbglRequestAmount);

            // change the pbgl request amount store it and sync the object in session
            budgetConstructionMonthly.refreshReferenceObject("pendingBudgetConstructionGeneralLedger");

            PendingBudgetConstructionGeneralLedger sourceRow = (PendingBudgetConstructionGeneralLedger) businessObjectService.retrieve(budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger());
            sourceRow.setAccountLineAnnualBalanceAmount(monthTotalAmount);
            businessObjectService.save(sourceRow);

            this.addOrUpdatePBGLRow(bcDoc, sourceRow);
            bcDoc.setExpenditureAccountLineAnnualBalanceAmountTotal(bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal().add(changeAmount));

        }

        businessObjectService.save(budgetConstructionMonthly);
        this.callForBenefitsCalcIfNeeded(bcDoc, budgetConstructionMonthly, changeAmount);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#callForBenefitsCalcIfNeeded(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly, org.kuali.rice.core.api.util.type.KualiInteger)
     */
    @Transactional
    public void callForBenefitsCalcIfNeeded(BudgetConstructionDocument bcDoc, BudgetConstructionMonthly budgetConstructionMonthly, KualiInteger pbglChangeAmount) {

        if (!benefitsCalculationService.isBenefitsCalculationDisabled()) {
            if (budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger().getPositionObjectBenefit() != null && !budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger().getPositionObjectBenefit().isEmpty()) {

                bcDoc.setMonthlyBenefitsCalcNeeded(true);
                if (pbglChangeAmount.isNonZero()) {
                    bcDoc.setBenefitsCalcNeeded(true);
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#calculateBenefitsIfNeeded(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    public void calculateBenefitsIfNeeded(BudgetConstructionDocument bcDoc) {

        if (bcDoc.isBenefitsCalcNeeded() || bcDoc.isMonthlyBenefitsCalcNeeded()) {

            if (bcDoc.isBenefitsCalcNeeded()) {
                this.calculateAnnualBenefits(bcDoc);
            }

            if (bcDoc.isMonthlyBenefitsCalcNeeded()) {
                this.calculateMonthlyBenefits(bcDoc);
            }

            // reload from the DB and refresh refs
            this.reloadBenefitsLines(bcDoc);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#calculateBenefits(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    public void calculateBenefits(BudgetConstructionDocument bcDoc) {

        this.calculateAnnualBenefits(bcDoc);
        this.calculateMonthlyBenefits(bcDoc);

        // reload from the DB and refresh refs
        this.reloadBenefitsLines(bcDoc);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#calculateAnnualBenefits(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    protected void calculateAnnualBenefits(BudgetConstructionDocument bcDoc) {

        // allow benefits calculation if document's account is not salary setting only lines
        bcDoc.setBenefitsCalcNeeded(false);
        if (!bcDoc.isSalarySettingOnly()) {
            
            String sysParam = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND");
            LOG.debug("sysParam: " + sysParam);
            // if sysParam == Y then Labor Benefit Rate Category Code must be used
            if (sysParam.equalsIgnoreCase("Y")) {
                benefitsCalculationService.calculateAnnualBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber(), bcDoc.getAccount().getLaborBenefitRateCategoryCode());
            } else {
                // pbgl lines are saved at this point, calc benefits
                benefitsCalculationService.calculateAnnualBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber(), this.getDefaultLaborBenefitRateCategoryCode());
            }
            
            // write global message on calc success
            KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BENEFITS_CALCULATED);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#calculateMonthlyBenefits(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    protected void calculateMonthlyBenefits(BudgetConstructionDocument bcDoc) {

        // allow benefits calculation if document's account is not salary setting only lines
        bcDoc.setMonthlyBenefitsCalcNeeded(false);
        if (!bcDoc.isSalarySettingOnly()) {
            
            String sysParam = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND");
            LOG.debug("sysParam: " + sysParam);
            // if sysParam == Y then Labor Benefit Rate Category Code must be used
            if (sysParam.equalsIgnoreCase("Y")) {
                benefitsCalculationService.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber(), bcDoc.getAccount().getLaborBenefitRateCategoryCode());
            } else {
                // pbgl lines are saved at this point, calc benefits
                benefitsCalculationService.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber(),this.getDefaultLaborBenefitRateCategoryCode());
            }
                
            // write global message on calc success
            KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BENEFITS_MONTHLY_CALCULATED);
        }
    }

    /**
     * Does sanity checks for null document object and null documentNumber
     * 
     * @param document
     */
    @NonTransactional
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
     * @param document
     * @param event
     * @throws WorkflowException
     * @throws ValidationException
     */
    @Transactional
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
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#validateDocument(org.kuali.rice.krad.document.Document)
     */
    @Transactional
    public void validateDocument(Document document) throws ValidationException {
        if (document == null) {
            LOG.error("document passed to validateDocument was null");
            throw new IllegalArgumentException("invalid (null) document");
        }
        LOG.info("validating document " + document.getDocumentNumber());
        document.validateBusinessRules(new SaveDocumentEvent(document));

    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getPBGLSalarySettingRows(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    public List<PendingBudgetConstructionGeneralLedger> getPBGLSalarySettingRows(BudgetConstructionDocument bcDocument) {

        List<String> ssObjects = getDetailSalarySettingLaborObjects(bcDocument.getUniversityFiscalYear(), bcDocument.getChartOfAccountsCode());
        ssObjects.add(KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG);
        List<PendingBudgetConstructionGeneralLedger> pbglSalarySettingRows = budgetConstructionDao.getPBGLSalarySettingRows(bcDocument.getDocumentNumber(), ssObjects);

        return pbglSalarySettingRows;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getDetailSalarySettingLaborObjects(java.lang.Integer, java.lang.String)
     */
    @NonTransactional
    public List<String> getDetailSalarySettingLaborObjects(Integer universityFiscalYear, String chartOfAccountsCode) {
        List<String> detailSalarySettingObjects = new ArrayList<String>();

        Map<String, Object> laborObjectCodeMap = new HashMap<String, Object>();
        laborObjectCodeMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        laborObjectCodeMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        laborObjectCodeMap.put(KFSPropertyConstants.DETAIL_POSITION_REQUIRED_INDICATOR, true);
        List<LaborLedgerObject> laborLedgerObjects = kualiModuleService.getResponsibleModuleService(LaborLedgerObject.class).getExternalizableBusinessObjectsList(LaborLedgerObject.class, laborObjectCodeMap);

        for (LaborLedgerObject laborObject : laborLedgerObjects) {
            detailSalarySettingObjects.add(laborObject.getFinancialObjectCode());
        }

        return detailSalarySettingObjects;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#addOrUpdatePBGLRow(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger)
     */
    @NonTransactional
    public BudgetConstructionDocument addOrUpdatePBGLRow(BudgetConstructionDocument bcDoc, PendingBudgetConstructionGeneralLedger sourceRow) {

        List<PendingBudgetConstructionGeneralLedger> expenditureRows = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines();

        // add or update salary setting row to set in memory - this assumes at least one row in the set
        // we can't even do salary setting without at least one salary detail row
        int index = 0;
        boolean insertNeeded = true;
        for (PendingBudgetConstructionGeneralLedger expRow : expenditureRows) {
            String expRowKey = expRow.getFinancialObjectCode() + expRow.getFinancialSubObjectCode();
            String sourceRowKey = sourceRow.getFinancialObjectCode() + sourceRow.getFinancialSubObjectCode();
            if (expRowKey.compareToIgnoreCase(sourceRowKey) == 0) {
                // update
                insertNeeded = false;
                expRow.setAccountLineAnnualBalanceAmount(sourceRow.getAccountLineAnnualBalanceAmount());
                expRow.setPersistedAccountLineAnnualBalanceAmount(sourceRow.getAccountLineAnnualBalanceAmount());
                expRow.setVersionNumber(sourceRow.getVersionNumber());
                break;
            }
            else {
                if (expRowKey.compareToIgnoreCase(sourceRowKey) > 0) {
                    // insert here - drop out
                    break;
                }
            }
            index++;
        }
        if (insertNeeded) {
            // insert the row
            sourceRow.setPersistedAccountLineAnnualBalanceAmount(sourceRow.getAccountLineAnnualBalanceAmount());
            expenditureRows.add(index, sourceRow);
        }

        return bcDoc;
    }

    /**
     * Reloads benefits target accounting lines. Usually called right after an annual benefits calculation and the display needs
     * updated with a fresh copy from the database. All old row versions are removed and database row versions are inserted in the
     * list in the correct order.
     * 
     * @param bcDoc
     */
    @Transactional
    protected void reloadBenefitsLines(BudgetConstructionDocument bcDoc) {

        // get list of potential fringe objects to use as an in query param
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, bcDoc.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, bcDoc.getChartOfAccountsCode());

        List<LaborLedgerBenefitsCalculation> benefitsCalculation = kualiModuleService.getResponsibleModuleService(LaborLedgerBenefitsCalculation.class).getExternalizableBusinessObjectsList(LaborLedgerBenefitsCalculation.class, fieldValues);

        List<String> fringeObjects = new ArrayList<String>();
        for (LaborLedgerBenefitsCalculation element : benefitsCalculation) {
            fringeObjects.add(element.getPositionFringeBenefitObjectCode());
        }

        List<PendingBudgetConstructionGeneralLedger> dbPBGLFringeLines = budgetConstructionDao.getDocumentPBGLFringeLines(bcDoc.getDocumentNumber(), fringeObjects);
        List<PendingBudgetConstructionGeneralLedger> docPBGLExpLines = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines();

        // holds the request sums of removed, added records and used to adjust the document expenditure request total
        KualiInteger docRequestTotals = KualiInteger.ZERO;
        KualiInteger dbRequestTotals = KualiInteger.ZERO;

        // remove the current set of fringe lines
        ListIterator docLines = docPBGLExpLines.listIterator();
        while (docLines.hasNext()) {
            PendingBudgetConstructionGeneralLedger docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
            if (fringeObjects.contains(docLine.getFinancialObjectCode())) {
                docRequestTotals = docRequestTotals.add(docLine.getAccountLineAnnualBalanceAmount());
                docLines.remove();
            }
        }

        // add the dbset of fringe lines, if any
        if (dbPBGLFringeLines != null && !dbPBGLFringeLines.isEmpty()) {

            if (docPBGLExpLines == null || docPBGLExpLines.isEmpty()) {
                docPBGLExpLines.addAll(dbPBGLFringeLines);
            }
            else {
                ListIterator dbLines = dbPBGLFringeLines.listIterator();
                docLines = docPBGLExpLines.listIterator();
                PendingBudgetConstructionGeneralLedger dbLine = (PendingBudgetConstructionGeneralLedger) dbLines.next();
                PendingBudgetConstructionGeneralLedger docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
                boolean dbDone = false;
                boolean docDone = false;
                while (!dbDone) {
                    if (docDone || docLine.getFinancialObjectCode().compareToIgnoreCase(dbLine.getFinancialObjectCode()) > 0) {
                        if (!docDone) {
                            docLine = (PendingBudgetConstructionGeneralLedger) docLines.previous();
                        }
                        dbRequestTotals = dbRequestTotals.add(dbLine.getAccountLineAnnualBalanceAmount());
                        dbLine.setPersistedAccountLineAnnualBalanceAmount(dbLine.getAccountLineAnnualBalanceAmount());
                        this.populatePBGLLine(dbLine);
                        docLines.add(dbLine);
                        if (!docDone) {
                            docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
                        }
                        if (dbLines.hasNext()) {
                            dbLine = (PendingBudgetConstructionGeneralLedger) dbLines.next();
                        }
                        else {
                            dbDone = true;
                        }
                    }
                    else {
                        if (docLines.hasNext()) {
                            docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
                        }
                        else {
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
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#populatePBGLLine(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger)
     */
    @Transactional
    public void populatePBGLLine(PendingBudgetConstructionGeneralLedger line) {

        final List REFRESH_FIELDS;
        if (StringUtils.isNotBlank(line.getFinancialSubObjectCode())) {
            REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT, KFSPropertyConstants.FINANCIAL_SUB_OBJECT, BCPropertyConstants.BUDGET_CONSTRUCTION_MONTHLY }));
        }
        else {
            REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT, BCPropertyConstants.BUDGET_CONSTRUCTION_MONTHLY }));
        }
        persistenceService.retrieveReferenceObjects(line, REFRESH_FIELDS);

    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getPendingBudgetConstructionAppointmentFundingRequestSum(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger)
     */
    @Transactional
    public KualiInteger getPendingBudgetConstructionAppointmentFundingRequestSum(PendingBudgetConstructionGeneralLedger salaryDetailLine) {
        return budgetConstructionDao.getPendingBudgetConstructionAppointmentFundingRequestSum(salaryDetailLine);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isBudgetableDocument(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader)
     */
    @NonTransactional
    public boolean isBudgetableDocument(BudgetConstructionHeader bcHeader) {
        if (bcHeader == null) {
            return false;
        }

        Integer budgetYear = bcHeader.getUniversityFiscalYear();
        Account account = bcHeader.getAccount();
        boolean isBudgetableAccount = this.isBudgetableAccount(budgetYear, account, true);

        if (isBudgetableAccount) {
            SubAccount subAccount = bcHeader.getSubAccount();
            String subAccountNumber = bcHeader.getSubAccountNumber();

            return this.isBudgetableSubAccount(subAccount, subAccountNumber);
        }

        return false;
    }

    @NonTransactional
    public boolean isBudgetableDocumentNoWagesCheck(BudgetConstructionHeader bcHeader) {
        if (bcHeader == null) {
            return false;
        }

        Integer budgetYear = bcHeader.getUniversityFiscalYear();
        Account account = bcHeader.getAccount();
        boolean isBudgetableAccount = this.isBudgetableAccount(budgetYear, account, false);

        if (isBudgetableAccount) {
            SubAccount subAccount = bcHeader.getSubAccount();
            String subAccountNumber = bcHeader.getSubAccountNumber();

            return this.isBudgetableSubAccount(subAccount, subAccountNumber);
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isBudgetableDocument(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @NonTransactional
    public boolean isBudgetableDocument(BudgetConstructionDocument document) {
        if (document == null) {
            return false;
        }

        Integer budgetYear = document.getUniversityFiscalYear();
        Account account = document.getAccount();
        boolean isBudgetableAccount = this.isBudgetableAccount(budgetYear, account, true);

        if (isBudgetableAccount) {
            SubAccount subAccount = document.getSubAccount();
            String subAccountNumber = document.getSubAccountNumber();

            return this.isBudgetableSubAccount(subAccount, subAccountNumber);
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isBudgetableDocumentNoWagesCheck(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @NonTransactional
    public boolean isBudgetableDocumentNoWagesCheck(BudgetConstructionDocument document) {
        if (document == null) {
            return false;
        }

        Integer budgetYear = document.getUniversityFiscalYear();
        Account account = document.getAccount();
        boolean isBudgetableAccount = this.isBudgetableAccount(budgetYear, account, false);

        if (isBudgetableAccount) {
            SubAccount subAccount = document.getSubAccount();
            String subAccountNumber = document.getSubAccountNumber();

            return this.isBudgetableSubAccount(subAccount, subAccountNumber);
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isAssociatedWithBudgetableDocument(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    @NonTransactional
    public boolean isAssociatedWithBudgetableDocument(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        BudgetConstructionHeader bcHeader = this.getBudgetConstructionHeader(appointmentFunding);
        return this.isBudgetableDocument(bcHeader);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isBudgetableAccount(java.lang.Integer,
     *      org.kuali.kfs.coa.businessobject.Account)
     */
    @NonTransactional
    public boolean isBudgetableAccount(Integer budgetYear, Account account, boolean isWagesCheck) {
        if (budgetYear == null || account == null) {
            return false;
        }

        // account cannot be closed.
        if (!account.isActive()) {
            return false;
        }

        // account cannot be expired before beginning of 6th accounting period, 2 years before budget construction fiscal year.
        Calendar expDate = BudgetConstructionRuleUtil.getNoBudgetAllowedExpireDate(budgetYear);
        if (account.isExpired(expDate)) {
            return false;
        }

        // account cannot be a cash control account
        if (StringUtils.equals(account.getBudgetRecordingLevelCode(), BCConstants.BUDGET_RECORDING_LEVEL_N)) {
            return false;
        }

        // this check is needed for salary setting
        if (isWagesCheck) {

            // account must be flagged as wages allowed
            SubFundGroup subFundGroup = account.getSubFundGroup();
            if (subFundGroup == null || !subFundGroup.isSubFundGroupWagesIndicator()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isBudgetableSubAccount(org.kuali.kfs.coa.businessobject.SubAccount,
     *      java.lang.String)
     */
    @NonTransactional
    public boolean isBudgetableSubAccount(SubAccount subAccount, String subAccountNumber) {
        if (StringUtils.isNotEmpty(subAccountNumber) && StringUtils.equals(subAccountNumber, KFSConstants.getDashSubAccountNumber())) {
            return true;
        }

        // sub account must exist and be active.
        if (ObjectUtils.isNull(subAccount) || !subAccount.isActive()) {
            return false;
        }

        // sub account must not be flagged cost share
        A21SubAccount a21SubAccount = subAccount.getA21SubAccount();
        if (ObjectUtils.isNotNull(a21SubAccount) && StringUtils.equals(a21SubAccount.getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isAccountReportsExist(java.lang.String, java.lang.String)
     */
    @Transactional
    public boolean isAccountReportsExist(String chartOfAccountsCode, String accountNumber) {

        BudgetConstructionAccountReports accountReports = (BudgetConstructionAccountReports) budgetConstructionDao.getAccountReports(chartOfAccountsCode, accountNumber);
        if (accountReports == null) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#updatePendingBudgetGeneralLedger(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.core.api.util.type.KualiInteger)
     */
    @Transactional
    public void updatePendingBudgetGeneralLedger(PendingBudgetConstructionAppointmentFunding appointmentFunding, KualiInteger updateAmount) {
        BudgetConstructionHeader budgetConstructionHeader = this.getBudgetConstructionHeader(appointmentFunding);
        if (budgetConstructionHeader == null) {
            return;
        }

        PendingBudgetConstructionGeneralLedger pendingRecord = this.getPendingBudgetConstructionGeneralLedger(budgetConstructionHeader, appointmentFunding, updateAmount, false);
        businessObjectService.save(pendingRecord);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#updatePendingBudgetGeneralLedgerPlug(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.core.api.util.type.KualiInteger)
     */
    @Transactional
    public void updatePendingBudgetGeneralLedgerPlug(PendingBudgetConstructionAppointmentFunding appointmentFunding, KualiInteger updateAmount) {
        if (updateAmount == null) {
            throw new IllegalArgumentException("The update amount cannot be null");
        }

        BudgetConstructionHeader budgetConstructionHeader = this.getBudgetConstructionHeader(appointmentFunding);
        if (budgetConstructionHeader == null) {
            return;
        }

        if (this.canUpdatePlugRecord(appointmentFunding)) {
            PendingBudgetConstructionGeneralLedger plugRecord = this.getPendingBudgetConstructionGeneralLedger(budgetConstructionHeader, appointmentFunding, updateAmount, true);

            KualiInteger annualBalanceAmount = plugRecord.getAccountLineAnnualBalanceAmount();
            KualiInteger beginningBalanceAmount = plugRecord.getFinancialBeginningBalanceLineAmount();

            if ((annualBalanceAmount == null || annualBalanceAmount.isZero()) && (beginningBalanceAmount == null || beginningBalanceAmount.isZero())) {
                businessObjectService.delete(plugRecord);
            }
            else {
                businessObjectService.save(plugRecord);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#updatePendingBudgetGeneralLedgerPlug(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.rice.core.api.util.type.KualiInteger)
     */
    @Transactional
    public PendingBudgetConstructionGeneralLedger updatePendingBudgetGeneralLedgerPlug(BudgetConstructionDocument bcDoc, KualiInteger updateAmount) {

        String twoPlugKey = KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG + KFSConstants.getDashFinancialSubObjectCode();
        List<PendingBudgetConstructionGeneralLedger> expenditureRows = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines();
        PendingBudgetConstructionGeneralLedger twoPlugRow = null;

        // update or insert the 2plg row - this assumes at least one row in the set
        // we can't even do salary setting without at least one detail line
        int index = 0;
        boolean insertNeeded = true;
        for (PendingBudgetConstructionGeneralLedger expRow : expenditureRows) {
            String expRowKey = expRow.getFinancialObjectCode() + expRow.getFinancialSubObjectCode();
            if (expRowKey.compareToIgnoreCase(twoPlugKey) == 0) {

                // update the existing row
                insertNeeded = false;
                expRow.setAccountLineAnnualBalanceAmount(expRow.getAccountLineAnnualBalanceAmount().add(updateAmount.negated()));
                expRow.setPersistedAccountLineAnnualBalanceAmount(expRow.getAccountLineAnnualBalanceAmount());
                businessObjectService.save(expRow);
                expRow.refresh();
                twoPlugRow = expRow;
                break;
            }
            else {
                if (expRowKey.compareToIgnoreCase(twoPlugKey) > 0) {

                    // case where offsetting salary setting updates under different object codes - insert a new row here
                    break;
                }
            }
            index++;
        }
        if (insertNeeded) {

            // do insert in the middle or at end of list
            String objectCode = KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG;
            String subObjectCode = KFSConstants.getDashFinancialSubObjectCode();
            String objectTypeCode = optionsService.getOptions(bcDoc.getUniversityFiscalYear()).getFinObjTypeExpenditureexpCd();

            PendingBudgetConstructionGeneralLedger pendingRecord = new PendingBudgetConstructionGeneralLedger();

            pendingRecord.setDocumentNumber(bcDoc.getDocumentNumber());
            pendingRecord.setUniversityFiscalYear(bcDoc.getUniversityFiscalYear());
            pendingRecord.setChartOfAccountsCode(bcDoc.getChartOfAccountsCode());
            pendingRecord.setAccountNumber(bcDoc.getAccountNumber());
            pendingRecord.setSubAccountNumber(bcDoc.getSubAccountNumber());

            pendingRecord.setFinancialObjectCode(objectCode);
            pendingRecord.setFinancialSubObjectCode(subObjectCode);
            pendingRecord.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
            pendingRecord.setFinancialObjectTypeCode(objectTypeCode);

            pendingRecord.setFinancialBeginningBalanceLineAmount(KualiInteger.ZERO);
            pendingRecord.setAccountLineAnnualBalanceAmount(updateAmount);

            // store and add to memory set
            pendingRecord.setPersistedAccountLineAnnualBalanceAmount(pendingRecord.getAccountLineAnnualBalanceAmount());
            businessObjectService.save(pendingRecord);
            expenditureRows.add(index, pendingRecord);
            twoPlugRow = pendingRecord;
            bcDoc.setContainsTwoPlug(true);
        }

        bcDoc.setExpenditureAccountLineAnnualBalanceAmountTotal(bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal().add(updateAmount.negated()));
        return twoPlugRow;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getBudgetConstructionHeader(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    @NonTransactional
    public BudgetConstructionHeader getBudgetConstructionHeader(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        String accountNumber = appointmentFunding.getAccountNumber();
        String subAccountNumber = appointmentFunding.getSubAccountNumber();
        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();

        return this.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getBudgetConstructionDocument(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    @NonTransactional
    public BudgetConstructionDocument getBudgetConstructionDocument(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, appointmentFunding.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, appointmentFunding.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, appointmentFunding.getAccountNumber());
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, appointmentFunding.getSubAccountNumber());

        // fiscalyear, chart, account, subaccount is a candidate key for BC document
        // This should not need the special handling and just return the first (only document) in the collection
        Collection<BudgetConstructionDocument> documents = businessObjectService.findMatching(BudgetConstructionDocument.class, fieldValues);
        for (BudgetConstructionDocument document : documents) {
            try {
                return (BudgetConstructionDocument) documentService.getByDocumentHeaderId(document.getDocumentHeader().getDocumentNumber());
            }
            catch (WorkflowException e) {
                throw new RuntimeException("Fail to retrieve the document for appointment funding" + appointmentFunding, e);
            }
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getBudgetConstructionDocument(org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion)
     */
    @NonTransactional
    public BudgetConstructionDocument getBudgetConstructionDocument(SalarySettingExpansion salarySettingExpansion) {
        try {
            return (BudgetConstructionDocument) documentService.getByDocumentHeaderId(salarySettingExpansion.getDocumentNumber());
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Fail to retrieve the document for salary expansion" + salarySettingExpansion, e);
        }
    }

    /**
     * determine whether the plug line can be updated or created. If the given appointment funding is in the plug override mode or
     * it associates with a contract and grant account, then no plug can be updated or created
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the plug line can be updated or created; otherwise, false
     */
    @Transactional
    protected boolean canUpdatePlugRecord(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        // no plug if the override mode is enabled
        if (appointmentFunding.isOverride2PlugMode()) {
            return false;
        }

        Account account = appointmentFunding.getAccount();

        // no plug for the account with the sub groups setup as a system parameter
        if (BudgetParameterFinder.getNotGenerate2PlgSubFundGroupCodes().contains(account.getSubFundGroupCode())) {
            return false;
        }

        // no plug for the contract and grant account
        if (account.isForContractsAndGrants()) {
            return false;
        }

        return true;
    }


    /**
     * get a pending budget construction GL record, and set its to the given update amount if it exists in database; otherwise,
     * create it with the given information
     * 
     * @param budgetConstructionHeader the budget construction header of the pending budget construction GL record
     * @param appointmentFunding the appointment funding associated with the pending budget construction GL record
     * @param updateAmount the amount being used to update the retrieved pending budget construction GL record
     * @param is2PLG the flag used to instrcut to retrieve a pending budget construction GL plug record
     * @return a pending budget construction GL record if any; otherwise, create one with the given information
     */
    @Transactional
    protected PendingBudgetConstructionGeneralLedger getPendingBudgetConstructionGeneralLedger(BudgetConstructionHeader budgetConstructionHeader, PendingBudgetConstructionAppointmentFunding appointmentFunding, KualiInteger updateAmount, boolean is2PLG) {
        if (budgetConstructionHeader == null) {
            throw new IllegalArgumentException("The given budget construction document header cannot be null");
        }

        if (appointmentFunding == null) {
            throw new IllegalArgumentException("The given pending budget appointment funding cannot be null");
        }

        if (updateAmount == null) {
            throw new IllegalArgumentException("The update amount cannot be null");
        }

        PendingBudgetConstructionGeneralLedger pendingRecord = this.retrievePendingBudgetConstructionGeneralLedger(budgetConstructionHeader, appointmentFunding, is2PLG);

        if (pendingRecord != null) {
            KualiInteger newAnnaulBalanceAmount = pendingRecord.getAccountLineAnnualBalanceAmount().add(updateAmount);
            pendingRecord.setAccountLineAnnualBalanceAmount(newAnnaulBalanceAmount);
        }
        else if (!is2PLG || (is2PLG && updateAmount.isNonZero())) {
            // initialize a new pending record if not plug line or plug line not zero

            Integer budgetYear = appointmentFunding.getUniversityFiscalYear();
            String objectCode = is2PLG ? KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG : appointmentFunding.getFinancialObjectCode();
            String subObjectCode = is2PLG ? KFSConstants.getDashFinancialSubObjectCode() : appointmentFunding.getFinancialSubObjectCode();
            String objectTypeCode = optionsService.getOptions(budgetYear).getFinObjTypeExpenditureexpCd();

            pendingRecord = new PendingBudgetConstructionGeneralLedger();

            pendingRecord.setDocumentNumber(budgetConstructionHeader.getDocumentNumber());
            pendingRecord.setUniversityFiscalYear(appointmentFunding.getUniversityFiscalYear());
            pendingRecord.setChartOfAccountsCode(appointmentFunding.getChartOfAccountsCode());
            pendingRecord.setAccountNumber(appointmentFunding.getAccountNumber());
            pendingRecord.setSubAccountNumber(appointmentFunding.getSubAccountNumber());

            pendingRecord.setFinancialObjectCode(objectCode);
            pendingRecord.setFinancialSubObjectCode(subObjectCode);
            pendingRecord.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
            pendingRecord.setFinancialObjectTypeCode(objectTypeCode);

            pendingRecord.setFinancialBeginningBalanceLineAmount(KualiInteger.ZERO);
            pendingRecord.setAccountLineAnnualBalanceAmount(updateAmount);
        }

        return pendingRecord;
    }

    /**
     * retrieve a pending budget construction GL record based on the given infromation
     * 
     * @param budgetConstructionHeader the budget construction header of the pending budget construction GL record to be retrieved
     * @param appointmentFunding the appointment funding associated with the pending budget construction GL record to be retrieved
     * @param is2PLG the flag used to instrcut to retrieve a pending budget construction GL plug record
     * @return a pending budget construction GL record if any; otherwise, null
     */
    @NonTransactional
    protected PendingBudgetConstructionGeneralLedger retrievePendingBudgetConstructionGeneralLedger(BudgetConstructionHeader budgetConstructionHeader, PendingBudgetConstructionAppointmentFunding appointmentFunding, boolean is2PLG) {
        String objectCode = is2PLG ? KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG : appointmentFunding.getFinancialObjectCode();
        String subObjectCode = is2PLG ? KFSConstants.getDashFinancialSubObjectCode() : appointmentFunding.getFinancialSubObjectCode();

        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, budgetConstructionHeader.getDocumentNumber());
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetConstructionHeader.getUniversityFiscalYear());
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, budgetConstructionHeader.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, budgetConstructionHeader.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, budgetConstructionHeader.getSubAccountNumber());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, optionsService.getOptions(appointmentFunding.getUniversityFiscalYear()).getFinObjTypeExpenditureexpCd());

        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, subObjectCode);

        return (PendingBudgetConstructionGeneralLedger) businessObjectService.findByPrimaryKey(PendingBudgetConstructionGeneralLedger.class, searchCriteria);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#retrievePendingBudgetConstructionGeneralLedger(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader)
     */
    @NonTransactional
    public List<PendingBudgetConstructionGeneralLedger> retrievePendingBudgetConstructionGeneralLedger(BudgetConstructionHeader budgetConstructionHeader) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, budgetConstructionHeader.getDocumentNumber());
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetConstructionHeader.getUniversityFiscalYear());
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, budgetConstructionHeader.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, budgetConstructionHeader.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, budgetConstructionHeader.getSubAccountNumber());

        return (List<PendingBudgetConstructionGeneralLedger>) businessObjectService.findMatching(PendingBudgetConstructionGeneralLedger.class, searchCriteria);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#retrieveOrBuildAccountOrganizationHierarchy(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    @Transactional
    public List<BudgetConstructionAccountOrganizationHierarchy> retrieveOrBuildAccountOrganizationHierarchy(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {

        List<BudgetConstructionAccountOrganizationHierarchy> accountOrgHier = new ArrayList<BudgetConstructionAccountOrganizationHierarchy>();
        BudgetConstructionAccountReports accountReports = (BudgetConstructionAccountReports) budgetConstructionDao.getAccountReports(chartOfAccountsCode, accountNumber);
        if (accountReports != null) {
            accountOrgHier = budgetConstructionDao.getAccountOrgHierForAccount(chartOfAccountsCode, accountNumber, universityFiscalYear);
            if (accountOrgHier == null || accountOrgHier.isEmpty()) {

                // attempt to build it
                String[] rootNode = organizationService.getRootOrganizationCode();
                String rootChart = rootNode[0];
                String rootOrganization = rootNode[1];
                Integer currentLevel = new Integer(1);
                String organizationChartOfAccountsCode = accountReports.getReportsToChartOfAccountsCode();
                String organizationCode = accountReports.getReportsToOrganizationCode();
                boolean overFlow = budgetConstructionDao.insertAccountIntoAccountOrganizationHierarchy(rootChart, rootOrganization, universityFiscalYear, chartOfAccountsCode, accountNumber, currentLevel, organizationChartOfAccountsCode, organizationCode);
                if (!overFlow) {
                    accountOrgHier = budgetConstructionDao.getAccountOrgHierForAccount(chartOfAccountsCode, accountNumber, universityFiscalYear);
                }
            }
        }
        return accountOrgHier;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#instantiateNewBudgetConstructionDocument(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    public BudgetConstructionDocument instantiateNewBudgetConstructionDocument(BudgetConstructionDocument budgetConstructionDocument) throws WorkflowException {

        budgetConstructionDocument.setOrganizationLevelChartOfAccountsCode(BCConstants.INITIAL_ORGANIZATION_LEVEL_CHART_OF_ACCOUNTS_CODE);
        budgetConstructionDocument.setOrganizationLevelOrganizationCode(BCConstants.INITIAL_ORGANIZATION_LEVEL_ORGANIZATION_CODE);
        budgetConstructionDocument.setOrganizationLevelCode(BCConstants.INITIAL_ORGANIZATION_LEVEL_CODE);
        budgetConstructionDocument.setBudgetTransactionLockUserIdentifier(BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
        budgetConstructionDocument.setBudgetLockUserIdentifier(BCConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);

        FinancialSystemDocumentHeader kualiDocumentHeader = budgetConstructionDocument.getFinancialSystemDocumentHeader();
        budgetConstructionDocument.setDocumentNumber(budgetConstructionDocument.getDocumentHeader().getDocumentNumber());
        kualiDocumentHeader.setOrganizationDocumentNumber(budgetConstructionDocument.getUniversityFiscalYear().toString());
        kualiDocumentHeader.setFinancialDocumentStatusCode(KFSConstants.INITIAL_KUALI_DOCUMENT_STATUS_CD);
        kualiDocumentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);
        kualiDocumentHeader.setDocumentDescription(String.format("%s %d %s %s", BCConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION, budgetConstructionDocument.getUniversityFiscalYear(), budgetConstructionDocument.getChartOfAccountsCode(), budgetConstructionDocument.getAccountNumber()));
        kualiDocumentHeader.setExplanation(BCConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION);
        businessObjectService.save(budgetConstructionDocument);
        List<AdHocRouteRecipient> emptyAdHocList = new ArrayList<AdHocRouteRecipient>();

        // call route with document type configured for no route paths or post processor
        documentService.routeDocument(budgetConstructionDocument, "created by application UI", emptyAdHocList);

        return budgetConstructionDocument;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getPushPullLevelList(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Transactional
    public List<BudgetConstructionAccountOrganizationHierarchy> getPushPullLevelList(BudgetConstructionDocument bcDoc, Person person) {
        List<BudgetConstructionAccountOrganizationHierarchy> pushOrPullList = new ArrayList<BudgetConstructionAccountOrganizationHierarchy>();

        pushOrPullList.addAll(budgetConstructionDao.getAccountOrgHierForAccount(bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getUniversityFiscalYear()));

        if (pushOrPullList.size() >= 1) {
            BudgetConstructionAccountOrganizationHierarchy levelZero = new BudgetConstructionAccountOrganizationHierarchy();
            levelZero.setUniversityFiscalYear(bcDoc.getUniversityFiscalYear());
            levelZero.setChartOfAccountsCode(bcDoc.getChartOfAccountsCode());
            levelZero.setAccountNumber(bcDoc.getAccountNumber());
            levelZero.setOrganizationLevelCode(0);
            levelZero.setOrganizationChartOfAccountsCode(pushOrPullList.get(0).getOrganizationChartOfAccountsCode());
            levelZero.setOrganizationCode(pushOrPullList.get(0).getOrganizationCode());
            pushOrPullList.add(0, levelZero);
        }

        return pushOrPullList;
    }

    /**
     * Sets the budgetConstructionDao attribute value.
     * 
     * @param budgetConstructionDao The budgetConstructionDao to set.
     */
    @NonTransactional
    public void setBudgetConstructionDao(BudgetConstructionDao budgetConstructionDao) {
        this.budgetConstructionDao = budgetConstructionDao;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    @NonTransactional
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the workflowDocumentService attribute value.
     * 
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    @NonTransactional
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * Sets the documentDao attribute value.
     * 
     * @param documentDao The documentDao to set.
     */
    @NonTransactional
    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }


    /**
     * Sets the benefitsCalculationService attribute value.
     * 
     * @param benefitsCalculationService The benefitsCalculationService to set.
     */
    @NonTransactional
    public void setBenefitsCalculationService(BenefitsCalculationService benefitsCalculationService) {
        this.benefitsCalculationService = benefitsCalculationService;
    }


    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the budgetParameterService attribute value.
     * 
     * @param budgetParameterService The budgetParameterService to set.
     */
    @NonTransactional
    public void setBudgetParameterService(BudgetParameterService budgetParameterService) {
        this.budgetParameterService = budgetParameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    @NonTransactional
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the fiscalYearFunctionControlService attribute value.
     * 
     * @param fiscalYearFunctionControlService The fiscalYearFunctionControlService to set.
     */
    @NonTransactional
    public void setFiscalYearFunctionControlService(FiscalYearFunctionControlService fiscalYearFunctionControlService) {
        this.fiscalYearFunctionControlService = fiscalYearFunctionControlService;
    }


    /**
     * Sets the optionsService attribute value.
     * 
     * @param optionsService The optionsService to set.
     */
    @NonTransactional
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }


    /**
     * Gets the persistenceService attribute.
     * 
     * @return Returns the persistenceService.
     */
    @NonTransactional
    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    /**
     * Sets the persistenceService attribute value.
     * 
     * @param persistenceService The persistenceService to set.
     */
    @NonTransactional
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * Sets the organizationService attribute value.
     * 
     * @param organizationService The organizationService to set.
     */
    @NonTransactional
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * Sets the kualiModuleService attribute value.
     * 
     * @param kualiModuleService The kualiModuleService to set.
     */
    @NonTransactional
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * Gets the defaultLaborBenefitRateCategoryCode attribute. 
     * @return Returns the defaultLaborBenefitRateCategoryCode.
     */
    @Transactional
    public String getDefaultLaborBenefitRateCategoryCode() {
        if(ObjectUtils.isNull(defaultLaborBenefitRateCategoryCode)){
         // make sure the parameter exists
            if (SpringContext.getBean(ParameterService.class).parameterExists(Account.class, "DEFAULT_BENEFIT_RATE_CATEGORY_CODE")) {
                this.defaultLaborBenefitRateCategoryCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(Account.class, "DEFAULT_BENEFIT_RATE_CATEGORY_CODE");
            }
            else {
                this.defaultLaborBenefitRateCategoryCode = "";
            }
        }
        return defaultLaborBenefitRateCategoryCode;
    }

    /**
     * Sets the defaultLaborBenefitRateCategoryCode attribute value.
     * @param defaultLaborBenefitRateCategoryCode The defaultLaborBenefitRateCategoryCode to set.
     */
    @Transactional
    public void setDefaultLaborBenefitRateCategoryCode(String defaultLaborBenefitRateCategoryCode) {
        this.defaultLaborBenefitRateCategoryCode = defaultLaborBenefitRateCategoryCode;
    }

}
