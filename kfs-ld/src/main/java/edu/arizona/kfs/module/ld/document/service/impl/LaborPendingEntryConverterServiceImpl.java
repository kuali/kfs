package edu.arizona.kfs.module.ld.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.module.ld.LaborConstants;

public class LaborPendingEntryConverterServiceImpl extends org.kuali.kfs.module.ld.document.service.impl.LaborPendingEntryConverterServiceImpl {

	@Override
	public LaborLedgerPendingEntry getBenefitPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
		LaborLedgerPendingEntry pendingEntry = getDefaultPendingEntry(document, accountingLine);
		
		// if account doesn't accept fringe charges, use reports to account
		if (!accountingLine.getAccount().isAccountsFringesBnftIndicator()) {
			pendingEntry.setChartOfAccountsCode(accountingLine.getAccount().getReportsToChartOfAccountsCode());
			pendingEntry.setAccountNumber(accountingLine.getAccount().getReportsToAccountNumber());
		}
		
		pendingEntry.setFinancialBalanceTypeCode(LaborConstants.BALANCE_TYPE_ACTUAL);
		pendingEntry.setFinancialObjectCode(pickValue(fringeBenefitObjectCode, KFSConstants.getDashFinancialObjectCode()));
		
		ObjectCode fringeObjectCode = getObjectCodeService().getByPrimaryId(accountingLine.getPayrollEndDateFiscalYear(), accountingLine.getChartOfAccountsCode(), fringeBenefitObjectCode);
		pendingEntry.setFinancialObjectTypeCode(fringeObjectCode.getFinancialObjectTypeCode());

		pendingEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
		pendingEntry.setTransactionLedgerEntryAmount(benefitAmount.abs());
		pendingEntry.setPositionNumber(StringUtils.isBlank(accountingLine.getPositionNumber()) ? LaborConstants.getDashPositionNumber() : accountingLine.getPositionNumber());
		pendingEntry.setEmplid(StringUtils.isBlank(accountingLine.getEmplid()) ? LaborConstants.getDashEmplId() : accountingLine.getEmplid());
		pendingEntry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));
		
		// year end document should post to previous fiscal year and final period
		overrideEntryForYearEndIfNecessary(document, pendingEntry);

		return pendingEntry;
	}
	
}
