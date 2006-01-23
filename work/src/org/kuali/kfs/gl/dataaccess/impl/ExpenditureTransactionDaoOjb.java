/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.dao.ojb;

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.gl.bo.ExpenditureTransaction;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.ExpenditureTransactionDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 *
 */
public class ExpenditureTransactionDaoOjb extends PersistenceBrokerDaoSupport implements ExpenditureTransactionDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExpenditureTransactionDaoOjb.class);

  public ExpenditureTransactionDaoOjb() {
    super();
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.dao.ExpenditureTransactionDao#getByTransaction(org.kuali.module.gl.bo.Transaction)
   */
  public ExpenditureTransaction getByTransaction(Transaction t) {
    LOG.debug("getByTransaction() started");

    Criteria crit = new Criteria();
    crit.addEqualTo("universityFiscalYear",t.getUniversityFiscalYear());
    crit.addEqualTo("chartOfAccountsCode",t.getChartOfAccountsCode());
    crit.addEqualTo("accountNumber",t.getAccountNumber());
    crit.addEqualTo("subAccountNumber",t.getSubAccountNumber());
    crit.addEqualTo("objectCode",t.getFinancialObjectCode());
    crit.addEqualTo("subObjectCode",t.getFinancialSubObjectCode());
    crit.addEqualTo("balanceTypeCode",t.getFinancialBalanceTypeCode());
    crit.addEqualTo("objectTypeCode",t.getFinancialObjectTypeCode());
    crit.addEqualTo("universityFiscalAccountingPeriod",t.getUniversityFiscalPeriodCode());
    crit.addEqualTo("projectCode",t.getProjectCode());
    crit.addEqualTo("organizationReferenceId",t.getOrganizationReferenceId());

    QueryByCriteria qbc = QueryFactory.newQuery(ExpenditureTransaction.class,crit);
    return (ExpenditureTransaction)getPersistenceBrokerTemplate().getObjectByQuery(qbc);
  }

  public Iterator getAllExpenditureTransactions() {
    LOG.debug("getAllExpenditureTransactions() started");

    Criteria crit = new Criteria();
    // We want them all so no criteria is added

    QueryByCriteria qbc = QueryFactory.newQuery(ExpenditureTransaction.class,crit);
    return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
  }

  public void delete(ExpenditureTransaction et) {
    LOG.debug("delete() started");

    getPersistenceBrokerTemplate().delete(et);
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.dao.ExpenditureTransactionDao#save(org.kuali.module.gl.bo.ExpenditureTransaction)
   */
  public void save(ExpenditureTransaction et) {
    LOG.debug("save() started");

    getPersistenceBrokerTemplate().store(et);
  }
}
