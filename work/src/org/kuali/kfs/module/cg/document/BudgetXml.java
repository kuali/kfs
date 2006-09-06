/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.budget.xml;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.KraConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetModular;
import org.kuali.module.kra.budget.bo.BudgetModularPeriod;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetPeriodThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetPeriodUniversityCostShare;
import org.kuali.module.kra.budget.bo.BudgetTask;
import org.kuali.module.kra.budget.bo.BudgetTaskPeriodIndirectCost;
import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetUniversityCostShare;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.web.struts.form.BudgetCostShareFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetIndirectCostFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetOverviewFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetOverviewFormHelper.BudgetOverviewPersonnelHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class creates an XML representation of a Budget's data.
 * 
 * @author KRA (era_team@indiana.edu)
 */
public class BudgetXml {

    private static String TO_BE_NAMED;
    private static final String OUTPUT_PERCENT_SYMBOL = "%";

    /**
     * Driving method for this class. Functions as a hub calling helper methods.
     * 
     * @param budgetDoc data representation of a budget
     * @param xmlDoc target xml representation for the budget. This field will be side effected.
     * @param param a parameter that is to be added to the XML as PARAMETER1. Useful for custom functions of a style sheet.
     * @throws Exception
     */
    public static void makeXml(BudgetDocument budgetDoc, Document xmlDoc, String baseUrl, String param) throws Exception {
        Budget budget = budgetDoc.getBudget();

        TO_BE_NAMED = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "toBeNamedLabel");

        // Initialize data needed. This is data true for the budget as global. There is some data in createTaskPeriodsElement
        // that is only true for a certain task / period.
        List nonpersonnelCategories = SpringServiceLocator.getBudgetNonpersonnelService().getAllNonpersonnelCategories();
        if (budget.isAgencyModularIndicator()) {
            SpringServiceLocator.getBudgetModularService().generateModularBudget(budget, nonpersonnelCategories);
        }
        SpringServiceLocator.getBudgetIndirectCostService().refreshIndirectCost(budgetDoc);
        BudgetIndirectCostFormHelper budgetIndirectCostFormHelper = new BudgetIndirectCostFormHelper(budget.getTasks(), budget.getPeriods(), budget.getIndirectCost().getBudgetTaskPeriodIndirectCostItems());

        // Start of XML elements
        Element proposalElement = xmlDoc.createElement("PROPOSAL");
        xmlDoc.appendChild(proposalElement);

        Element budgetElement = xmlDoc.createElement("BUDGET");
        proposalElement.appendChild(budgetElement);

        budgetElement.setAttribute("BUDGET_NUMBER", budget.getDocumentHeaderId());
        budgetElement.setAttribute("CURRENT_BASE", budget.getIndirectCost().getBudgetBaseCode());
        budgetElement.setAttribute("PURPOSE", budget.getIndirectCost().getBudgetPurposeCode());
        budgetElement.setAttribute("GRANT_NUMBER", budget.getElectronicResearchAdministrationGrantNumber());

        // Code to get the current date/time
        Calendar calendar = new GregorianCalendar();
        Date date = calendar.getTime();
        DateFormat localFormat = DateFormat.getDateTimeInstance();

        budgetElement.setAttribute("XML_CREATE_DATE_TIME", localFormat.format(date));
        budgetElement.setAttribute("BASE_URL", baseUrl);
        budgetElement.setAttribute("PARAMETER1", param);

        budgetElement.appendChild(createProjectDirectorElement(budget, xmlDoc));
        budgetElement.appendChild(createAgencyElement(budget, xmlDoc));
        budgetElement.appendChild(createTasksElement(budget, xmlDoc));
        budgetElement.appendChild(createPeriodsElement(budget, xmlDoc));
        budgetElement.appendChild(createCostShareElement(budgetIndirectCostFormHelper, budget, xmlDoc));
        budgetElement.appendChild(createInflationRatesElement(budget, xmlDoc));
        budgetElement.appendChild(createTaskPeriodsElement(nonpersonnelCategories, budgetIndirectCostFormHelper, budget, xmlDoc));
        budgetElement.appendChild(createIndirectCostElement(budgetIndirectCostFormHelper, budget, xmlDoc));
        budgetElement.appendChild(createModularBudgetElement(budget, xmlDoc));
    }

    /**
     * Creates PROJECT_DIRECTOR node.
     * 
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createProjectDirectorElement(Budget budget, Document xmlDoc) {
        Element projectDirectorElement = xmlDoc.createElement("PROJECT_DIRECTOR");

        if (budget.isProjectDirectorToBeNamedIndicator()) {
            projectDirectorElement.setAttribute("FIRST_NAME", TO_BE_NAMED);
        }
        else if (budget.getProjectDirector() != null) {
            projectDirectorElement.setAttribute("FIRST_NAME", ObjectUtils.toString(budget.getProjectDirector().getUniversalUser().getPersonFirstName()));

            projectDirectorElement.setAttribute("LAST_NAME", ObjectUtils.toString(budget.getProjectDirector().getUniversalUser().getPersonLastName()));

            projectDirectorElement.setAttribute("USER_ID", ObjectUtils.toString(budget.getProjectDirector().getUniversalUser().getPersonEmailAddress()));
        }

        return projectDirectorElement;
    }

    /**
     * Creates AGENCY node.
     * 
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createAgencyElement(Budget budget, Document xmlDoc) {
        Element agencyElement = xmlDoc.createElement("AGENCY");

        if (budget.isAgencyToBeNamedIndicator()) {
            agencyElement.setAttribute("AGENCY_FULL_NAME", TO_BE_NAMED);
        }
        else if (budget.getBudgetAgency() != null) {
            agencyElement.setAttribute("AGENCY_NUMBER", ObjectUtils.toString(budget.getBudgetAgency().getAgencyNumber()));

            agencyElement.setAttribute("AGENCY_FULL_NAME", ObjectUtils.toString(budget.getBudgetAgency().getFullName()));

            agencyElement.setAttribute("AGENCY_REPORT_NAME", ObjectUtils.toString(budget.getBudgetAgency().getReportingName()));
        }

        return agencyElement;
    }

    /**
     * Creates TASKS node.
     * 
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createTasksElement(Budget budget, Document xmlDoc) {
        Element tasksElement = xmlDoc.createElement("TASKS");

        Iterator iter = budget.getTasks().iterator();
        for (int i = 0; iter.hasNext(); i++) {
            BudgetTask currentTask = (BudgetTask) iter.next();
            Element taskElement = xmlDoc.createElement("TASK");
            // Setup with the default location attribute
            taskElement.setAttribute("LOCATION", "ON");

            Element taskSequenceNumberElement = xmlDoc.createElement("TASK_SEQUENCE_NUMBER");
            taskSequenceNumberElement.appendChild(xmlDoc.createTextNode(currentTask.getBudgetTaskSequenceNumber().toString()));
            taskElement.appendChild(taskSequenceNumberElement);

            Element taskNumberElement = xmlDoc.createElement("TASK_NUMBER");
            taskNumberElement.appendChild(xmlDoc.createTextNode(Integer.toString(i + 1)));
            taskElement.appendChild(taskNumberElement);

            Element taskNameElement = xmlDoc.createElement("TASK_NAME");
            taskNameElement.appendChild(xmlDoc.createTextNode(currentTask.getBudgetTaskName()));
            taskElement.appendChild(taskNameElement);

            tasksElement.appendChild(taskElement);
        }

        return tasksElement;
    }

    /**
     * Creates PERIODS node.
     * 
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createPeriodsElement(Budget budget, Document xmlDoc) {
        Element periodsElement = xmlDoc.createElement("PERIODS");

        DateFormat dateFormatterKra = new SimpleDateFormat("MM/dd/yyyy"); // this is also in BudgetPeriod
        DateFormat dateFormatterLong = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);

        Iterator iter = budget.getPeriods().iterator();
        for (int i = 0; iter.hasNext(); i++) {
            BudgetPeriod currentPeriod = (BudgetPeriod) iter.next();
            Element periodElement = xmlDoc.createElement("PERIOD");

            Element periodSequenceNumberElement = xmlDoc.createElement("PERIOD_SEQUENCE_NUMBER");
            periodSequenceNumberElement.appendChild(xmlDoc.createTextNode(currentPeriod.getBudgetPeriodSequenceNumber().toString()));
            periodElement.appendChild(periodSequenceNumberElement);

            Element periodNumberElement = xmlDoc.createElement("PERIOD_NUMBER");
            periodNumberElement.appendChild(xmlDoc.createTextNode(Integer.toString(i + 1)));
            periodElement.appendChild(periodNumberElement);

            Element periodStartDateElement = xmlDoc.createElement("START_DATE");
            periodStartDateElement.appendChild(xmlDoc.createTextNode(dateFormatterKra.format(currentPeriod.getBudgetPeriodBeginDate())));
            periodElement.appendChild(periodStartDateElement);

            Element periodStartDateStringElement = xmlDoc.createElement("START_DATE_STRING");
            periodStartDateStringElement.appendChild(xmlDoc.createTextNode(dateFormatterLong.format(currentPeriod.getBudgetPeriodBeginDate())));
            periodElement.appendChild(periodStartDateStringElement);

            Element periodStopDateElement = xmlDoc.createElement("STOP_DATE");
            periodStopDateElement.appendChild(xmlDoc.createTextNode(dateFormatterKra.format(currentPeriod.getBudgetPeriodEndDate())));
            periodElement.appendChild(periodStopDateElement);

            Element periodStopDateStringElement = xmlDoc.createElement("STOP_DATE_STRING");
            periodStopDateStringElement.appendChild(xmlDoc.createTextNode(dateFormatterLong.format(currentPeriod.getBudgetPeriodEndDate())));
            periodElement.appendChild(periodStopDateStringElement);
            periodsElement.appendChild(periodElement);
        }

        return periodsElement;
    }

    /**
     * Creates COST_SHARE node.
     * 
     * @param budgetIndirectCostFormHelper
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createCostShareElement(BudgetIndirectCostFormHelper budgetIndirectCostFormHelper, Budget budget, Document xmlDoc) throws Exception {
        Element costShareElement = xmlDoc.createElement("COST_SHARE");

        costShareElement.setAttribute("INSTITUTION_COST_SHARE_INDICATOR", "YES");
        costShareElement.setAttribute("THIRD_PARTY_COST_SHARE_INDICATOR", "NO");

        BudgetCostShareFormHelper budgetCostShareFormHelper =
            new BudgetCostShareFormHelper(budget.getPeriods(), budget.getPersonnel(), budget.getNonpersonnelItems(),
                    budget.getUniversityCostSharePersonnelItems(), budget.getUniversityCostShareItems(), budget.getThirdPartyCostShareItems(),
                    budgetIndirectCostFormHelper);

        costShareElement.appendChild(createInstitutionCostShareElement(budgetCostShareFormHelper, budget, xmlDoc));
        costShareElement.appendChild(createInstitutionIndirectCostShareElement(budgetIndirectCostFormHelper, budget, xmlDoc));
        costShareElement.appendChild(createThirdPartyCostShareElement(budgetCostShareFormHelper, budget, xmlDoc));
        costShareElement.appendChild(createSubcontractorCostShareElement(budgetCostShareFormHelper, budget, xmlDoc));

        return costShareElement;
    }

    /**
     * Creates INSTITUTION_COST_SHARE node.
     * 
     * @param budgetCostShareFormHelper
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createInstitutionCostShareElement(BudgetCostShareFormHelper budgetCostShareFormHelper, Budget budget, Document xmlDoc) throws Exception {
        Element institutionCostShareElement = xmlDoc.createElement("INSTITUTION_COST_SHARE");
        Element institutionCostSharePeriodsElement = xmlDoc.createElement("INSTITUTION_COST_SHARE_PERIODS");

        institutionCostSharePeriodsElement.setAttribute("TOTAL_INSTITUTION_BUDGETED", budgetCostShareFormHelper.getInstitutionDirect().getTotalTotalBudgeted().toString());
        institutionCostSharePeriodsElement.setAttribute("TOTAL_INSTITUTION_AMOUNT_DISTRIBUTED", budgetCostShareFormHelper.getInstitutionDirect().getTotalAmountDistributed().toString());
        institutionCostSharePeriodsElement.setAttribute("TOTAL_INSTITUTION_BALANCE", budgetCostShareFormHelper.getInstitutionDirect().getTotalBalanceToBeDistributed().toString());

        for (int i = 0; i < budget.getPeriods().size(); i++) {
            Element institutionCostSharePeriodElement = xmlDoc.createElement("INSTITUTION_COST_SHARE_PERIOD");
            institutionCostSharePeriodElement.setAttribute("PERIOD_NUMBER", Integer.toString(i + 1));

            Element institutionTotalBudgetedElement = xmlDoc.createElement("INSTITUTION_TOTAL_BUDGETED");
            institutionTotalBudgetedElement.appendChild(xmlDoc.createTextNode(budgetCostShareFormHelper.getInstitutionDirect().getTotalBudgeted()[i].toString()));
            institutionCostSharePeriodElement.appendChild(institutionTotalBudgetedElement);

            Element institutionAmountDistributedElement = xmlDoc.createElement("INSTITUTION_AMOUNT_DISTRIBUTED");
            institutionAmountDistributedElement.appendChild(xmlDoc.createTextNode(budgetCostShareFormHelper.getInstitutionDirect().getAmountDistributed()[i].toString()));
            institutionCostSharePeriodElement.appendChild(institutionAmountDistributedElement);

            Element institutionBalanceElement = xmlDoc.createElement("INSTITUTION_BALANCE");
            institutionBalanceElement.appendChild(xmlDoc.createTextNode(budgetCostShareFormHelper.getInstitutionDirect().getBalanceToBeDistributed()[i].toString()));
            institutionCostSharePeriodElement.appendChild(institutionBalanceElement);

            institutionCostSharePeriodsElement.appendChild(institutionCostSharePeriodElement);
        }
        institutionCostShareElement.appendChild(institutionCostSharePeriodsElement);

        Element institutionCostShareChartOrgsElement = xmlDoc.createElement("INSTITUTION_COST_SHARE_CHART_ORGS");
        for (int i = 0; i < budget.getUniversityCostShareItems().size(); i++) {
            BudgetUniversityCostShare budgetUniversityCostShare = budget.getUniversityCostShareItem(i);

            Element institutionCostShareChartOrgElement = xmlDoc.createElement("INSTITUTION_COST_SHARE_CHART_ORG");
            institutionCostShareChartOrgElement.setAttribute("CHART", budgetUniversityCostShare.getChartOfAccountsCode());
            institutionCostShareChartOrgElement.setAttribute("ORG", budgetUniversityCostShare.getOrganizationCode());
            institutionCostShareChartOrgElement.setAttribute("TOTAL", budgetCostShareFormHelper.getInstitutionDirect().getTotalSource()[i].toString());

            Iterator innerIter = budgetUniversityCostShare.getBudgetPeriodCostShare().iterator();
            for (int j = 0; innerIter.hasNext(); j++) {
                BudgetPeriodUniversityCostShare periodInstitutionCostShare = (BudgetPeriodUniversityCostShare) innerIter.next();

                Element institutionCostShareChartOrgPeriodElement = xmlDoc.createElement("INSTITUTION_COST_SHARE_CHART_ORG_PERIOD");
                institutionCostShareChartOrgPeriodElement.setAttribute("PERIOD_NUMBER", Integer.toString(j + 1));

                Element institutionCostShareChartOrgPeriodAmountElement = xmlDoc.createElement("INSTITUTION_COST_SHARE_CHART_ORG_PERIOD_AMOUNT");
                institutionCostShareChartOrgPeriodAmountElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(periodInstitutionCostShare.getBudgetCostShareAmount())));
                institutionCostShareChartOrgPeriodElement.appendChild(institutionCostShareChartOrgPeriodAmountElement);

                institutionCostShareChartOrgElement.appendChild(institutionCostShareChartOrgPeriodElement);
            }
            institutionCostShareChartOrgsElement.appendChild(institutionCostShareChartOrgElement);
        }
        institutionCostShareElement.appendChild(institutionCostShareChartOrgsElement);

        return institutionCostShareElement;
    }

    /**
     * Creates INSTITUTION_INDIRECT_COST_SHARE node.
     * 
     * @param budgetCostShareFormHelper
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createInstitutionIndirectCostShareElement(BudgetIndirectCostFormHelper budgetIndirectCostFormHelper, Budget budget, Document xmlDoc) {
        Element institutionIndirectCostShareElement = xmlDoc.createElement("INSTITUTION_INDIRECT_COST_SHARE");
        
        institutionIndirectCostShareElement.setAttribute("TOTAL_INDIRECT_COST_SHARE", budgetIndirectCostFormHelper.getPeriodSubTotal().getCostShareCalculatedIndirectCost().toString());
        institutionIndirectCostShareElement.setAttribute("TOTAL_UNRECOVERED_INDIRECT_COST", budgetIndirectCostFormHelper.getPeriodSubTotal().getCostShareUnrecoveredIndirectCost().toString());

        for (int i = 0; i < budget.getPeriods().size(); i++) {
            BudgetTaskPeriodIndirectCost periodTotal = budgetIndirectCostFormHelper.getPeriodTotal(i);
            
            Element institutionIndirectCostSharePeriodElement = xmlDoc.createElement("INSTITUTION_INDIRECT_COST_SHARE_PERIOD");
            institutionIndirectCostSharePeriodElement.setAttribute("PERIOD_NUMBER", Integer.toString(i + 1));

            Element indirectCostOnCostShareElement = xmlDoc.createElement("INDIRECT_COST_ON_COST_SHARE");
            indirectCostOnCostShareElement.appendChild(xmlDoc.createTextNode(periodTotal.getCostShareCalculatedIndirectCost().toString()));
            institutionIndirectCostSharePeriodElement.appendChild(indirectCostOnCostShareElement);

            Element unrecoveredIndirectCostElement = xmlDoc.createElement("UNRECOVERED_INDIRECT_COST");
            unrecoveredIndirectCostElement.appendChild(xmlDoc.createTextNode(periodTotal.getCostShareUnrecoveredIndirectCost().toString()));
            institutionIndirectCostSharePeriodElement.appendChild(unrecoveredIndirectCostElement);

            institutionIndirectCostShareElement.appendChild(institutionIndirectCostSharePeriodElement);
        }

        return institutionIndirectCostShareElement;
    }

    /**
     * Creates THIRD_PARTY_COST_SHARE node.
     * 
     * @param budgetCostShareFormHelper
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createThirdPartyCostShareElement(BudgetCostShareFormHelper budgetCostShareFormHelper, Budget budget, Document xmlDoc) {
        Element thirdPartyCostShareElement = xmlDoc.createElement("THIRD_PARTY_COST_SHARE");

        Element thirdPartyCostSharePeriodsElement = xmlDoc.createElement("THIRD_PARTY_COST_SHARE_PERIODS");
        thirdPartyCostSharePeriodsElement.setAttribute("THIRD_PARTY_TOTAL_BUDGETED", budgetCostShareFormHelper.getThirdPartyDirect().getTotalTotalBudgeted().toString());
        thirdPartyCostSharePeriodsElement.setAttribute("THIRD_PARTY_AMOUNT_DISTRIBUTED", budgetCostShareFormHelper.getThirdPartyDirect().getTotalAmountDistributed().toString());
        thirdPartyCostSharePeriodsElement.setAttribute("THIRD_PARTY_BALANCE", budgetCostShareFormHelper.getThirdPartyDirect().getTotalBalanceToBeDistributed().toString());
        
        for (int i = 0; i < budget.getPeriods().size(); i++) {
            Element thirdPartyCostSharePeriodElement = xmlDoc.createElement("THIRD_PARTY_COST_SHARE_PERIOD");
            thirdPartyCostSharePeriodElement.setAttribute("PERIOD_NUMBER", Integer.toString(i + 1));

            Element thirdPartyTotalBudgetedElement = xmlDoc.createElement("THIRD_PARTY_TOTAL_BUDGETED");
            thirdPartyTotalBudgetedElement.appendChild(xmlDoc.createTextNode(budgetCostShareFormHelper.getThirdPartyDirect().getTotalBudgeted()[i].toString()));
            thirdPartyCostSharePeriodElement.appendChild(thirdPartyTotalBudgetedElement);

            Element thirdPartyAmountDistributedElement = xmlDoc.createElement("THIRD_PARTY_AMOUNT_DISTRIBUTED");
            thirdPartyAmountDistributedElement.appendChild(xmlDoc.createTextNode(budgetCostShareFormHelper.getThirdPartyDirect().getAmountDistributed()[i].toString()));
            thirdPartyCostSharePeriodElement.appendChild(thirdPartyAmountDistributedElement);

            Element thirdPartyBalanceElement = xmlDoc.createElement("THIRD_PARTY_BALANCE");
            thirdPartyBalanceElement.appendChild(xmlDoc.createTextNode(budgetCostShareFormHelper.getThirdPartyDirect().getBalanceToBeDistributed()[i].toString()));
            thirdPartyCostSharePeriodElement.appendChild(thirdPartyBalanceElement);

            thirdPartyCostSharePeriodsElement.appendChild(thirdPartyCostSharePeriodElement);
        }
        thirdPartyCostShareElement.appendChild(thirdPartyCostSharePeriodsElement);

        Element thirdPartyCostShareSourcesElement = xmlDoc.createElement("THIRD_PARTY_COST_SHARE_SOURCES");
        for (int i = 0; i < budget.getThirdPartyCostShareItems().size(); i++) {
            BudgetThirdPartyCostShare budgetThirdPartyCostShare = budget.getThirdPartyCostShareItem(i);

            Element thirdPartyCostShareSourceElement = xmlDoc.createElement("THIRD_PARTY_COST_SHARE_SOURCE");
            thirdPartyCostShareSourceElement.setAttribute("SOURCE", budgetThirdPartyCostShare.getBudgetCostShareSourceName());
            thirdPartyCostShareSourceElement.setAttribute("TOTAL", budgetCostShareFormHelper.getThirdPartyDirect().getTotalSource()[i].toString());

            Iterator innerIter = budgetThirdPartyCostShare.getBudgetPeriodCostShare().iterator();
            for (int j = 0; innerIter.hasNext(); j++) {
                BudgetPeriodThirdPartyCostShare periodThirdPartyCostShare = (BudgetPeriodThirdPartyCostShare) innerIter.next();

                Element thirdPartyCostSharePeriodAmount = xmlDoc.createElement("THIRD_PARTY_COST_SHARE_PERIOD_AMOUNT");
                thirdPartyCostSharePeriodAmount.setAttribute("PERIOD_NUMBER", Integer.toString(j + 1));
                thirdPartyCostSharePeriodAmount.appendChild(xmlDoc.createTextNode(periodThirdPartyCostShare.getBudgetCostShareAmount().toString()));

                thirdPartyCostShareSourcesElement.appendChild(thirdPartyCostSharePeriodAmount);
            }
            thirdPartyCostShareSourcesElement.appendChild(thirdPartyCostShareSourceElement);
        }
        thirdPartyCostShareElement.appendChild(thirdPartyCostShareSourcesElement);

        return thirdPartyCostShareElement;
    }

    /**
     * Creates SUBCONTRACTOR_COST_SHARE node.
     * 
     * @param budgetCostShareFormHelper
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createSubcontractorCostShareElement(BudgetCostShareFormHelper budgetCostShareFormHelper, Budget budget, Document xmlDoc) {
        Element subcontractorCostShareElement = xmlDoc.createElement("SUBCONTRACTOR_COST_SHARE");
        Element subcontractorCostShareSourcesElement = xmlDoc.createElement("SUBCONTRACTOR_COST_SHARE_SOURCES");

        for (BudgetCostShareFormHelper.Subcontractor subcontractor : budgetCostShareFormHelper.getSubcontractors()) {
            Element subconstractorCostShareSource = xmlDoc.createElement("SUBCONTRACTOR_COST_SHARE_SOURCE");

            subconstractorCostShareSource.setAttribute("NAME", subcontractor.getBudgetNonpersonnelDescription());
            subconstractorCostShareSource.setAttribute("TOTAL", subcontractor.getTotalPeriodAmount().toString());

            for (int i = 0; i < subcontractor.getPeriodAmounts().length; i++) {
                Element subcontractorSourcePeriod = xmlDoc.createElement("SUBCONTRACTOR_SOURCE_PERIOD");
                subcontractorSourcePeriod.setAttribute("PERIOD_NUMBER", Integer.toString(i + 1));

                Element subcontractorAmount = xmlDoc.createElement("SUBCONTRACTOR_AMOUNT");
                subcontractorAmount.appendChild(xmlDoc.createTextNode(subcontractor.getPeriodAmounts()[i].toString()));
                subcontractorSourcePeriod.appendChild(subcontractorAmount);

                subconstractorCostShareSource.appendChild(subcontractorSourcePeriod);
            }

            subcontractorCostShareSourcesElement.appendChild(subconstractorCostShareSource);
        }

        subcontractorCostShareElement.appendChild(subcontractorCostShareSourcesElement);

        return subcontractorCostShareElement;
    }

    /**
     * Creates INFLATION_RATES node.
     * 
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createInflationRatesElement(Budget budget, Document xmlDoc) {
        Element inflationRatesElement = xmlDoc.createElement("INFLATION_RATES");

        inflationRatesElement.setAttribute("PERSONNEL", ObjectUtils.toString(budget.getBudgetPersonnelInflationRate().toString()));

        inflationRatesElement.setAttribute("NON_PERSONNEL", ObjectUtils.toString(budget.getBudgetNonpersonnelInflationRate().toString()));

        return inflationRatesElement;
    }

    /**
     * Creates TASK_PERIODS node.
     * 
     * @param nonpersonnelCategories
     * @param budgetIndirectCostFormHelper
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createTaskPeriodsElement(List nonpersonnelCategories, BudgetIndirectCostFormHelper budgetIndirectCostFormHelper, Budget budget, Document xmlDoc) {
        Element taskPeriodsElement = xmlDoc.createElement("TASK_PERIODS");

        for (int i = 0; i < budget.getTasks().size(); i++) {
            BudgetTask currentTask = (BudgetTask) budget.getTasks().get(i);

            for (int j = 0; j < budget.getPeriods().size(); j++) {
                BudgetPeriod currentPeriod = (BudgetPeriod) budget.getPeriods().get(j);

                Element taskPeriodElement = xmlDoc.createElement("TASK_PERIOD");
                taskPeriodElement.setAttribute("TASK_NUMBER", Integer.toString(i + 1));
                taskPeriodElement.setAttribute("PERIOD_NUMBER", "" + Integer.toString(j + 1));

                // Collect necessary data based on current task / period.
                BudgetOverviewFormHelper budgetOverviewFormHelper = new BudgetOverviewFormHelper();
                BudgetNonpersonnelFormHelper budgetNonpersonnelFormHelper = budgetOverviewFormHelper.recalculate(currentTask.getBudgetTaskSequenceNumber(), currentPeriod.getBudgetPeriodSequenceNumber(), nonpersonnelCategories, budgetIndirectCostFormHelper, budget);

                taskPeriodElement.setAttribute("TOTAL_AGENCY_REQUEST_DIRECT_COST", budgetOverviewFormHelper.getTotalDirectCostsAgencyRequest().toString());
                taskPeriodElement.setAttribute("TOTAL_INSTITUTION_DIRECT_COST", budgetOverviewFormHelper.getTotalDirectCostsInstitutionCostShare().toString());
                taskPeriodElement.setAttribute("TOTAL_THIRD_PARTY_DIRECT_COST", budgetOverviewFormHelper.getTotalDirectThirdPartyCostShare().toString());

                taskPeriodElement.setAttribute("TOTAL_AGENCY_REQUEST_INDIRECT_COST", budgetOverviewFormHelper.getTotalIndirectCostsAgencyRequest().toString());
                taskPeriodElement.setAttribute("TOTAL_INSTITUTION_INDIRECT_COST", budgetOverviewFormHelper.getTotalIndirectCostsInstitutionCostShare().toString());
                taskPeriodElement.setAttribute("TOTAL_INSTITUTION_UNRECOVERED_INDIRECT_COST", budgetOverviewFormHelper.getTotalIndirectCostsInstitutionCostShareUnrecovered().toString());
                
                // Not so sure why this exists as there doesn't appear to be a third party Indirect Cost. Maybe legacy XSLT support?
                taskPeriodElement.setAttribute("TOTAL_THIRD_PARTY_INDIRECT_COST", "0");

                taskPeriodElement.setAttribute("TOTAL_AGENCY_COST", budgetOverviewFormHelper.getTotalCostsAgencyRequest().toString());
                taskPeriodElement.setAttribute("TOTAL_INSTITUTION_COST", budgetOverviewFormHelper.getTotalCostsInstitutionCostShare().toString());
                taskPeriodElement.setAttribute("TOTAL_THIRD_PARTY_COST", budgetOverviewFormHelper.getTotalCostsThirdPartyCostShare().toString());

                taskPeriodElement.appendChild(setupPersonnel(budgetOverviewFormHelper, xmlDoc));
                taskPeriodElement.appendChild(setupNonPersonnel(currentTask, currentPeriod, budgetNonpersonnelFormHelper, budget, xmlDoc));

                taskPeriodsElement.appendChild(taskPeriodElement);
            }
        }

        return taskPeriodsElement;
    }

    /**
     * Creates PERSONNEL node.
     * 
     * @param budgetOverviewFormHelper
     * @param xmlDoc
     * @return resulting node
     */
    private static Element setupPersonnel(BudgetOverviewFormHelper budgetOverviewFormHelper, Document xmlDoc) {
        Element personnelElement = xmlDoc.createElement("PERSONNEL");

        List<BudgetOverviewPersonnelHelper> budgetOverviewPersonnelHelpers = budgetOverviewFormHelper.getBudgetOverviewPersonnelHelpers();
        for (BudgetOverviewPersonnelHelper budgetOverviewPersonnelHelper : budgetOverviewPersonnelHelpers) {
            Element personElement = xmlDoc.createElement("PERSON");

            Element nameElement = xmlDoc.createElement("NAME");
            nameElement.appendChild(xmlDoc.createTextNode(budgetOverviewPersonnelHelper.getPersonName()));
            personElement.appendChild(nameElement);

            Element roleElement = xmlDoc.createElement("ROLE");
            roleElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(budgetOverviewPersonnelHelper.getRole())));
            personElement.appendChild(roleElement);

            Element appointmentElement = xmlDoc.createElement("APPOINTMENT");
            appointmentElement.setAttribute("APPOINTMENT_TYPE", budgetOverviewPersonnelHelper.getAppointmentTypeDescription());
            appointmentElement.setAttribute("APPOINTMENT_CODE", budgetOverviewPersonnelHelper.getUniversityAppointmentTypeCode());
            personElement.appendChild(appointmentElement);

            personElement.setAttribute("PROJECT_DIRECTOR", ObjectUtils.toString(budgetOverviewPersonnelHelper.isPersonProjectDirectorIndicator()).toUpperCase());
            
            // CREATE_TIMESTAMP was dropped in KRA, it is replaced with this field
            personElement.setAttribute("SEQUENCE_NUMBER", ObjectUtils.toString(budgetOverviewPersonnelHelper.getBudgetUserSequenceNumber()));

            personElement.setAttribute("PERIOD_SALARY", budgetOverviewPersonnelHelper.getUserBudgetPeriodSalaryAmount());
            personElement.setAttribute("SUMMER_WEEKS", budgetOverviewPersonnelHelper.getPersonWeeksAmount() == null ? "0" : ObjectUtils.toString(budgetOverviewPersonnelHelper.getPersonWeeksAmount()));

            personElement.setAttribute("AGENCY_PERCENT_SALARY", ObjectUtils.toString(budgetOverviewPersonnelHelper.getAgencyPercentEffortAmount()) + OUTPUT_PERCENT_SYMBOL);
            personElement.setAttribute("AGENCY_HOURS", ObjectUtils.toString(budgetOverviewPersonnelHelper.getUserAgencyHours()));
            personElement.setAttribute("AGENCY_AMOUNT_SALARY", ObjectUtils.toString(budgetOverviewPersonnelHelper.getAgencyRequestTotalAmount()));
            personElement.setAttribute("INSTITUTION_PERCENT_SALARY", ObjectUtils.toString(budgetOverviewPersonnelHelper.getUniversityCostSharePercentEffortAmount()) + OUTPUT_PERCENT_SYMBOL);
            personElement.setAttribute("INSTITUTION_HOURS", ObjectUtils.toString(budgetOverviewPersonnelHelper.getUserUniversityHours()));
            personElement.setAttribute("INSTITUTION_AMOUNT_SALARY", ObjectUtils.toString(budgetOverviewPersonnelHelper.getUniversityCostShareRequestTotalAmount()));
            personElement.setAttribute("AGENCY_FRINGE_BENEFIT_RATE", ObjectUtils.toString(budgetOverviewPersonnelHelper.getContractsAndGrantsFringeRateAmount()) + OUTPUT_PERCENT_SYMBOL);
            personElement.setAttribute("AGENCY_FRINGE_BENEFIT_AMOUNT", ObjectUtils.toString(budgetOverviewPersonnelHelper.getAgencyFringeBenefitTotalAmount()));
            personElement.setAttribute("INSTITUTION_FRINGE_BENEFIT_RATE", ObjectUtils.toString(budgetOverviewPersonnelHelper.getUniversityCostShareFringeRateAmount()) + OUTPUT_PERCENT_SYMBOL);
            personElement.setAttribute("INSTITUTION_FRINGE_BENEFIT_AMOUNT", ObjectUtils.toString(budgetOverviewPersonnelHelper.getUniversityCostShareFringeBenefitTotalAmount()));

            // Following calculation should probably be somewhere else.
            /** TODO Create App Constants for the below or move into Personnel? Does it already exist there? */
            KualiInteger agencyPercentEffortAmount = budgetOverviewPersonnelHelper.getAgencyPercentEffortAmount() == null ? new KualiInteger(0) : budgetOverviewPersonnelHelper.getAgencyPercentEffortAmount();
            KualiInteger universityCostSharePercentEffortAmount = budgetOverviewPersonnelHelper.getUniversityCostSharePercentEffortAmount() == null ? new KualiInteger(0) : budgetOverviewPersonnelHelper.getUniversityCostSharePercentEffortAmount();
            BigDecimal combinedPercentEffort = agencyPercentEffortAmount.add(universityCostSharePercentEffortAmount).divide(new KualiInteger(100));
            String calendarMonths = "";
            String academicMonths = "";
            String summerMonths = "";
            if (budgetOverviewFormHelper.FULL_YEAR_APPOINTMENTS.contains(budgetOverviewPersonnelHelper.getUniversityAppointmentTypeCode()) || budgetOverviewFormHelper.GRADUATE_RA_APPOINTMENTS.contains(budgetOverviewPersonnelHelper.getUniversityAppointmentTypeCode())) {
                BigDecimal personMonths = new BigDecimal(12 * combinedPercentEffort.doubleValue()).setScale(1, BigDecimal.ROUND_HALF_DOWN);
                calendarMonths = personMonths.toString();
            }
            else if (budgetOverviewFormHelper.SUMMER_GRID_APPOINTMENT.contains(budgetOverviewPersonnelHelper.getUniversityAppointmentTypeCode())) {
                // AS
                BigDecimal personMonths = new BigDecimal(3 * combinedPercentEffort.doubleValue()).setScale(1, BigDecimal.ROUND_HALF_DOWN);
                summerMonths = personMonths.toString();
            }
            else if (budgetOverviewFormHelper.SUMMER_GRID_APPOINTMENTS.contains(budgetOverviewPersonnelHelper.getUniversityAppointmentTypeCode())) {
                // A2 & AS, note that AS got caught above though and it should. Just trying to avoid creating another application constant
                // as personnel already uses this one.
                BigDecimal personMonths = new BigDecimal(9 * combinedPercentEffort.doubleValue()).setScale(1, BigDecimal.ROUND_HALF_DOWN);
                academicMonths = personMonths.toString();
            }
            else if (budgetOverviewFormHelper.HOURLY_APPOINTMENTS.contains(budgetOverviewPersonnelHelper.getUniversityAppointmentTypeCode())) {
                KualiInteger totalsHours = budgetOverviewPersonnelHelper.getUserAgencyHours().add(budgetOverviewPersonnelHelper.getUserUniversityHours());
                
                // 173.33 = 2080 hours per year / 12 months
                calendarMonths = "" + totalsHours.divide(new BigDecimal(173.33)).setScale(1, BigDecimal.ROUND_HALF_DOWN);
            }
            personElement.setAttribute("CALENDAR_MONTHS", calendarMonths);
            personElement.setAttribute("ACADEMIC_MONTHS", academicMonths);
            personElement.setAttribute("SUMMER_MONTHS", summerMonths);

            personnelElement.appendChild(personElement);
        }

        personnelElement.setAttribute("TOTAL_AGENCY_SALARY", budgetOverviewFormHelper.getPersonnelSalaryAgencyRequest().toString());
        personnelElement.setAttribute("TOTAL_AGENCY_FRINGE_BENEFITS", budgetOverviewFormHelper.getPersonnelFringeBenefitsAgencyRequest().toString());
        personnelElement.setAttribute("TOTAL_AGENCY_PERSONNEL", budgetOverviewFormHelper.getTotalPersonnelAgencyRequest().toString());
        personnelElement.setAttribute("TOTAL_INSTITUTION_SALARY", budgetOverviewFormHelper.getPersonnelSalaryInstitutionCostShare().toString());
        personnelElement.setAttribute("TOTAL_INSTITUTION_FRINGE_BENEFITS", budgetOverviewFormHelper.getPersonnelFringeBenefitsInstitutionCostShare().toString());
        personnelElement.setAttribute("TOTAL_INSTITUTION_PERSONNEL", budgetOverviewFormHelper.getTotalPersonnelInstitutionCostShare().toString());

        return personnelElement;
    }

    /**
     * Creates NON_PERSONNEL node.
     * 
     * @param currentTask
     * @param currentPeriod
     * @param budgetNonpersonnelFormHelper
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element setupNonPersonnel(BudgetTask currentTask, BudgetPeriod currentPeriod, BudgetNonpersonnelFormHelper budgetNonpersonnelFormHelper, Budget budget, Document xmlDoc) {
        Element nonpersonnelElement = xmlDoc.createElement("NON_PERSONNEL");
        nonpersonnelElement.setAttribute("TOTAL_AGENCY_REQUESTED_AMOUNT", budgetNonpersonnelFormHelper.getNonpersonnelAgencyTotal().toString());
        nonpersonnelElement.setAttribute("TOTAL_INSTITUTION_COST_SHARE_AMOUNT", budgetNonpersonnelFormHelper.getNonpersonnelUnivCostShareTotal().toString());
        nonpersonnelElement.setAttribute("TOTAL_THIRD_PARTY_COST_SHARE_AMOUNT", budgetNonpersonnelFormHelper.getNonpersonnelThirdPartyCostShareTotal().toString());

        for (Iterator iter = budget.getNonpersonnelItems().iterator(); iter.hasNext();) {
            BudgetNonpersonnel currentItem = (BudgetNonpersonnel) iter.next();

            if (currentPeriod.getBudgetPeriodSequenceNumber().equals(currentItem.getBudgetPeriodSequenceNumber()) && currentTask.getBudgetTaskSequenceNumber().equals(currentItem.getBudgetTaskSequenceNumber())) {
                Element nonpersonnelItemElement = xmlDoc.createElement("NON_PERSONNEL_ITEM");

                Element categoryElement = xmlDoc.createElement("CATEGORY");
                categoryElement.appendChild(xmlDoc.createTextNode(currentItem.getNonpersonnelObjectCode().getNonpersonnelCategory().getName()));
                nonpersonnelItemElement.appendChild(categoryElement);

                Element subCategoryElement = xmlDoc.createElement("SUB_CATEGORY");
                subCategoryElement.appendChild(xmlDoc.createTextNode(currentItem.getNonpersonnelObjectCode().getNonpersonnelSubCategory().getName()));
                nonpersonnelItemElement.appendChild(subCategoryElement);

                Element descriptionElement = xmlDoc.createElement("DESCRIPTION");
                descriptionElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(currentItem.getBudgetNonpersonnelDescription())));
                nonpersonnelItemElement.appendChild(descriptionElement);

                Element agencyRequestAmountElement = xmlDoc.createElement("AGENCY_REQUEST_AMOUNT");
                agencyRequestAmountElement.appendChild(xmlDoc.createTextNode(currentItem.getAgencyRequestAmount().toString()));
                nonpersonnelItemElement.appendChild(agencyRequestAmountElement);

                Element institutionCostShareAmountElement = xmlDoc.createElement("INSTITUTION_COST_SHARE_AMOUNT");
                institutionCostShareAmountElement.appendChild(xmlDoc.createTextNode(currentItem.getBudgetUniversityCostShareAmount().toString()));
                nonpersonnelItemElement.appendChild(institutionCostShareAmountElement);

                Element thirdPartyCostShareAmountElement = xmlDoc.createElement("THIRD_PARTY_COST_SHARE_AMOUNT");
                thirdPartyCostShareAmountElement.appendChild(xmlDoc.createTextNode(currentItem.getBudgetThirdPartyCostShareAmount().toString()));
                nonpersonnelItemElement.appendChild(thirdPartyCostShareAmountElement);

                nonpersonnelElement.appendChild(nonpersonnelItemElement);
            }
        }
        return nonpersonnelElement;
    }

    /**
     * Creates INDIRECT_COST node.
     * 
     * @param budgetIndirectCostFormHelper
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createIndirectCostElement(BudgetIndirectCostFormHelper budgetIndirectCostFormHelper, Budget budget, Document xmlDoc) {
        Element indirectCostElement = xmlDoc.createElement("INDIRECT_COST");
        indirectCostElement.setAttribute("BASE_TYPE", budget.getIndirectCost().getBudgetBaseCode());
        if (KraConstants.DATABASE_TRUE_VALUE.equals(budget.getIndirectCost().getBudgetManualRateIndicator())) {
            indirectCostElement.setAttribute("IDC_RATE", "MANUAL");
        }
        else {
            indirectCostElement.setAttribute("IDC_RATE", "SYSTEM");
        }

        for (int i = 0; i < budget.getTasks().size(); i++) {
            Element indirectCostTaskElement = xmlDoc.createElement("INDIRECT_COST_TASK");
            indirectCostTaskElement.setAttribute("TASK_NUMBER", Integer.toString(i + 1));

            for (int j = 0; j < budget.getPeriods().size(); j++) {
                Element indirectCostTaskPeriodElement = xmlDoc.createElement("INDIRECT_COST_TASK_PERIOD");
                indirectCostTaskPeriodElement.setAttribute("PERIOD_NUMBER", Integer.toString(j + 1));

                // Pick the correct task / period item.
                BudgetTaskPeriodIndirectCost indirectCostItem = null;
                for (Iterator indirectCostItemsIter = budget.getIndirectCost().getBudgetTaskPeriodIndirectCostItems().iterator(); indirectCostItemsIter.hasNext();) {
                    indirectCostItem = (BudgetTaskPeriodIndirectCost) indirectCostItemsIter.next();

                    if (indirectCostItem.getBudgetTaskSequenceNumber().equals(budget.getTask(i).getBudgetTaskSequenceNumber())
                            && indirectCostItem.getBudgetPeriodSequenceNumber().equals(budget.getPeriod(j).getBudgetPeriodSequenceNumber())) {
                        break;
                    }
                }

                indirectCostTaskPeriodElement.setAttribute("RATE", ObjectUtils.toString(indirectCostItem.getIndirectCostRate()) + OUTPUT_PERCENT_SYMBOL);

                createIndirectCostElementHelper("INDIRECT_COST_TASK_PERIOD_AGENCY_AMOUNT", "INDIRECT_COST_TASK_PERIOD_INSTITUTION", xmlDoc, indirectCostTaskPeriodElement, indirectCostItem);

                indirectCostTaskElement.appendChild(indirectCostTaskPeriodElement);
            }

            Element indirectCostTaskSubtotalElement = xmlDoc.createElement("INDIRECT_COST_TASK_SUBTOTAL");

            createIndirectCostElementHelper("INDIRECT_COST_TASK_SUBTOTAL_AGENCY_AMOUNT", "INDIRECT_COST_TASK_SUBTOTAL_INSTITUTION", xmlDoc, indirectCostTaskSubtotalElement, (BudgetTaskPeriodIndirectCost) budgetIndirectCostFormHelper.getTaskTotals().get(i));

            indirectCostTaskElement.appendChild(indirectCostTaskSubtotalElement);
            indirectCostElement.appendChild(indirectCostTaskElement);
        }

        Element indirectCostSummaryElement = xmlDoc.createElement("INDIRECT_COST_SUMMARY");

        for (int i = 0; i < budget.getPeriods().size(); i++) {
            Element indirectCostSummaryPeriodElement = xmlDoc.createElement("INDIRECT_COST_SUMMARY_PERIOD");
            indirectCostSummaryPeriodElement.setAttribute("PERIOD_NUMBER", Integer.toString(i + 1));

            createIndirectCostElementHelper("INDIRECT_COST_SUMMARY_PERIOD_AGENCY_AMOUNT", "INDIRECT_COST_SUMMARY_PERIOD_INSTITUTION", xmlDoc, indirectCostSummaryPeriodElement, (BudgetTaskPeriodIndirectCost) budgetIndirectCostFormHelper.getPeriodTotals().get(i));

            indirectCostSummaryElement.appendChild(indirectCostSummaryPeriodElement);
        }

        Element indirectCostSummaryTotalElement = xmlDoc.createElement("INDIRECT_COST_SUMMARY_TOTAL");

        createIndirectCostElementHelper("INDIRECT_COST_SUMMARY_TOTAL_AGENCY_AMOUNT", "INDIRECT_COST_SUMMARY_TOTAL_INSTITUTION", xmlDoc, indirectCostSummaryTotalElement, budgetIndirectCostFormHelper.getPeriodSubTotal());

        indirectCostSummaryElement.appendChild(indirectCostSummaryTotalElement);
        indirectCostElement.appendChild(indirectCostSummaryElement);

        return indirectCostElement;
    }

    /**
     * Helper for INDIRECT_COST node (createIndirectCostElement). Side effecting on indirectCostTaskPeriodElement argument.
     * 
     * @param agencyAmountLabel
     * @param institutionAmountLabel
     * @param xmlDoc
     * @param indirectCostTaskPeriodElement
     * @param indirectCostItem
     */
    private static void createIndirectCostElementHelper(String agencyAmountLabel, String institutionAmountLabel, Document xmlDoc, Element indirectCostTaskPeriodElement, BudgetTaskPeriodIndirectCost indirectCostItem) {
        Element indirectCostTaskPeriodAgencyAmountElement = xmlDoc.createElement(agencyAmountLabel);

        Element tdcElement = xmlDoc.createElement("TDC");
        tdcElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(indirectCostItem.getTotalDirectCost())));
        indirectCostTaskPeriodAgencyAmountElement.appendChild(tdcElement);

        Element agencyBaseElement = xmlDoc.createElement("AGENCY_BASE");
        agencyBaseElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(indirectCostItem.getBaseCost())));
        indirectCostTaskPeriodAgencyAmountElement.appendChild(agencyBaseElement);

        Element agencyCalculatedIndirectCostElement = xmlDoc.createElement("AGENCY_CALCULATED_INDIRECT_COST");
        agencyCalculatedIndirectCostElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(indirectCostItem.getCalculatedIndirectCost())));
        indirectCostTaskPeriodAgencyAmountElement.appendChild(agencyCalculatedIndirectCostElement);

        indirectCostTaskPeriodElement.appendChild(indirectCostTaskPeriodAgencyAmountElement);

        Element indirectCostTaskPeriodInstitutionElement = xmlDoc.createElement(institutionAmountLabel);

        Element institutionBaseElement = xmlDoc.createElement("INSTITUTION_BASE");
        institutionBaseElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(indirectCostItem.getCostShareBaseCost())));
        indirectCostTaskPeriodInstitutionElement.appendChild(institutionBaseElement);

        Element institutionCalculatedIndirectCostElement = xmlDoc.createElement("INSTITUTION_CALCULATED_INDIRECT_COST");
        institutionCalculatedIndirectCostElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(indirectCostItem.getCostShareCalculatedIndirectCost())));
        indirectCostTaskPeriodInstitutionElement.appendChild(institutionCalculatedIndirectCostElement);

        Element unrecoveredIndirectCostElement = xmlDoc.createElement("UNRECOVERED_INDIRECT_COST");
        unrecoveredIndirectCostElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(indirectCostItem.getCostShareUnrecoveredIndirectCost())));
        indirectCostTaskPeriodInstitutionElement.appendChild(unrecoveredIndirectCostElement);

        indirectCostTaskPeriodElement.appendChild(indirectCostTaskPeriodInstitutionElement);
    }

    /**
     * Creates MODULAR_BUDGET node.
     * 
     * @param budget
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createModularBudgetElement(Budget budget, Document xmlDoc) {
        Element modularBudgetElement = xmlDoc.createElement("MODULAR_BUDGET");

        if (budget.isAgencyModularIndicator()) {
            BudgetModular budgetModular = (BudgetModular) budget.getModularBudget();

            Element modularBudgetPeriodsElement = xmlDoc.createElement("MODULAR_BUDGET_PERIODS");
            Iterator budgetModularIter = budgetModular.getBudgetModularPeriods().iterator();
            for (int i = 0; budgetModularIter.hasNext(); i++) {
                BudgetModularPeriod budgetModularPeriod = (BudgetModularPeriod) budgetModularIter.next();
                Element modularBudgetPeriod = xmlDoc.createElement("MODULAR_BUDGET_PERIOD");
                modularBudgetPeriod.setAttribute("PERIOD_NUMBER", Integer.toString(i + 1));

                Element modularDirectCost = xmlDoc.createElement("MODULAR_DIRECT_COST");
                modularDirectCost.appendChild(xmlDoc.createTextNode(budgetModularPeriod.getActualDirectCostAmount().toString()));
                modularBudgetPeriod.appendChild(modularDirectCost);

                Element adjustedModularDirectCost = xmlDoc.createElement("ADJUSTED_MODULAR_DIRECT_COST");
                adjustedModularDirectCost.appendChild(xmlDoc.createTextNode(budgetModularPeriod.getBudgetAdjustedModularDirectCostAmount().toString()));
                modularBudgetPeriod.appendChild(adjustedModularDirectCost);

                Element modularAdjustment = xmlDoc.createElement("MODULAR_ADJUSTMENT");
                modularAdjustment.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(budgetModularPeriod.getModularVarianceAmount())));
                modularBudgetPeriod.appendChild(modularAdjustment);

                modularBudgetPeriodsElement.appendChild(modularBudgetPeriod);
            }
            modularBudgetElement.appendChild(modularBudgetPeriodsElement);

            Element modularBudgetDescriptionsElement = xmlDoc.createElement("MODULAR_BUDGET_DESCRIPTIONS");

            Element modularBudgetPersonnelDescriptionElement = xmlDoc.createElement("MODULAR_BUDGET_PERSONNEL_DESCRIPTION");
            modularBudgetPersonnelDescriptionElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(budgetModular.getBudgetModularPersonnelDescription())));
            modularBudgetDescriptionsElement.appendChild(modularBudgetPersonnelDescriptionElement);

            Element modularBudgetConsortiumDescriptionElement = xmlDoc.createElement("MODULAR_BUDGET_CONSORTIUM_DESCRIPTION");
            modularBudgetConsortiumDescriptionElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(budgetModular.getBudgetModularConsortiumDescription())));
            modularBudgetDescriptionsElement.appendChild(modularBudgetConsortiumDescriptionElement);

            Element modularBudgetVariableAdjustmentDescriptionElement = xmlDoc.createElement("MODULAR_BUDGET_VARIABLE_ADJUSTMENT_DESCRIPTION");
            modularBudgetVariableAdjustmentDescriptionElement.appendChild(xmlDoc.createTextNode(ObjectUtils.toString(budgetModular.getBudgetModularVariableAdjustmentDescription())));
            modularBudgetDescriptionsElement.appendChild(modularBudgetVariableAdjustmentDescriptionElement);

            modularBudgetElement.appendChild(modularBudgetDescriptionsElement);
        }

        return modularBudgetElement;
    }
}
