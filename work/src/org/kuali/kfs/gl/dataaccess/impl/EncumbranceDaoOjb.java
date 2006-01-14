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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.EncumbranceDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 *
 */
public class EncumbranceDaoOjb extends PersistenceBrokerDaoSupport implements EncumbranceDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceDaoOjb.class);

  public EncumbranceDaoOjb() {
    super();
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.dao.EncumbranceDao#getEncumbranceByTransaction(org.kuali.module.gl.bo.Transaction)
   */
  public Encumbrance getEncumbranceByTransaction(Transaction t) {
    LOG.debug("getEncumbranceByTransaction() started");

    Criteria crit = new Criteria();
    crit.addEqualTo("universityFiscalYear",t.getUniversityFiscalYear());
    crit.addEqualTo("chartOfAccountsCode",t.getChartOfAccountsCode());
    crit.addEqualTo("accountNumber",t.getAccountNumber());
    crit.addEqualTo("subAccountNumber",t.getSubAccountNumber());
    crit.addEqualTo("objectCode",t.getObjectCode());
    crit.addEqualTo("subObjectCode",t.getSubObjectCode());
    crit.addEqualTo("balanceTypeCode",t.getBalanceTypeCode());
    crit.addEqualTo("documentTypeCode",t.getDocumentTypeCode());
    crit.addEqualTo("originCode",t.getOriginCode());
    crit.addEqualTo("documentNumber",t.getDocumentNumber());

    QueryByCriteria qbc = QueryFactory.newQuery(Encumbrance.class,crit);
    return (Encumbrance)getPersistenceBrokerTemplate().getObjectByQuery(qbc);
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.dao.EncumbranceDao#save(org.kuali.module.gl.bo.Encumbrance)
   */
  public void save(Encumbrance e) {
    LOG.debug("save() started");

    getPersistenceBrokerTemplate().store(e);
  }
}
