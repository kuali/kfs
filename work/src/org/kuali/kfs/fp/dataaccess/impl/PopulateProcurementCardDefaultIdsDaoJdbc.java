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
