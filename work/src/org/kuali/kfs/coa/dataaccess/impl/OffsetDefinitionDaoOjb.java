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
package org.kuali.module.chart.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.dao.OffsetDefinitionDao;
import org.springframework.orm.ojb.PersistenceBrokerTemplate;


/**
 * This class is the OJB implementation of the OffsetDefinitionDao interface.
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
*/
public class OffsetDefinitionDaoOjb extends PersistenceBrokerTemplate
    implements OffsetDefinitionDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OffsetDefinitionDaoOjb.class);

    /* (non-Javadoc)
     * @see org.kuali.dao.OffsetDefinitionDao#getByPrimaryId(java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
     */
    public OffsetDefinition getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String financialDocumentTypeCode, String financialBalanceTypeCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", universityFiscalYear);
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("financialDocumentTypeCode", financialDocumentTypeCode);
        criteria.addEqualTo("financialBalanceTypeCode", financialBalanceTypeCode);

        return (OffsetDefinition) getObjectByQuery(QueryFactory.newQuery(OffsetDefinition.class,
                criteria));
    }

    /**
     * Retrieves account business object by primary key
     * @param chartOfAccountsCode - the FIN_COA_CD of the Chart Code that is part of the composite key of Account
     * @param accountNumber - the ACCOUNT_NBR part of the composite key of Accont
     * @return Account
     * @see AccountDao
     */
/*    public Account getByPrimaryId(String chartOfAccountsCode,
        String accountNumber) {
        LOG.debug("getByPrimaryID() started ");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);

        return (Account) getObjectByQuery(new QueryByCriteria(Account.class,
                criteria));
    }
    
*/}
