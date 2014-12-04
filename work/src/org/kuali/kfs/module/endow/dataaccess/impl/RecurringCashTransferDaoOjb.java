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
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.dataaccess.RecurringCashTransferDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class RecurringCashTransferDaoOjb extends PlatformAwareDaoBaseOjb implements RecurringCashTransferDao {

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.RecurringCashTransferDao#getRecurringCashTransferWithNextPayDateEqualToCurrentDate(Date)
     */
    public List<EndowmentRecurringCashTransfer> getRecurringCashTransferWithNextPayDateEqualToCurrentDate(Date currentDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_NEXT_PROC_DATE, currentDate);
        criteria.addNotNull(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_FREQUENCY_CODE);
        criteria.addEqualTo(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_ACTIVE_INDICATOR, "Y");
        return (List<EndowmentRecurringCashTransfer>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(EndowmentRecurringCashTransfer.class, criteria));
    }
}
