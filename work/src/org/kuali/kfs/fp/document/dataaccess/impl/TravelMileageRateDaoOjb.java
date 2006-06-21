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
package org.kuali.module.financial.dao.ojb;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.module.chart.dao.ojb.ChartDaoOjb;
import org.kuali.module.financial.bo.TravelMileageRate;
import org.kuali.module.financial.dao.TravelMileageRateDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is the OJB implementation of the TravelMileageRate interface.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class TravelMileageRateDaoOjb extends PersistenceBrokerDaoSupport implements TravelMileageRateDao {
    private static Logger LOG = Logger.getLogger(ChartDaoOjb.class);

    /**
     * @see org.kuali.module.financial.dao.TravelMileageRateDao#retrieveMostEffectiveMileageRates(java.sql.Timestamp)
     */
    public Collection retrieveMostEffectiveMileageRates(Date effectiveDate) {
        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan("disbursementVoucherMileageEffectiveDate", effectiveDate);

        QueryByCriteria queryByCriteria = new QueryByCriteria(TravelMileageRate.class, criteria);
        queryByCriteria.addOrderByDescending("disbursementVoucherMileageEffectiveDate");
        queryByCriteria.addOrderByDescending("mileageLimitAmount");

        Collection mostEffectiveRates = new ArrayList();
        Collection rates = getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
        Date mostEffectiveDate = ((TravelMileageRate) rates.iterator().next()).getDisbursementVoucherMileageEffectiveDate();
        for (Iterator iter = rates.iterator(); iter.hasNext();) {
            TravelMileageRate rate = (TravelMileageRate) iter.next();
            if (rate.getDisbursementVoucherMileageEffectiveDate().compareTo(mostEffectiveDate) == 0) {
                mostEffectiveRates.add(rate);
            }
        }

        return mostEffectiveRates;
    }


}