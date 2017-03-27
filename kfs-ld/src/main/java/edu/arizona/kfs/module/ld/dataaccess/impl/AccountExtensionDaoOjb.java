package edu.arizona.kfs.module.ld.dataaccess.impl;

import java.util.Arrays;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.coa.businessobject.AccountExtension;
import edu.arizona.kfs.module.ld.dataaccess.AccountExtensionDao;

@Transactional
public class AccountExtensionDaoOjb extends PlatformAwareDaoBaseOjb implements AccountExtensionDao {

	@Override
	public AccountExtension getAccountExtensionByPrimaryKey(String accountNumber, String finCoaCd) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER , accountNumber);
		criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, finCoaCd);
		
		QueryByCriteria query = QueryFactory.newQuery(AccountExtension.class, criteria);
		return (AccountExtension) getPersistenceBrokerTemplate().getObjectByQuery(query);
	}

	@Override
	public Account getAccountByPrimaryKey(String accountNumber, String chartCode) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER , accountNumber);
		criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartCode);
		
		QueryByCriteria query = QueryFactory.newQuery(Account.class, criteria);
		return (Account) getPersistenceBrokerTemplate().getObjectByQuery(query);
	}

	@Override
	public Account getAccountBySubFundGroupCd(String fringeAccount, String objectCode, String[] includedSubFunds) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER , fringeAccount);
		criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectCode);
		criteria.addIn(KFSPropertyConstants.SUB_FUND_GROUP_CODE, Arrays.asList(includedSubFunds));
		
		QueryByCriteria query = QueryFactory.newQuery(Account.class, criteria);
		return (Account) getPersistenceBrokerTemplate().getObjectByQuery(query);
	}

}
