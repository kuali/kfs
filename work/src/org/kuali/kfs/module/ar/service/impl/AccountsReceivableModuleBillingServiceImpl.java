/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.service.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.ar.AccountsReceivableMilestoneSchedule;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.integration.ar.AccountsReceivablePredeterminedBillingSchedule;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;

public class AccountsReceivableModuleBillingServiceImpl implements AccountsReceivableModuleBillingService {
    protected BusinessObjectService businessObjectService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected KualiModuleService kualiModuleService;
    protected ConfigurationService configurationService;

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getAwardBilledToDateAmountByProposalNumber(java.lang.Long) This
     *      method gets the award billed to date using ContractsGrantsInvoiceDocumentService
     * @param roposalNumber
     * @return
     */
    @Override
    public KualiDecimal getAwardBilledToDateAmountByProposalNumber(Long proposalNumber) {
        return getContractsGrantsInvoiceDocumentService().getAwardBilledToDateAmountByProposalNumber(proposalNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#calculateTotalPaymentsToDateByAward(java.lang.Long) This
     *      method calculates total payments to date by Award using ContractsGrantsInvoiceDocumentService
     * @param proposalNumber
     * @return
     */
    @Override
    public KualiDecimal calculateTotalPaymentsToDateByAward(Long proposalNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        ContractsAndGrantsBillingAward award = getKualiModuleService().getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);

        return getContractsGrantsInvoiceDocumentService().calculateTotalPaymentsToDateByAward(award);

    }

    @Override
    public AccountsReceivableMilestoneSchedule getMilestoneSchedule() {
        return new MilestoneSchedule();
    }

    @Override
    public AccountsReceivablePredeterminedBillingSchedule getPredeterminedBillingSchedule() {
        return new PredeterminedBillingSchedule();
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getLastBilledDate(java.lang.Long)
     */
    @Override
    public Date getLastBilledDate(ContractsAndGrantsBillingAward award) {
        return getContractsGrantsInvoiceDocumentService().getLastBilledDate(award);
    }

    @Override
    public List<String> checkAwardContractControlAccounts(ContractsAndGrantsBillingAward award) {
        return getContractsGrantsInvoiceDocumentService().checkAwardContractControlAccounts(award);
    }

    @Override
    public boolean hasPredeterminedBillingSchedule(Long proposalNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        PredeterminedBillingSchedule schedule = getBusinessObjectService().findByPrimaryKey(PredeterminedBillingSchedule.class, map);
        return ObjectUtils.isNotNull(schedule);
    }

    @Override
    public boolean hasMilestoneSchedule(Long proposalNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        MilestoneSchedule schedule = getBusinessObjectService().findByPrimaryKey(MilestoneSchedule.class, map);
        return ObjectUtils.isNotNull(schedule);
    }

    @Override
    public boolean hasActiveBills(Long proposalNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        PredeterminedBillingSchedule schedule = getBusinessObjectService().findByPrimaryKey(PredeterminedBillingSchedule.class, map);
        if (ObjectUtils.isNotNull(schedule)) {
            for (Bill bill: schedule.getBills()) {
                if (bill.isActive()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasActiveMilestones(Long proposalNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        MilestoneSchedule schedule = getBusinessObjectService().findByPrimaryKey(MilestoneSchedule.class, map);
        if (ObjectUtils.isNotNull(schedule)) {
            for (Milestone milestone: schedule.getMilestones()) {
                if (milestone.isActive()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String getContractsGrantsInvoiceDocumentType() {
        return ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE;
    }

    @Override
    public boolean isContractsGrantsBillingEnhancementActive() {
        return getConfigurationService().getPropertyValueAsBoolean(KFSConstants.CONTRACTS_GRANTS_BILLING_ENABLED);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}