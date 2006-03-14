package org.kuali.module.gl.dao;

import java.util.Collection;

import org.kuali.module.gl.bo.SufficientFundRebuild;

public interface SufficientFundRebuildDao {
    public Collection getAll();
    public SufficientFundRebuild getByAccount(String chartOfAccountsCode, String accountNumberFinancialObjectCode);
    public SufficientFundRebuild get(String chartOfAccountsCode, String accountFinancialObjectTypeCode, String accountNumberFinancialObjectCode);
    public void save(SufficientFundRebuild sfrb);
    public void delete(SufficientFundRebuild sfrb);
}
