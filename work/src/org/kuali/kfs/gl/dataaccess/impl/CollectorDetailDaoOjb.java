/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.dataaccess.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Iterator;

import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.dataaccess.CollectorDetailDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.exception.ClassNotPersistableException;

/**
 * An OJB implementation of the CollectorDetailDao
 */
public class CollectorDetailDaoOjb extends PlatformAwareDaoBaseOjb implements CollectorDetailDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorDetailDaoOjb.class);

    private DescriptorRepository descriptorRepository;

    public CollectorDetailDaoOjb() {
        MetadataManager metadataManager = MetadataManager.getInstance();
        descriptorRepository = metadataManager.getGlobalRepository();
    }

    /**
     * Purge the table by year/chart.  Clears persistence broker template at the end to ensure OJB has to to DB again
     * to retrieve the post-purged state of the DB. 
     * 
     * @see org.kuali.kfs.gl.dataaccess.CollectorDetailDao#purgeYearByChart(java.lang.String, int)
     */
    public void purgeYearByChart(String chartOfAccountsCode, int universityFiscalYear) {
        LOG.debug("purgeYearByChart() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addLessThan(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, new Integer(universityFiscalYear));
        
        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(CollectorDetail.class, criteria));

        // This is required because if any deleted items are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * Retrieves the DB table name that's mapped to instances of CollectorDetail by finding the class descriptor name from the
     * class descriptor respository 
     * @return the table name where collector details are saved to
     * @see org.kuali.kfs.gl.dataaccess.CollectorDetailDao#retrieveCollectorDetailTableName()
     */
    public String retrieveCollectorDetailTableName() {
        ClassDescriptor classDescriptor = null;
        DescriptorRepository globalRepository = descriptorRepository;
        try {
            classDescriptor = globalRepository.getDescriptorFor(CollectorDetail.class);
        }
        catch (ClassNotPersistenceCapableException e) {
            throw new ClassNotPersistableException("class '" + CollectorDetail.class.getName() + "' is not persistable", e);
        }

        return classDescriptor.getFullTableName();
    }


    public Integer getMaxCreateSequence(Date date) {
        Criteria crit = new Criteria();
        crit.addEqualTo("CREATE_DT", date);

        ReportQueryByCriteria q = QueryFactory.newReportQuery(CollectorDetail.class, crit);
        q.setAttributes(new String[] { "max(transactionLedgerEntrySequenceNumber)" });

        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if (iter.hasNext()) {
            Object[] result = iter.next();
            if (result[0] != null) {
                return new Integer(((BigDecimal)result[0]).intValue());
            }
        }
        return null;
    }
    
    
}
