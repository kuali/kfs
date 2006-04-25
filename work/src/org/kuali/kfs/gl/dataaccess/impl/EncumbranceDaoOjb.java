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
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.EncumbranceDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 * @version $Id: EncumbranceDaoOjb.java,v 1.6 2006-04-25 20:58:43 bgao Exp $
 */
public class EncumbranceDaoOjb extends PersistenceBrokerDaoSupport implements EncumbranceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceDaoOjb.class);

    public EncumbranceDaoOjb() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.EncumbranceDao#getEncumbranceByTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public Encumbrance getEncumbranceByTransaction(Transaction t) {
        LOG.debug("getEncumbranceByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", t.getUniversityFiscalYear());
        crit.addEqualTo("chartOfAccountsCode", t.getChartOfAccountsCode());
        crit.addEqualTo("accountNumber", t.getAccountNumber());
        crit.addEqualTo("subAccountNumber", t.getSubAccountNumber());
        crit.addEqualTo("objectCode", t.getFinancialObjectCode());
        crit.addEqualTo("subObjectCode", t.getFinancialSubObjectCode());
        crit.addEqualTo("balanceTypeCode", t.getFinancialBalanceTypeCode());
        crit.addEqualTo("documentTypeCode", t.getFinancialDocumentTypeCode());
        crit.addEqualTo("originCode", t.getFinancialSystemOriginationCode());
        crit.addEqualTo("documentNumber", t.getFinancialDocumentNumber());

        QueryByCriteria qbc = QueryFactory.newQuery(Encumbrance.class, crit);
        return (Encumbrance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    public Iterator getEncumbrancesToClose(Integer fiscalYear) {
        
//        816  002750     EXEC SQL
//        817  002760        DECLARE GLEC_CURSOR CURSOR FOR
//        818  002770          SELECT UNIV_FISCAL_YR,
//        819  002780                 FIN_COA_CD,
//        820  002790                 ACCOUNT_NBR,
//        821  002800                 SUB_ACCT_NBR,
//        822  002810                 FIN_OBJECT_CD,
//        823  002820                 FIN_SUB_OBJ_CD,
//        824  002830                 FIN_BALANCE_TYP_CD,
//        825  002840                 FDOC_TYP_CD,
//        826  002850                 FS_ORIGIN_CD,
//        827  002860                 FDOC_NBR,
//        828  002870                 TRN_ENCUM_DESC,
//        829  002880                 ACLN_ENCUM_AMT,
//        830  002890                 ACLN_ENCUM_CLS_AMT
//        831  002900           FROM  GL_ENCUMBRANCE_T
//        832  002910           WHERE UNIV_FISCAL_YR = RTRIM(:GLGLEC-UNIV-FISCAL-YR)
//        833  002920           ORDER BY FIN_COA_CD,
//        834  002930                    ACCOUNT_NBR,
//        835  002940                    SUB_ACCT_NBR,
//        836  002950                    FIN_OBJECT_CD,
//        837  002960                    FIN_SUB_OBJ_CD,
//        838  002970                    FIN_BALANCE_TYP_CD
//        839  002980           END-EXEC.        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", fiscalYear);
        
        QueryByCriteria query = new QueryByCriteria(Encumbrance.class, criteria);
        query.addOrderByAscending("chartOfAccountsCode");
        query.addOrderByAscending("accountNumber");
        query.addOrderByAscending("subAccountNumber");
        query.addOrderByAscending("objectCode");
        query.addOrderByAscending("subObjectCode");
        query.addOrderByAscending("balanceTypeCode");

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.EncumbranceDao#save(org.kuali.module.gl.bo.Encumbrance)
     */
    public void save(Encumbrance e) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(e);
    }

    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addLessThan("universityFiscalYear", new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(Encumbrance.class, criteria));

        // This is required because if any deleted account balances are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * @see org.kuali.module.gl.dao.EncumbranceDao#getAllEncumbrances()
     */
    public Iterator getAllEncumbrances() {
        Criteria criteria = new Criteria();
        QueryByCriteria query = QueryFactory.newQuery(Encumbrance.class, criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }
}
