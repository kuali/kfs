package org.kuali.module.gl.dao;

import java.util.Collection;

import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.SufficientFundRebuild;

public interface SufficientFundRebuildDao {

    public Collection getAll(); 
    public SufficientFundRebuild get(String chartOfAccountsCode, String accountObjectIdentityCode, String AccountNumberObjectCode);
    public void save(SufficientFundRebuild sfrb);
    public void delete(SufficientFundRebuild sfrb);
}
