/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.fp.dataaccess.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.kuali.kfs.fp.dataaccess.PopulateProcurementCardDefaultIdsDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;

public class PopulateProcurementCardDefaultIdsDaoJdbc extends PlatformAwareDaoBaseJdbc implements PopulateProcurementCardDefaultIdsDao {
    protected SequenceAccessorService sequenceAccessorService;

    /**
     * @see org.kuali.kfs.fp.dataaccess.PopulateProcurementCardDefaultIdsDao#populateIdsOnProcurementCardDefaults()
     */
    @Override
    public void populateIdsOnProcurementCardDefaults() {
        // do i wish i had java 8 for this?  yes, i wish i had java 8 for this...
        getJdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement stmt = con.prepareStatement("select CC_NBR from FP_PRCRMNT_CARD_DFLT_T where ID is null");
                return stmt;
            }
        }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                final String ccNumber = rs.getString("CC_NBR");
                final Long nextSequenceValue = getSequenceAccessorService().getNextAvailableSequenceNumber(KFSConstants.PROCUREMENT_CARD_DEFAULT_SEQUENCE_NAME);
                getJdbcTemplate().execute(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement stmt = con.prepareStatement("update FP_PRCRMNT_CARD_DFLT_T set ID = ? where CC_NBR = ?");
                        return stmt;
                    }
                }, new PreparedStatementCallback() {
                    @Override
                    public Object doInPreparedStatement(PreparedStatement stmt) throws SQLException, DataAccessException {
                        stmt.clearParameters();
                        stmt.setLong(1, nextSequenceValue.longValue());
                        stmt.setString(2, ccNumber);
                        stmt.executeUpdate();
                        return null;
                    }
                });
            }
        });
    }

    public SequenceAccessorService getSequenceAccessorService() {
        return sequenceAccessorService;
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }
}