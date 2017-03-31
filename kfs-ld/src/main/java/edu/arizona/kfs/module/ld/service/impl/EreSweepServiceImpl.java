package edu.arizona.kfs.module.ld.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.coa.businessobject.AccountExtension;
import edu.arizona.kfs.module.ld.LaborConstants;
import edu.arizona.kfs.module.ld.dataaccess.AccountExtensionDao;
import edu.arizona.kfs.module.ld.dataaccess.EreSweepDao;
import edu.arizona.kfs.module.ld.service.EreSweepFileHandlerService;
import edu.arizona.kfs.module.ld.service.EreSweepParameterService;
import edu.arizona.kfs.module.ld.service.EreSweepService;
import edu.arizona.kfs.module.ld.util.EreSweepBalanceHelper;

@Transactional
public class EreSweepServiceImpl implements EreSweepService {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EreSweepServiceImpl.class);
	
	private EreSweepFileHandlerService fileHandler;
	private EreSweepDao ereSweepDao;
	private AccountExtensionDao accountExtensionDao;
	private EreSweepParameterService ereSweepParameterService;

	@Override
	public void processEreSweep(Date jobRunDate) {
		java.sql.Date sqlDate = new java.sql.Date(jobRunDate.getTime());
		
		LOG.info("processEreSweep: start=" + new Date());
		LOG.info("EreSweep job run date=" + sqlDate);
		
		List<String> includedSubFunds = Arrays.asList(ereSweepParameterService.getSubFundGroupParameters());
		List<String> includedObjectCodes = Arrays.asList(ereSweepParameterService.getObjectSubTypesParameters());
		
		LOG.info("includedSubFunds = " + includedSubFunds);
		LOG.info("includedObkectCodes = " + includedObjectCodes);
		
		fileHandler.startUp();
		
		List<EreSweepBalanceHelper> distinctBalances = ereSweepDao.getDistinctBalance(includedSubFunds, includedObjectCodes);
		
		// Iterate over the distinct balanceCollection Calculate BBA then Print to LD feed file
		for (EreSweepBalanceHelper employeeBalance : distinctBalances) {
			List<LedgerBalance> ledgerBalances = ereSweepDao.getMatchingBalances(employeeBalance, includedSubFunds, includedObjectCodes);
			KualiDecimal amount = calculateCb(ledgerBalances);
			
			if (amount.isNonZero()) {
				LedgerBalance ledgerBalance = ledgerBalances.get(0);
				
				String accountNumber = ledgerBalance.getAccountNumber();
				String chartCode = ledgerBalance.getChartOfAccountsCode();
				LOG.info("Account = " + accountNumber + " ChartCode = " + chartCode);
				AccountExtension accountExt = accountExtensionDao.getAccountExtensionByPrimaryKey(accountNumber, chartCode);
				if (ObjectUtils.isNull(accountExt) || StringUtils.isBlank(accountExt.getInstitutionalFringeAccountExt()) || StringUtils.isBlank(accountExt.getInstitutionalFringeCoaCodeExt())) {
					fileHandler.prepareErrorFile(KualiDecimal.ZERO, ledgerBalance);
				} else {
					fileHandler.prepareOutputFile(accountExt, amount, ledgerBalance);
				}
			}
		}
		
		fileHandler.closeConnection();
		
		LOG.info("processEreSweeo: end = " + new Date());
	}

	public KualiDecimal calculateCb(Collection<LedgerBalance> balances) {
		KualiDecimal cbAmount = KualiDecimal.ZERO;
		KualiDecimal acAmount = KualiDecimal.ZERO;
		KualiDecimal ieAmount = KualiDecimal.ZERO;
		
		boolean cbExists = false;
		
		for (LedgerBalance balance : balances) {
			if (balance.getFinancialBalanceTypeCode().equals(LaborConstants.FRINGE_BALANCE_TYPE_CODE_CB)) {
				cbAmount = balance.getAccountLineAnnualBalanceAmount();
				cbExists = true;
			}
			if (balance.getFinancialBalanceTypeCode().equals(LaborConstants.FRINGE_BALANCE_TYPE_CODE_AC)) {
				acAmount = balance.getAccountLineAnnualBalanceAmount();
			}
			if (balance.getFinancialBalanceTypeCode().equals(LaborConstants.FRINGE_BALANCE_TYPE_CODE_IE)) {
				ieAmount = balance.getAccountLineAnnualBalanceAmount();
			}
		}
		
		if (!cbExists) {
			return (ieAmount.add(acAmount)).multiply(new KualiDecimal(-1));
		}
		
		return cbAmount.subtract(acAmount.add(ieAmount));
	}
	
	public void setFileHandler(EreSweepFileHandlerService fileHandler) {
		this.fileHandler = fileHandler;
	}

	public void setEreSweepDao(EreSweepDao ereSweepDao) {
		this.ereSweepDao = ereSweepDao;
	}

	public void setAccountExtensionDao(AccountExtensionDao accountExtensionDao) {
		this.accountExtensionDao = accountExtensionDao;
	}

	public void setEreSweepParameterService(EreSweepParameterService ereSweepParameterService) {
		this.ereSweepParameterService = ereSweepParameterService;
	}
	
}
