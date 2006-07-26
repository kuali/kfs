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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.dao.ChartDao;
import org.springframework.orm.ojb.PersistenceBrokerTemplate;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

import com.sun.org.apache.bcel.internal.generic.RETURN;

/**
 * This class is the OJB implementation of the ChartDao interface.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */


public class ChartDaoOjb extends PersistenceBrokerDaoSupport implements ChartDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ChartDaoOjb.class);

    public ChartDaoOjb() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.iu.uis.kuali.dao.ChartDao#getAll()
     */
    public Collection getAll() {
        QueryByCriteria qbc = QueryFactory.newQuery(Chart.class, (Criteria) null);
        qbc.addOrderByAscending("chartOfAccountsCode");

        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
    
    public Chart getUniversityChart() {
        Criteria criteria = new Criteria();
        criteria.addEqualToField("FIN_COA_CD", "RPTS_TO_FIN_COA_CD");
        return (Chart)getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Chart.class, criteria)).iterator().next();
    }


    public Chart getByPrimaryId(String chartOfAccountsCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);

        return (Chart)getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Chart.class, criteria));
    }

    /**
     * fetch the charts that the user is manager for
     * 
     * @param kualiUser
     * @return a list of Charts that the user has responsibility for
     */
    public List getChartsThatUserIsResponsibleFor(KualiUser kualiUser) {
        List chartResponsibilities = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("finCoaManagerUniversalId", kualiUser.getPersonUniversalIdentifier());
        Collection charts = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Chart.class, criteria));
        for (Iterator iter = charts.iterator(); iter.hasNext();) {
            Chart chart = (Chart) iter.next();
            chartResponsibilities.add(chart);
        }
        return chartResponsibilities;
    }
}