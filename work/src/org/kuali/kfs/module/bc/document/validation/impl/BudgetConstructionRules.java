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
package org.kuali.kfs.module.bc.document.validation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.SaveDocumentRule;
import org.kuali.kfs.module.bc.BCConstants.MonthSpreadDeleteType;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.validation.AddPendingBudgetGeneralLedgerLineRule;
import org.kuali.kfs.module.bc.document.validation.BudgetExpansionRule;
import org.kuali.kfs.module.bc.document.validation.DeleteMonthlySpreadRule;
import org.kuali.kfs.module.bc.document.validation.DeletePendingBudgetGeneralLedgerLineRule;
import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;
import org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent;

/**
 * Base rule class for Budget Construction. Handles calling other expansion rule classes and the core budget document rules.
 */
public class BudgetConstructionRules implements BudgetExpansionRule, SalarySettingRule, SaveDocumentRule, AddPendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger>, DeletePendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger>, DeleteMonthlySpreadRule<BudgetConstructionDocument> {
    private Collection<BusinessRule> expansionRules;
    private BudgetConstructionDocumentRules budgetConstructionDocumentRules;
    private SalarySettingRules salarySettingRules;

    /**
     * Initialize expansion rule classes.
     */
    public BudgetConstructionRules() {
        expansionRules = new ArrayList<BusinessRule>();

        try {
            budgetConstructionDocumentRules = BudgetConstructionDocumentRules.class.newInstance();
            expansionRules.add(budgetConstructionDocumentRules);

            salarySettingRules = SalarySettingRules.class.newInstance();
            expansionRules.add(salarySettingRules);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.BudgetExpansionRule#processExpansionRule(org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent)
     */
    public boolean processExpansionRule(BudgetExpansionEvent budgetExpansionEvent) {
        boolean valid = true;

        Class expansionRuleClass = budgetExpansionEvent.getExpansionRuleInterfaceClass();
        for (BusinessRule expansionRule : expansionRules) {
            if (expansionRuleClass.isAssignableFrom(expansionRule.getClass())) {
                valid &= budgetExpansionEvent.invokeExpansionRuleMethod(expansionRule);
            }
        }

        return valid;
    }

    /**
     * @see org.kuali.core.rule.SaveDocumentRule#processSaveDocument(org.kuali.core.document.Document)
     */
    public boolean processSaveDocument(Document document) {
        return ((SaveDocumentRule) budgetConstructionDocumentRules).processSaveDocument(document);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.AddPendingBudgetGeneralLedgerLineRule#processAddPendingBudgetGeneralLedgerLineRules(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger, boolean)
     */
    public boolean processAddPendingBudgetGeneralLedgerLineRules(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, boolean isRevenue) {
        return ((AddPendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger>) budgetConstructionDocumentRules).processAddPendingBudgetGeneralLedgerLineRules(budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, isRevenue);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.DeletePendingBudgetGeneralLedgerLineRule#processDeletePendingBudgetGeneralLedgerLineRules(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger, boolean)
     */
    public boolean processDeletePendingBudgetGeneralLedgerLineRules(BudgetConstructionDocument budgetConstructionDocument, PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, boolean isRevenue) {
        return ((DeletePendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger>) budgetConstructionDocumentRules).processDeletePendingBudgetGeneralLedgerLineRules(budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, isRevenue);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.DeleteMonthlySpreadRule#processDeleteMonthlySpreadRules(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.kfs.module.bc.BCConstants.MonthSpreadDeleteType)
     */
    public boolean processDeleteMonthlySpreadRules(BudgetConstructionDocument budgetConstructionDocument, MonthSpreadDeleteType monthSpreadDeleteType) {
        return ((DeleteMonthlySpreadRule<BudgetConstructionDocument>) budgetConstructionDocumentRules).processDeleteMonthlySpreadRules(budgetConstructionDocument, monthSpreadDeleteType);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAddAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processAddAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> existingAppointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return salarySettingRules.processAddAppointmentFunding(existingAppointmentFundings, appointmentFunding);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processSaveAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return salarySettingRules.processSaveAppointmentFunding(appointmentFunding);
    }
}
