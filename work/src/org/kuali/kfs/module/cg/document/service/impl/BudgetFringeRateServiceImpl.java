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
package org.kuali.module.kra.budget.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.budget.bo.AppointmentType;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetFringeRate;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.service.BudgetFringeRateService;

/**
 * This class is the service implementation for the Account structure. This is the default, Kuali provided implementation.
 */
public class BudgetFringeRateServiceImpl implements BudgetFringeRateService {

    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;

    public BudgetFringeRate getBudgetFringeRate(String documentNumber, String institutionAppointmentTypeCode) {

        BudgetFringeRate budgetFringeRate = (BudgetFringeRate) businessObjectService.retrieve(new BudgetFringeRate(documentNumber, institutionAppointmentTypeCode));

        if (budgetFringeRate == null) {
            AppointmentType appointmentType = (AppointmentType) businessObjectService.retrieve(new AppointmentType(institutionAppointmentTypeCode));
            budgetFringeRate = new BudgetFringeRate(documentNumber, appointmentType);
        }

        return budgetFringeRate;
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetFringeRateService#getDefaultFringeRates()
     */
    public Collection getDefaultFringeRates() {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        return businessObjectService.findMatching(AppointmentType.class, fieldValues);
    }

    public BudgetFringeRate getBudgetFringeRateForDefaultAppointmentType(String documentNumber) {

        AppointmentType appointmentType = (AppointmentType) businessObjectService.retrieve(new AppointmentType(parameterService.getParameterValue(ParameterConstants.RESEARCH_ADMINISTRATION_DOCUMENT.class, KraConstants.DEFAULT_APPOINTMENT_TYPE)));

        BudgetFringeRate defaultFringeRate = (BudgetFringeRate) businessObjectService.retrieve(new BudgetFringeRate(documentNumber, appointmentType.getAppointmentTypeCode()));

        if (defaultFringeRate != null) {
            return defaultFringeRate;
        }
        else {
            return new BudgetFringeRate(documentNumber, appointmentType);
        }
    }

    public BudgetFringeRate getBudgetFringeRateForPerson(BudgetUser budgetUser) {
        if (StringUtils.isNotEmpty(budgetUser.getAppointmentTypeCode())) {
            return this.getBudgetFringeRate(budgetUser.getDocumentNumber(), budgetUser.getAppointmentTypeCode());
        }
        else if (budgetUser.getUserAppointmentTasks().size() > 0 && StringUtils.isNotEmpty(budgetUser.getUserAppointmentTask(0).getInstitutionAppointmentTypeCode())) {
            return this.getBudgetFringeRate(budgetUser.getDocumentNumber(), budgetUser.getUserAppointmentTask(0).getInstitutionAppointmentTypeCode());
        }
        else {
            return this.getBudgetFringeRateForDefaultAppointmentType(budgetUser.getDocumentNumber());
        }
    }

    public boolean isValidFringeRate(KualiDecimal fringeRate) {
        if (fringeRate != null) {
            return fringeRate.isLessEqual(KFSConstants.CONTRACTS_AND_GRANTS_FRINGE_RATE_MAX);
        }
        else {
            return false;
        }
    }

    public boolean isValidCostShare(KualiDecimal costShare) {
        if (costShare != null) {
            return costShare.isLessEqual(KFSConstants.CONTRACTS_AND_GRANTS_COST_SHARE_MAX);
        }
        else {
            return false;
        }
    }

    public void setupDefaultFringeRates(Budget budget) {
        for (Iterator iter = getDefaultFringeRates().iterator(); iter.hasNext();) {
            AppointmentType appType = (AppointmentType) iter.next();
            BudgetFringeRate bfr = new BudgetFringeRate(budget.getDocumentNumber(), appType.getAppointmentTypeCode(), appType.getFringeRateAmount(), appType.getCostShareFringeRateAmount(), appType);
            budget.getFringeRates().add(bfr);
        }
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
