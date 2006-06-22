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
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.dao.SubObjectCodeDao;
import org.springframework.orm.ojb.PersistenceBrokerTemplate;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;


/**
 * This class is the OJB implementation of the SubObjectCodeDao interface.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class SubObjectCodeDaoOjb extends PersistenceBrokerDaoSupport implements SubObjectCodeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubObjectCodeDaoOjb.class);

    /**
     * Retrieves sub object code business object by primary key
     * 
     * @param universityFiscalYear - part of composite key
     * @param chartOfAccountsCode - part of composite key
     * @param accountNumber - part of composite key
     * @param financialObjectCode - part of composite key
     * @param financialSubObjectCode - part of composite key
     * @return SubObjectCode
     * @see SubObjectCodeDao
     */
    public SubObjCd getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", universityFiscalYear);
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("financialObjectCode", financialObjectCode);
        criteria.addEqualTo("financialSubObjectCode", financialSubObjectCode);

        return (SubObjCd)getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(SubObjCd.class, criteria));
    }
}
