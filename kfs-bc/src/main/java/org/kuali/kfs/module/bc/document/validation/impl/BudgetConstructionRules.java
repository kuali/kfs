/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.bc.document.validation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.bc.BCConstants.MonthSpreadDeleteType;
import org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.validation.AddBudgetConstructionDocumentRule;
import org.kuali.kfs.module.bc.document.validation.AddPendingBudgetGeneralLedgerLineRule;
import org.kuali.kfs.module.bc.document.validation.BudgetExpansionRule;
import org.kuali.kfs.module.bc.document.validation.DeleteMonthlySpreadRule;
import org.kuali.kfs.module.bc.document.validation.DeletePendingBudgetGeneralLedgerLineRule;
import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;
import org.kuali.kfs.module.bc.document.validation.SaveMonthlyBudgetRule;
import org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.SaveDocumentRule;

/**
 * Base rule class for Budget Construction. Handles calling other expansion rule classes and the core budget document rules.
 */
public class BudgetConstructionRules implements BudgetExpansionRule, SalarySettingRule, SaveDocumentRule, AddBudgetConstructionDocumentRule<BudgetConstructionDocument>, AddPendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger>, DeletePendingBudgetGeneralLedgerLineRule<BudgetConstructionDocument, PendingBudgetConstructionGeneralLedger>, DeleteMonthlySpreadRule<BudgetConstructionDocument>, SaveMonthlyBudgetRule<BudgetConstructionDocument, BudgetConstructionMonthly> {
    private Collection<BusinessRule> expansionRules;
    private BudgetConstructionDocumentRules budgetConstructionDocumentRules;
    private SalarySettingRule salarySettingRules;

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

        Class<? extends BusinessRule> expansionRuleClass = budgetExpansionEvent.getExpansionRuleInterfaceClass();
        for (BusinessRule expansionRule : expansionRules) {
            if (expansionRuleClass.isAssignableFrom(expansionRule.getClass())) {
                valid &= budgetExpansionEvent.invokeExpansionRuleMethod(expansionRule);
            }
        }

        return valid;
    }

    /**
     * @see org.kuali.rice.krad.rule.SaveDocumentRule#processSaveDocument(org.kuali.rice.krad.document.Document)
     */
    public boolean processSaveDocument(Document document) {
        return ((SaveDocumentRule) budgetConstructionDocumentRules).processSaveDocument(document);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.AddBudgetConstructionDocumentRule#processAddBudgetConstructionDocumentRules(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    public boolean processAddBudgetConstructionDocumentRules(BudgetConstructionDocument budgetConstructionDocument) {
        return ((AddBudgetConstructionDocumentRule<BudgetConstructionDocument>) budgetConstructionDocumentRules).processAddBudgetConstructionDocumentRules(budgetConstructionDocument);
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

    public boolean processSaveMonthlyBudgetRules(BudgetConstructionDocument budgetConstructionDocument, BudgetConstructionMonthly budgetConstructionMonthly){
        return ((SaveMonthlyBudgetRule<BudgetConstructionDocument, BudgetConstructionMonthly>) budgetConstructionDocumentRules).processSaveMonthlyBudgetRules(budgetConstructionDocument, budgetConstructionMonthly);
    }
    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAddAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processAddAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> existingAppointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding, SynchronizationCheckType synchronizationCheckType) {
        return salarySettingRules.processAddAppointmentFunding(existingAppointmentFundings, appointmentFunding, synchronizationCheckType);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processSaveAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding, SynchronizationCheckType synchronizationCheckType) {
        return salarySettingRules.processSaveAppointmentFunding(appointmentFunding, synchronizationCheckType);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processNormalizePayrateAndAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processNormalizePayrateAndAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return salarySettingRules.processNormalizePayrateAndAmount(appointmentFunding);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAdjustSalaraySettingLinePercent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processAdjustSalaraySettingLinePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return salarySettingRules.processAdjustSalaraySettingLinePercent(appointmentFunding);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processQuickSaveAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processQuickSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return salarySettingRules.processQuickSaveAppointmentFunding(appointmentFunding);
    }
}
