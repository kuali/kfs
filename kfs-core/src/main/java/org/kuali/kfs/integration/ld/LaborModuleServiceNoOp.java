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
package org.kuali.kfs.integration.ld;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.integration.UnimplementedKfsModuleServiceImpl;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.lookup.HtmlData;

public class LaborModuleServiceNoOp extends UnimplementedKfsModuleServiceImpl implements LaborModuleService  {

    private Logger LOG = Logger.getLogger(getClass());

    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return KualiDecimal.ZERO;
    }

    public KualiDecimal calculateFringeBenefitFromLaborObject(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return KualiDecimal.ZERO;
    }

    @Override
    public int countPendingSalaryExpenseTransfer(String emplid) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return 0;
    }

    @Override
    public void createAndBlankApproveSalaryExpenseTransferDocument(String documentDescription, String explanation, String annotation, List<String> adHocRecipients, List<LaborLedgerExpenseTransferAccountingLine> sourceAccoutingLines, List<LaborLedgerExpenseTransferAccountingLine> targetAccoutingLines) throws WorkflowException {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
    }

    @Override
    public boolean doesLaborLedgerPositionObjectGroupExist(String positionObjectGroupCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    @Override
    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return Collections.emptyList();
    }

    @Override
    public Collection<LaborLedgerBalance> findLedgerBalances(Map<String, Collection<String>> fieldValues, Map<String, Collection<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypes, List<String> positionObjectGroupCodes) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return Collections.emptyList();
    }

    @Override
    public boolean hasFringeBenefitProducingObjectCodes(Integer fiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    @Override
    public boolean hasPendingLaborLedgerEntry(String chartOfAccountsCode, String accountNumber) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    @Override
    public boolean isEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    @Override
    public LaborLedgerObject retrieveLaborLedgerObject(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    @Override
    public LaborLedgerObject retrieveLaborLedgerObject(ObjectCode financialObject) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    @Override
    public List<LaborLedgerPositionObjectBenefit> retrieveLaborPositionObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return Collections.emptyList();
    }

    @Override
    public List<LaborLedgerPositionObjectBenefit> retrieveActiveLaborPositionObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return Collections.emptyList();
    }

    @Override
    public KualiDecimal calculateFringeBenefitFromLaborObject(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    @Override
    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    @Override
    public List<String> getLaborLedgerGLOriginCodes() {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return Collections.emptyList();
    }

    @Override
    public HtmlData getInquiryUrlForGeneralLedgerEntryDocumentNumber(Entry entry) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }


    @Override
    public String getBenefitRateCategoryCode(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    @Override
    public String getCostSharingSourceAccountNumber() {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    @Override
    public String getCostSharingSourceSubAccountNumber() {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    @Override
    public String getCostSharingSourceChartOfAccountsCode() {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    @Override
    public AccountingLineOverride determineNeededOverrides(AccountingDocument document, AccountingLine line) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    @Override
    @Deprecated
    public AccountingLineOverride determineNeededOverrides(AccountingLine line) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

}
