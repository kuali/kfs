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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineBase;
import org.kuali.kfs.module.endow.dataaccess.EndowmentAccountingLineBaseDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class EndowmentAccountingLineBaseDaoOjb extends PlatformAwareDaoBaseOjb implements EndowmentAccountingLineBaseDao {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EndowmentAccountingLineBaseDaoOjb.class);

    /**
     * @@see {@link org.kuali.kfs.module.endow.dataaccess.EndowmentAccountingLineBaseDao#getAllEndowmentAccountingLines(String)

     */
    public Collection<EndowmentAccountingLineBase> getAllEndowmentAccountingLines(String documentNumber) {
        Collection<EndowmentAccountingLineBase> endowmentAccountingLines = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.DOCUMENT_NUMBER, documentNumber);

        // sort the data on these columns....
        QueryByCriteria qbc = QueryFactory.newQuery(EndowmentAccountingLineBase.class, criteria);

        qbc.addOrderByAscending(EndowPropertyConstants.ColumnNames.GlInterfaceBatchProcessLine.TRANSACTION_ARCHIVE_FDOC_LN_TYP_CD);

        endowmentAccountingLines = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);

        return endowmentAccountingLines;
    }
}
