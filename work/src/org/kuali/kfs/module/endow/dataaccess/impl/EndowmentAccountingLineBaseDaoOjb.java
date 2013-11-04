/*
 * Copyright 2010 The Kuali Foundation.
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
