package edu.arizona.kfs.module.ld.dataaccess.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import edu.arizona.kfs.module.ld.LaborPropertyConstants;
import edu.arizona.kfs.module.ld.dataaccess.EreSweepDao;
import edu.arizona.kfs.module.ld.util.EreSweepBalanceHelper;

public class EreSweepDaoOjb extends PlatformAwareDaoBaseOjb implements EreSweepDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<LedgerBalance> getMatchingBalances(EreSweepBalanceHelper employeeBalance, List<String> includedSubFunds, List<String> includedObjectCodes) {
		Criteria criteria = new Criteria();
		criteria.addIn(LaborPropertyConstants.LedgerBalance.ACCOUNT_SUB_FUND_GROUP_CD, includedSubFunds);
		criteria.addIn(LaborPropertyConstants.LedgerBalance.FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CD, includedObjectCodes);
		criteria.addEqualTo(LaborPropertyConstants.LedgerBalance.ACCOUNT_NUMBER, employeeBalance.getAccountNumber());
		criteria.addEqualTo(LaborPropertyConstants.LedgerBalance.SUB_ACCOUNT_NUMBER, employeeBalance.getSubAccountNumber());
		criteria.addEqualTo(LaborPropertyConstants.LedgerBalance.FINANCIAL_SUB_OBJECT_CD, employeeBalance.getFinancialSubObjectCode());
		criteria.addEqualTo(LaborPropertyConstants.LedgerBalance.FINANCIAL_OBJECT_CD, employeeBalance.getFinObjectCode());
		criteria.addEqualTo(LaborPropertyConstants.LedgerBalance.EMPLOYEE_ID , employeeBalance.getEmployeeId());
		criteria.addEqualTo(LaborPropertyConstants.LedgerBalance.POSITION_NUMBER, employeeBalance.getPositionNbr());
		
		ReportQueryByCriteria query = new ReportQueryByCriteria(LedgerBalance.class, criteria);
		return (List<LedgerBalance>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
	}

	@Override
	public List<EreSweepBalanceHelper> getDistinctBalance(List<String> includedSubFunds, List<String> includedObjectCodes) {
		Criteria criteria = new Criteria();
		criteria.addIn(LaborPropertyConstants.LedgerBalance.ACCOUNT_SUB_FUND_GROUP_CD, includedSubFunds);
		criteria.addIn(LaborPropertyConstants.LedgerBalance.FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CD, includedObjectCodes);
		
		ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);
		
		query.setAttributes(new String[] {LaborPropertyConstants.LedgerBalance.ACCOUNT_NUMBER, LaborPropertyConstants.LedgerBalance.FINANCIAL_OBJECT_CD, LaborPropertyConstants.LedgerBalance.EMPLOYEE_ID , LaborPropertyConstants.LedgerBalance.POSITION_NUMBER, LaborPropertyConstants.LedgerBalance.SUB_ACCOUNT_NUMBER, LaborPropertyConstants.LedgerBalance.FINANCIAL_SUB_OBJECT_CD});
		
		query.setDistinct(true);
		
		List<EreSweepBalanceHelper> balanceList = new ArrayList<EreSweepBalanceHelper>();
		
		@SuppressWarnings("unchecked")
		Iterator<Object[]> iterBalance = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
		
		while (iterBalance != null && iterBalance.hasNext()) {
			balanceList.add(new EreSweepBalanceHelper(iterBalance.next()));
		}
		
		return balanceList;
	}

}
