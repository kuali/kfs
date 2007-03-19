/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetInstitutionCostShare;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.BudgetPeriodInstitutionCostShare;
import org.kuali.module.kra.budget.bo.BudgetPeriodThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.UserAppointmentTask;
import org.kuali.module.kra.budget.bo.UserAppointmentTaskPeriod;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.web.struts.form.BudgetOverviewFormHelper;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormOtherCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.bo.RoutingFormSubcontractor;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.service.RoutingFormService;

import edu.iu.uis.eden.exception.WorkflowException;

public class RoutingFormServiceImpl implements RoutingFormService {

    private DocumentService documentService;
    
    public void initializeRoutingForm(RoutingFormDocument routingFormDocument) throws WorkflowException {
        //TODO
    }

    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormService#prepareRoutingFormForSave(org.kuali.module.kra.routingform.document.RoutingFormDocument)
     */
    public void prepareRoutingFormForSave(RoutingFormDocument routingFormDocument) throws WorkflowException {
        // TODO
    }

    public BudgetDocument retrieveBudgetForLinking(String budgetDocumentNumber) throws WorkflowException {
        Document document = documentService.getByDocumentHeaderId(budgetDocumentNumber);

        if (document != null && document instanceof BudgetDocument) {
            return (BudgetDocument)document;
        } else {
            return null;
        }
    }
    
    public void linkImportBudgetDataToRoutingForm(RoutingFormDocument routingFormDocument, String budgetDocumentHeaderId, List<BudgetOverviewFormHelper> periodOverviews) throws WorkflowException {
        
        KualiConfigurationService kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
        final String PERSON_ROLE_CODE_PD = kualiConfigurationService.getApplicationParameterValue(KraConstants.KRA_DEVELOPMENT_GROUP, "KraRoutingFormPersonRoleCodeProjectDirector");       
        
        Integer minPeriod = routingFormDocument.getRoutingFormBudget().getRoutingFormBudgetMinimumPeriodNumber();
        Integer maxPeriod = routingFormDocument.getRoutingFormBudget().getRoutingFormBudgetMaximumPeriodNumber();
        
        BudgetDocument budgetDocument = retrieveBudgetForLinking(budgetDocumentHeaderId);
        
        Budget budget = budgetDocument.getBudget();

        //clear out lists that are going to be re-populated with Budget data.
        routingFormDocument.getRoutingFormInstitutionCostShares().clear();
        routingFormDocument.getRoutingFormOtherCostShares().clear();
        routingFormDocument.getRoutingFormSubcontractors().clear();
        
        RoutingFormBudget routingFormBudget =  routingFormDocument.getRoutingFormBudget();
        routingFormBudget.resetRoutingFormBudgetData();
        
        for (BudgetOverviewFormHelper periodOverview : periodOverviews) {
            if (routingFormBudget.getRoutingFormBudgetTotalStartDate() == null) {
                routingFormBudget.setRoutingFormBudgetTotalStartDate(periodOverview.getBudgetPeriod().getBudgetPeriodBeginDate());
            }
            routingFormBudget.setRoutingFormBudgetTotalEndDate(periodOverview.getBudgetPeriod().getBudgetPeriodEndDate());
            routingFormBudget.setRoutingFormBudgetTotalIndirectCostAmount(routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount().add(periodOverview.getTotalIndirectCostsAgencyRequest()));
            
            if(!periodOverview.isOverviewShowModular()) {
                routingFormBudget.setRoutingFormBudgetTotalDirectAmount(routingFormBudget.getRoutingFormBudgetTotalDirectAmount().add(periodOverview.getTotalDirectCostsAgencyRequest()));
            } else {
                routingFormBudget.setRoutingFormBudgetTotalDirectAmount(routingFormBudget.getRoutingFormBudgetTotalDirectAmount().add(periodOverview.getAdjustedDirectCostsAgencyRequest()));
            }
            
            if (periodOverview.isSelected()) {
                if (routingFormBudget.getRoutingFormBudgetStartDate() == null) {
                    routingFormBudget.setRoutingFormBudgetStartDate(periodOverview.getBudgetPeriod().getBudgetPeriodBeginDate());
                }
                routingFormBudget.setRoutingFormBudgetEndDate(periodOverview.getBudgetPeriod().getBudgetPeriodEndDate());
                routingFormBudget.setRoutingFormBudgetIndirectCostAmount(routingFormBudget.getRoutingFormBudgetIndirectCostAmount().add(periodOverview.getTotalIndirectCostsAgencyRequest()));
                
                if(!periodOverview.isOverviewShowModular()) {                
                    routingFormBudget.setRoutingFormBudgetDirectAmount(routingFormBudget.getRoutingFormBudgetDirectAmount().add(periodOverview.getTotalDirectCostsAgencyRequest()));
                } else {
                    routingFormBudget.setRoutingFormBudgetDirectAmount(routingFormBudget.getRoutingFormBudgetDirectAmount().add(periodOverview.getAdjustedDirectCostsAgencyRequest()));
                }
            }
        }
        
        //set simple values - just replace the RF version w/ what's in the Budget
        routingFormDocument.setRoutingFormBudgetNumber(budgetDocumentHeaderId);
        routingFormDocument.setRoutingFormAnnouncementNumber(budget.getBudgetProgramAnnouncementNumber());
        routingFormDocument.setAgencyFederalPassThroughNumber(budget.getFederalPassThroughAgencyNumber());
        routingFormDocument.setRoutingFormAgencyToBeNamedIndicator(budget.isAgencyToBeNamedIndicator());
        routingFormDocument.setRoutingFormPurposeCode(budget.getIndirectCost().getBudgetPurposeCode());
        
        routingFormDocument.getRoutingFormAgency().setAgencyNumber(budget.getBudgetAgencyNumber());
        

        //Project Director
        for (Iterator i = routingFormDocument.getRoutingFormPersonnel().iterator(); i.hasNext(); ) {
            RoutingFormPersonnel routingFormPerson = (RoutingFormPersonnel) i.next();
            if (routingFormPerson.isProjectDirector()) {
                i.remove();
                break;
            }
        }
        
        for (BudgetUser budgetUser : budget.getPersonnel()) {
            if (budgetUser.isPersonProjectDirectorIndicator()) {
                //Get Project Director from Budget
                RoutingFormPersonnel routingFormProjectDirector = new RoutingFormPersonnel(budgetUser, routingFormDocument.getDocumentNumber(), PERSON_ROLE_CODE_PD);
                routingFormProjectDirector.setPersonCreditPercent(new KualiInteger(100));
                routingFormProjectDirector.setPersonFinancialAidPercent(new KualiInteger(100));
                routingFormProjectDirector.refreshNonUpdateableReferences();
                routingFormDocument.addPerson(routingFormProjectDirector);
            }
            
            //Routing Form Cost Share entries from Personnel
            RoutingFormInstitutionCostShare routingFormInstitutionCostShare = new RoutingFormInstitutionCostShare();
            routingFormInstitutionCostShare.setChartOfAccountsCode(budgetUser.getFiscalCampusCode());
            routingFormInstitutionCostShare.setOrganizationCode(budgetUser.getPrimaryDepartmentCode());
            routingFormInstitutionCostShare.setRoutingFormCostShareAmount(new KualiInteger(0));
            
            for (UserAppointmentTask userAppointmentTask : budgetUser.getUserAppointmentTasks()) {
                for (UserAppointmentTaskPeriod userAppointmentTaskPeriod : userAppointmentTask.getUserAppointmentTaskPeriods()) {
                    if (userAppointmentTaskPeriod.getBudgetPeriodSequenceNumber().compareTo(minPeriod) >= 0 &&
                            userAppointmentTaskPeriod.getBudgetPeriodSequenceNumber().compareTo(maxPeriod) <= 0) {
                        routingFormInstitutionCostShare.setRoutingFormCostShareAmount(routingFormInstitutionCostShare.getRoutingFormCostShareAmount().add(userAppointmentTaskPeriod.getInstitutionCostShareRequestTotalAmount()));
                    }
                }
            }
            
            if (routingFormInstitutionCostShare.getRoutingFormCostShareAmount().isGreaterThan(KualiInteger.ZERO)) {
                routingFormDocument.addRoutingFormInstitutionCostShare(routingFormInstitutionCostShare, true);
            }
        }
        
        //Routing Form Cost Share entries from Budget Cost Share screen (not personnel)
        for (BudgetInstitutionCostShare budgetInstitutionCostShare : budget.getInstitutionCostShareItems()) {
            RoutingFormInstitutionCostShare routingFormInstitutionCostShare = new RoutingFormInstitutionCostShare();
            routingFormInstitutionCostShare.setChartOfAccountsCode(budgetInstitutionCostShare.getChartOfAccountsCode());
            routingFormInstitutionCostShare.setOrganizationCode(budgetInstitutionCostShare.getOrganizationCode());
            routingFormInstitutionCostShare.setRoutingFormCostShareAmount(KualiInteger.ZERO);
            
            for (BudgetPeriodInstitutionCostShare budgetPeriodInstitutionCostShare : budgetInstitutionCostShare.getBudgetPeriodCostShare()) {
                if (budgetPeriodInstitutionCostShare.getBudgetPeriodSequenceNumber().compareTo(minPeriod) >= 0 &&
                        budgetPeriodInstitutionCostShare.getBudgetPeriodSequenceNumber().compareTo(maxPeriod) <= 0) {
                    routingFormInstitutionCostShare.setRoutingFormCostShareAmount(routingFormInstitutionCostShare.getRoutingFormCostShareAmount().add(budgetPeriodInstitutionCostShare.getBudgetCostShareAmount()));
                }                
            }

            if (routingFormInstitutionCostShare.getRoutingFormCostShareAmount().isGreaterThan(KualiInteger.ZERO)) {
                routingFormDocument.addRoutingFormInstitutionCostShare(routingFormInstitutionCostShare, true);
            }

        }
        
        //Other Cost Share from Budget Cost Share screen
        for (BudgetThirdPartyCostShare budgetThirdPartyCostShare : budget.getThirdPartyCostShareItems()) {
            RoutingFormOtherCostShare routingFormOtherCostShare = new RoutingFormOtherCostShare();
            routingFormOtherCostShare.setRoutingFormCostShareSourceName(budgetThirdPartyCostShare.getBudgetCostShareDescription());
            routingFormOtherCostShare.setRoutingFormCostShareAmount(KualiInteger.ZERO);
            
            for (BudgetPeriodThirdPartyCostShare budgetPeriodThirdPartyCostShare : budgetThirdPartyCostShare.getBudgetPeriodCostShare()) {
                if (budgetPeriodThirdPartyCostShare.getBudgetPeriodSequenceNumber().compareTo(minPeriod) >= 0 &&
                        budgetPeriodThirdPartyCostShare.getBudgetPeriodSequenceNumber().compareTo(maxPeriod) <= 0) {
                    routingFormOtherCostShare.setRoutingFormCostShareAmount(routingFormOtherCostShare.getRoutingFormCostShareAmount().add(budgetPeriodThirdPartyCostShare.getBudgetCostShareAmount()));
                }       
            }
            
            if (routingFormOtherCostShare.getRoutingFormCostShareAmount().isGreaterThan(KualiInteger.ZERO)) {
                routingFormDocument.addRoutingFormOtherCostShare(routingFormOtherCostShare);
            }
        }
        
        // Other Cost Share from Subcontractors (only Third Party and Inst. Cost Share amounts)
        // Create Aggregate Entries for Subcontractor Cost Share and Subcontractor entries
        Map<String, RoutingFormOtherCostShare> subcontractorCostShareMap = new HashMap<String, RoutingFormOtherCostShare>();
        Map<String, RoutingFormSubcontractor> subcontractorMap = new HashMap<String, RoutingFormSubcontractor>();
        for (BudgetNonpersonnel budgetNonpersonnel : budget.getNonpersonnelItems()) {
            if (KraConstants.SUBCONTRACTOR_CATEGORY_CODE.equals(budgetNonpersonnel.getBudgetNonpersonnelCategoryCode())) {
                if (budgetNonpersonnel.getBudgetPeriodSequenceNumber().compareTo(minPeriod) >= 0 && budgetNonpersonnel.getBudgetPeriodSequenceNumber().compareTo(maxPeriod) <= 0) {
                    if (subcontractorCostShareMap.containsKey(budgetNonpersonnel.getSubcontractorNumber())) {
                        RoutingFormOtherCostShare subcontractor = subcontractorCostShareMap.get(budgetNonpersonnel.getSubcontractorNumber());
                        subcontractor.setRoutingFormCostShareAmount(subcontractor.getRoutingFormCostShareAmount().add(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount().add(budgetNonpersonnel.getBudgetInstitutionCostShareAmount())));
                    }
                    else {
                        RoutingFormOtherCostShare routingFormOtherCostShare = new RoutingFormOtherCostShare();
                        routingFormOtherCostShare.setRoutingFormCostShareSourceName(budgetNonpersonnel.getBudgetNonpersonnelDescription());
                        routingFormOtherCostShare.setRoutingFormCostShareAmount(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount().add(budgetNonpersonnel.getBudgetInstitutionCostShareAmount()));

                        subcontractorCostShareMap.put(budgetNonpersonnel.getSubcontractorNumber(), routingFormOtherCostShare);
                    }
                    
                    if (subcontractorMap.containsKey(budgetNonpersonnel.getSubcontractorNumber())) {
                        RoutingFormSubcontractor subcontractor = subcontractorMap.get(budgetNonpersonnel.getSubcontractorNumber());
                        subcontractor.setRoutingFormSubcontractorAmount(subcontractor.getRoutingFormSubcontractorAmount().add(budgetNonpersonnel.getAgencyRequestAmount()));
                    }
                    else {
                        RoutingFormSubcontractor subcontractor = new RoutingFormSubcontractor();
                        subcontractor.setRoutingFormSubcontractorNumber(budgetNonpersonnel.getSubcontractorNumber());
                        subcontractor.setRoutingFormSubcontractorAmount(budgetNonpersonnel.getAgencyRequestAmount());
                        
                        subcontractorMap.put(budgetNonpersonnel.getSubcontractorNumber(), subcontractor);
                    }
                }
            }
        }

        for (RoutingFormOtherCostShare routingFormOtherCostShare : subcontractorCostShareMap.values()) {
            if (routingFormOtherCostShare.getRoutingFormCostShareAmount().isGreaterThan(KualiInteger.ZERO)) {
                routingFormDocument.addRoutingFormOtherCostShare(routingFormOtherCostShare);
            }
        }
        
        for (RoutingFormSubcontractor routingFormSubcontractor : subcontractorMap.values()) {
            if (routingFormSubcontractor.getRoutingFormSubcontractorAmount().isGreaterThan(KualiInteger.ZERO)) {
                routingFormDocument.addRoutingFormSubcontractor(routingFormSubcontractor);
            }
        }
    }
    
    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
