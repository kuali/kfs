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
package org.kuali.kfs.sys.batch.dataaccess.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.batch.dataaccess.PreparedStatementCachingDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class AbstractPreparedStatementCachingDaoJdbc extends PlatformAwareDaoBaseJdbc implements PreparedStatementCachingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractPreparedStatementCachingDaoJdbc.class);

    protected static final String RETRIEVE_PREFIX = "retrieve-";
    protected static final String INSERT_PREFIX = "insert-";
    protected static final String UPDATE_PREFIX = "update-";

    protected abstract class JdbcWrapper<T> {
        protected abstract void populateStatement(PreparedStatement preparedStatement) throws SQLException;

        void update(Class<T> type, PreparedStatement preparedStatement) {
            try {
                populateStatement(preparedStatement);
                preparedStatement.executeUpdate();
            }
            catch (SQLException e) {
                throw new RuntimeException("AbstractUpdatingPreparedStatementCachingDaoJdbc.UpdatingJdbcWrapper encountered exception during getObject method for type: " + type, e);
            }
        }
    }

    protected abstract class RetrievingJdbcWrapper<T> extends JdbcWrapper {
        protected abstract T extractResult(ResultSet resultSet) throws SQLException;

        public T get(Class<T> type) {
            T value = null;
            PreparedStatement statement = preparedStatementCache.get(RETRIEVE_PREFIX + type);
            try {
                populateStatement(statement);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    value = extractResult(resultSet);
                    if (resultSet.next()) {
                        throw new RuntimeException("More that one row returned when selecting by primary key in AbstractRetrievingPreparedStatementCachingDaoJdbc.RetrievingJdbcWrapper for: " + type);
                    }
                }
                resultSet.close();
            }
            catch (SQLException e) {
                throw new RuntimeException("AbstractRetrievingPreparedStatementCachingDaoJdbc.RetrievingJdbcWrapper encountered exception during getObject method for type: " + type, e);
            }
            return value;
        }
    }

    /**
     * Retrieve list jdbc objects
     */
    protected abstract class RetrievingListJdbcWrapper<T> extends JdbcWrapper {
        protected abstract T extractResult(ResultSet resultSet) throws SQLException;

        public List<T> get(Class<T> type) {
            List<T> resultList = new ArrayList<T>();
            PreparedStatement statement = preparedStatementCache.get(RETRIEVE_PREFIX + type);
            try {
                populateStatement(statement);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    resultList.add(extractResult(resultSet));
                }
                resultSet.close();
            }
            catch (SQLException e) {
                throw new RuntimeException("AbstractRetrievingPreparedStatementCachingDaoJdbc.RetrievingListJdbcWrapper encountered exception during getObject method for type: " + type, e);
            }
            return resultList;
        }
    }

    protected abstract class InsertingJdbcWrapper<T> extends JdbcWrapper {
        public void execute(Class<T> type) {
            update(type, preparedStatementCache.get(INSERT_PREFIX + type));
        }
    }

    protected abstract class UpdatingJdbcWrapper<T> extends JdbcWrapper {
        public void execute(Class<T> type) {
            update(type, preparedStatementCache.get(UPDATE_PREFIX + type));
        }
    }

    protected Map<String, PreparedStatement> preparedStatementCache;

    protected abstract Map<String, String> getSql();

    public void initialize() {
        preparedStatementCache = new HashMap<String, PreparedStatement>();
        try {
            for (String statementKey : getSql().keySet()) {
                preparedStatementCache.put(statementKey, getConnection().prepareStatement(getSql().get(statementKey)));
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Caught exception preparing statements in CachingDaoJdbc initialize method", e);
        }
    }

    public void destroy() {
        try {
            // this should never happen, but it did; so just in case.
            if (ObjectUtils.isNull(preparedStatementCache)) {
                LOG.error("preparedStatementCache is null. This shouldn't have happened.");
                return;
            }
            for (PreparedStatement preparedStatement : preparedStatementCache.values()) {
                preparedStatement.close();
            }
            preparedStatementCache = null;
        }
        catch (SQLException e) {
            throw new RuntimeException("Caught exception closing statements in CachingDaoJdbc destroy method", e);
        }
    }

}
