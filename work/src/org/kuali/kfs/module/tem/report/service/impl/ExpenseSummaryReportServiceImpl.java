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
package org.kuali.kfs.module.tem.report.service.impl;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.Report.TRAVEL_REPORT_INSTITUTION_NAME;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.LODGING_TYPE_CODES;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.SHOW_TA_ESTIMATE_IN_SUMMARY_REPORT_IND;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.TRANSPORTATION_TYPE_CODES;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.module.tem.util.BufferedLogger.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.report.ExpenseSummaryReport;
import org.kuali.kfs.module.tem.report.service.ExpenseSummaryReportService;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of ExpenseSummaryReportService.
 */
@Transactional
public class ExpenseSummaryReportServiceImpl implements ExpenseSummaryReportService {

    private KualiConfigurationService configurationService;
    private ParameterService parameterService;
    private PersonService personService;
    private TravelAuthorizationService travelAuthorizationService;
    private AccountingDistributionService accountingDistributionService;
    private TravelDocumentService travelDocumentService;

    public KualiConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final KualiConfigurationService kualiConfigurationService) {
        this.configurationService = kualiConfigurationService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(final PersonService personService) {
        this.personService = personService;
    }

    /**
     * Creates a {@link ReportInfoHolder} instance that is used with the {@link TravelReportService}
     */
    @Override
    public ExpenseSummaryReport buildReport(TravelDocument travelDocument) {
        info("Building report objects");
        final ExpenseSummaryReport retval = new ExpenseSummaryReport();
        retval.setTraveler(travelDocument.getTraveler().getFirstName() + " " + travelDocument.getTraveler().getLastName());
        
        final String initiatorId = travelDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        final Person initiator = getPersonService().getPerson(initiatorId);
        retval.setInitiator(initiator.getFirstName() + " " + initiator.getLastName());
        retval.setBeginDate(travelDocument.getTripBegin() != null ? DateUtils.clearTimeFields(travelDocument.getTripBegin()) : new Date());
        retval.setEndDate(travelDocument.getTripEnd() != null ? DateUtils.clearTimeFields(travelDocument.getTripEnd()) : new Date());
        retval.setLocations(travelDocument.getPrimaryDestinationName());
        retval.setPurpose(travelDocument.getReportPurpose() == null ? "" : travelDocument.getReportPurpose());
        retval.setTripId(travelDocument.getTravelDocumentIdentifier() + "");
        retval.setInstitution(getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TRAVEL_REPORT_INSTITUTION_NAME));

        final Collection<ExpenseSummaryReport.Detail> expenses = new ArrayList<ExpenseSummaryReport.Detail>();
        final Collection<ExpenseSummaryReport.Detail> summary = new ArrayList<ExpenseSummaryReport.Detail>();

        List<ExpenseSummaryReport.Detail> perDiemMilageList = new ArrayList<ExpenseSummaryReport.Detail>();
        List<ExpenseSummaryReport.Detail> perDiemLodgingList = new ArrayList<ExpenseSummaryReport.Detail>();
        List<ExpenseSummaryReport.Detail> perDiemMealList = new ArrayList<ExpenseSummaryReport.Detail>();
        
        for (final PerDiemExpense perDiemExpense: travelDocument.getPerDiemExpenses()){
            if (perDiemExpense.getMileageTotal().isGreaterThan(KualiDecimal.ZERO)) {
                ExpenseSummaryReport.Detail perDiemMilage = new ExpenseSummaryReport.Detail("Per Diem Milage", "TRANSPORTATION", perDiemExpense.getMileageTotal(), perDiemExpense.getMileageDate());
                perDiemMilageList.add(perDiemMilage);
            }

            if (perDiemExpense.getLodgingTotal().isGreaterThan(KualiDecimal.ZERO)) {
                ExpenseSummaryReport.Detail perDiemLodging = new ExpenseSummaryReport.Detail("Per Diem Lodging", "LODGING", perDiemExpense.getLodgingTotal(), perDiemExpense.getMileageDate());
                perDiemLodgingList.add(perDiemLodging);
            }

            if (perDiemExpense.getMealsAndIncidentals().isGreaterThan(KualiDecimal.ZERO)) {
                ExpenseSummaryReport.Detail perDiemMeal = new ExpenseSummaryReport.Detail("Per Diem Meals", "MEALS", perDiemExpense.getMealsAndIncidentals(), perDiemExpense.getMileageDate());
                perDiemMealList.add(perDiemMeal);
            }
        }
        
        if (perDiemMilageList.size() > 0) {
            expenses.addAll(perDiemMilageList);
        }
        
        if (perDiemLodgingList.size() > 0) {
            expenses.addAll(perDiemLodgingList);
        }
        
        if (perDiemMealList.size() > 0) {
            expenses.addAll(perDiemMealList);
        }
               
        info("Adding details from other expenses");
        debug("There are ", travelDocument.getActualExpenses().size(), " other expenses");
        for (final ActualExpense expense : travelDocument.getActualExpenses()) {
            expense.refreshReferenceObject("travelExpenseTypeCode");
            
            if (expense.getNonReimbursable()) {
                debug("Adding detail for non reimbursable item");
                expenses.add(new ExpenseSummaryReport.Detail(expense.getTravelExpenseTypeCode().getName(), "NONREIMBURSABLE", expense.getConvertedAmount(), expense.getExpenseDate()));
            }

            String expenseType = "OTHER";
            if (isTransportationExpense(expense)) {
                expenseType = "TRANSPORTATION";
            }
            else if (isLodgingExpense(expense)) {
                expenseType = "LODGING";
            }
            else if (isMealsExpense(expense)) {
                expenseType = "MEALS";
            }
            
            expenses.add(new ExpenseSummaryReport.Detail(expense.getTravelExpenseTypeCode().getName(), expenseType, expense.getConvertedAmount(), expense.getExpenseDate()));           
        }

        final KualiDecimal expenseLimit = travelDocument.getExpenseLimit() == null ? KualiDecimal.ZERO : travelDocument.getExpenseLimit();
        final KualiDecimal maxExpense = expenseLimit.isLessThan(travelDocument.getDocumentGrandTotal()) ? expenseLimit : travelDocument.getDocumentGrandTotal();
        String totalExpenseName = "Owed to Requestor";
        KualiDecimal owed = maxExpense;
        
        boolean isTR = travelDocument.getDocumentHeader().getWorkflowDocument().getDocumentType().equals(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
        
        KualiDecimal advanceTotal = KualiDecimal.ZERO;
        if (isTR) {
            advanceTotal = getTravelDocumentService().getAdvancesTotalFor(travelDocument);
            owed = maxExpense.subtract(advanceTotal);
            if (advanceTotal.isGreaterThan(maxExpense)) {
                totalExpenseName = "Owed to Institution";
                owed = advanceTotal.subtract(maxExpense);
            }
        }

        summary.add(new ExpenseSummaryReport.Detail("Expense Total", "SUMMARY", travelDocument.getDocumentGrandTotal(), travelDocument.getTripBegin()));
        summary.add(new ExpenseSummaryReport.Detail("Expense Limit", "SUMMARY", travelDocument.getExpenseLimit(), travelDocument.getTripBegin()));
        if (isTR) {
            summary.add(new ExpenseSummaryReport.Detail("Less Advances", "SUMMARY", advanceTotal, travelDocument.getTripBegin()));
        }
        summary.add(new ExpenseSummaryReport.Detail(totalExpenseName, "SUMMARY", owed, travelDocument.getTripBegin()));
        
        if (isTR) {
            final boolean showTAEstimate = getParameterService().getIndicatorParameter(PARAM_NAMESPACE, TravelReimbursementParameters.PARAM_DTL_TYPE, SHOW_TA_ESTIMATE_IN_SUMMARY_REPORT_IND);

            if (showTAEstimate) {
                try {
                    TravelDocument ta = getTravelDocumentService().findCurrentTravelAuthorization(travelDocument);
                    if (ta != null) {
                        summary.add(new ExpenseSummaryReport.Detail("Expense Total Estimate", "SUMMARY", ta.getDocumentGrandTotal(), ta.getTripBegin()));
                    }
                }
                catch (WorkflowException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
            }            
        }
        
        List<AccountingDistribution> accountingDistributions = getAccountingDistributionService().buildDistributionFrom(travelDocument);
        if(accountingDistributions != null){
            for(AccountingDistribution ad : accountingDistributions){
                summary.add(new ExpenseSummaryReport.Detail("Distribution: "+ad.getObjectCode(), "OTHER", ad.getSubTotal().abs(), travelDocument.getTripBegin()));
            }
        }         
        
        if (summary.size() > 0) {
            retval.setSummary(summary);
        }
        if (expenses.size() > 0) {
            retval.setData(expenses);
        }

        return retval;
    }

    protected boolean isTransportationExpense(final ActualExpense expense) {
        debug("Checking if ", expense, " is a transportation ");
        return expenseTypeCodeMatchesParameter(expense.getTravelExpenseTypeCodeCode(), TRANSPORTATION_TYPE_CODES);
    }

    protected boolean isMealsExpense(final ActualExpense expense) {
        return getTravelDocumentService().isHostedMeal(expense);
    }
    
    protected boolean isLodgingExpense(final ActualExpense expense) {
        debug("Checking if ", expense, " is a lodging ");
        return expenseTypeCodeMatchesParameter(expense.getTravelExpenseTypeCodeCode(), LODGING_TYPE_CODES);
    }

    protected boolean expenseTypeCodeMatchesParameter(final String expenseTypeCode, final String parameter) {
        return getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, parameter).indexOf(expenseTypeCode) != -1;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    /**
     * Gets the travelAuthorizationService attribute. 
     * @return Returns the travelAuthorizationService.
     */
    public TravelAuthorizationService getTravelAuthorizationService() {
        return travelAuthorizationService;
    }

    /**
     * Sets the travelAuthorizationService attribute value.
     * @param travelAuthorizationService The travelAuthorizationService to set.
     */
    public void setTravelAuthorizationService(TravelAuthorizationService travelAuthorizationService) {
        this.travelAuthorizationService = travelAuthorizationService;
    }
    
    /**
     * Gets the travelAuthorizationService attribute. 
     * @return Returns the travelAuthorizationService.
     */
    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    /**
     * Sets the travelDocumentService attribute value.
     * @param travelDocumentService The travelDocumentService to set.
     */
    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public AccountingDistributionService getAccountingDistributionService() {
        return accountingDistributionService;
    }

    public void setAccountingDistributionService(AccountingDistributionService accountingDistributionService) {
        this.accountingDistributionService = accountingDistributionService;
    }
    
}
