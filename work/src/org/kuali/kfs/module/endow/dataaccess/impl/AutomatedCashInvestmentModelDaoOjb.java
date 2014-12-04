/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.sql.Date;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.module.endow.dataaccess.AutomatedCashInvestmentModelDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;


public class AutomatedCashInvestmentModelDaoOjb extends PlatformAwareDaoBaseOjb implements AutomatedCashInvestmentModelDao {
        
    /**
     * @see org.kuali.kfs.module.endow.dataaccess.AutomatedCashInvestmentModelDao#getAutomatedCashInvestmentModelWithNextPayDateEqualToCurrentDate()
     */
    public List<AutomatedCashInvestmentModel> getAutomatedCashInvestmentModelWithNextPayDateEqualToCurrentDate(Date currentDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.ACI_MODEL_NEXT_DUE_DATE, currentDate);
        criteria.addNotNull(EndowPropertyConstants.ACI_MODEL_FREQUENCY_CDOE);
        criteria.addEqualTo(EndowPropertyConstants.ACI_MODEL_ACTIVE_INDICATOR, "Y");
        return (List<AutomatedCashInvestmentModel>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AutomatedCashInvestmentModel.class, criteria));
    }

}
