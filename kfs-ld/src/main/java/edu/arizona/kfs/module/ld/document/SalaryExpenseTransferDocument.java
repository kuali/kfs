package edu.arizona.kfs.module.ld.document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.util.LaborPendingEntryGenerator;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.ld.LaborConstants;
import edu.arizona.kfs.module.ld.LaborParameterKeyConstants;

public class SalaryExpenseTransferDocument extends org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument {

	private static final long serialVersionUID = 1L;

	private BusinessObjectService businessObjectService;
	private ParameterService parameterService;

	@Override
	public boolean generateLaborLedgerPendingEntries(AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("started generateLaborLedgerPendingEntries()");
		}

		boolean isSuccessful = true;
		ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

		List<LaborLedgerPendingEntry> expensePendingEntries = LaborPendingEntryGenerator.generateExpensePendingEntries(this, expenseTransferAccountingLine, sequenceHelper);
		if (expensePendingEntries != null && !expensePendingEntries.isEmpty()) {
			isSuccessful &= this.getLaborLedgerPendingEntries().addAll(expensePendingEntries);
		}

		List<LaborLedgerPendingEntry> benefitPendingEntries = LaborPendingEntryGenerator.generateBenefitPendingEntries(this, expenseTransferAccountingLine, sequenceHelper);
		if (benefitPendingEntries != null && !benefitPendingEntries.isEmpty()) {
			isSuccessful &= this.getLaborLedgerPendingEntries().addAll(benefitPendingEntries);
		}

		String salaryObjectCode = getParameterService().getParameterValueAsString(SalaryExpenseTransferDocument.class, LaborParameterKeyConstants.SalaryExpenseTransfer.TUITION_REMISSION_DIRECT_BILL_SALARY_PARM_NM);
		String fringeBenefitObjectCode = getParameterService().getParameterValueAsString(SalaryExpenseTransferDocument.class, LaborParameterKeyConstants.SalaryExpenseTransfer.TUITION_REMISSION_DIRECT_BILL_POSTING_PARM_NM);

		if (StringUtils.isNotEmpty(salaryObjectCode) && StringUtils.isNotEmpty(fringeBenefitObjectCode) && StringUtils.equals(expenseTransferAccountingLine.getFinancialObjectCode(), salaryObjectCode)) {
			KualiDecimal benefitAmount = calculateTuitionRemission(expenseTransferAccountingLine, salaryObjectCode, fringeBenefitObjectCode);

			if (benefitAmount.isNonZero()) {
				benefitPendingEntries = LaborPendingEntryGenerator.generateBenefitPendingEntries(this, expenseTransferAccountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);
				if (benefitPendingEntries != null && !benefitPendingEntries.isEmpty()) {
					isSuccessful &= this.getLaborLedgerPendingEntries().addAll(benefitPendingEntries);
				}
			}
		}
		
		return isSuccessful;
	}

	public KualiDecimal calculateTuitionRemission(ExpenseTransferAccountingLine expenseTransferAccountingLine, String salaryObjectCode, String fringeBenefitObjectCode) {
		BigDecimal salaryAmount = amountForCalculation(expenseTransferAccountingLine, salaryObjectCode);
		BigDecimal tuitionRemissionAmount = amountForCalculation(expenseTransferAccountingLine, fringeBenefitObjectCode);

		if (salaryAmount == null || salaryAmount.intValue() == 0 || tuitionRemissionAmount == null || tuitionRemissionAmount.intValue() == 0) {
			return KualiDecimal.ZERO;
		}

		BigDecimal fringeBenefitPercent = tuitionRemissionAmount.divide(salaryAmount, 12, BigDecimal.ROUND_HALF_UP);
		return new KualiDecimal(fringeBenefitPercent.multiply(expenseTransferAccountingLine.getAmount().bigDecimalValue()));
	}

	public BigDecimal amountForCalculation(ExpenseTransferAccountingLine expenseTransferAccountingLine, String objectCode) {
		Map<String, String> fieldValues = new HashMap<String, String>();
		fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, expenseTransferAccountingLine.getPayrollEndDateFiscalYear().toString());
		fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
		fieldValues.put(KFSPropertyConstants.POSITION_NUMBER, expenseTransferAccountingLine.getPositionNumber());
		fieldValues.put(KFSPropertyConstants.EMPLID, expenseTransferAccountingLine.getEmplid());

		Collection<LedgerBalance> ledgerBalances = findLedgerBalanceSimple(fieldValues);

		BigDecimal amountforCalculation = new BigDecimal(0);
		for (LedgerBalance balance : ledgerBalances) {
			amountforCalculation = amountforCalculation.add(balance.getAmountByPeriod(expenseTransferAccountingLine.getPayrollEndDateFiscalPeriodCode()).bigDecimalValue());
		}
		
		return amountforCalculation;
	}

	private Collection<LedgerBalance> findLedgerBalanceSimple(Map<String, String> fieldValues) {
		List<LedgerBalance> ledgerBalanceToReturn = new ArrayList<LedgerBalance>();
		Collection<LedgerBalance> ledgerBalances = getBusinessObjectService().findMatching(LedgerBalance.class, fieldValues);

		for (LedgerBalance ledgerBalance : ledgerBalances) {
			if (ledgerBalance.getFinancialBalanceTypeCode().equalsIgnoreCase(LaborConstants.BALANCE_TYPE_ACTUAL) || ledgerBalance.getFinancialBalanceTypeCode().equalsIgnoreCase(LaborConstants.BALANCE_TYPE_A2)) {
				ledgerBalanceToReturn.add(ledgerBalance);
			}
		}
		
		return ledgerBalanceToReturn;
	}

	public BusinessObjectService getBusinessObjectService() {
		if (ObjectUtils.isNull(businessObjectService)) {
			businessObjectService = SpringContext.getBean(BusinessObjectService.class);
		}
		return businessObjectService;
	}

	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

	public ParameterService getParameterService() {
		if (ObjectUtils.isNull(parameterService)) {
			parameterService = SpringContext.getBean(ParameterService.class);
		}
		return parameterService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

}
