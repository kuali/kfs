/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.batch.dataaccess.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.batch.dataaccess.PreparedStatementCachingDao;
import org.kuali.rice.kns.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.kns.service.DateTimeService;

public abstract class AbstractPreparedStatementCachingDaoJdbc extends PlatformAwareDaoBaseJdbc implements PreparedStatementCachingDao {
    protected static final String RETRIEVE_PREFIX = "retrieve-";
    protected static final String INSERT_PREFIX = "insert-";
    protected static final String UPDATE_PREFIX = "update-";

    protected abstract class JdbcWrapper<T> {
        protected abstract void populateStatement(PreparedStatement preparedStatement) throws SQLException;

        void update(PreparedStatement preparedStatement) {
            try {
                populateStatement(preparedStatement);
                preparedStatement.executeUpdate();
            }
            catch (SQLException e) {
                throw new RuntimeException("AbstractUpdatingPreparedStatementCachingDaoJdbc.UpdatingJdbcWrapper encountered exception during getObject method for type: " + getClass().getTypeParameters()[0].getClass(), e);
            }
        }
    }

    protected abstract class RetrievingJdbcWrapper<T> extends JdbcWrapper {
        protected abstract T extractResult(ResultSet resultSet) throws SQLException;

        public T get() {
            T value = null;
            PreparedStatement statement = preparedStatementCache.get(RETRIEVE_PREFIX + getClass().getTypeParameters()[0].getClass());
            try {
                populateStatement(statement);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    value = extractResult(resultSet);
                    if (resultSet.next()) {
                        throw new RuntimeException("More that one row returned when selecting by primary key in AbstractRetrievingPreparedStatementCachingDaoJdbc.RetrievingJdbcWrapper for: " + getClass().getTypeParameters()[0].getClass());
                    }
                }
                resultSet.close();
            }
            catch (SQLException e) {
                throw new RuntimeException("AbstractRetrievingPreparedStatementCachingDaoJdbc.RetrievingJdbcWrapper encountered exception during getObject method for type: " + getClass().getTypeParameters()[0].getClass(), e);
            }
            return (T) value;
        }
    }

    protected abstract class InsertingJdbcWrapper<T> extends JdbcWrapper {
        public void execute() {
            update(preparedStatementCache.get(INSERT_PREFIX + getClass().getTypeParameters()[0].getClass()));
        }
    }

    protected abstract class UpdatingJdbcWrapper<T> extends JdbcWrapper {
        public void execute() {
            update(preparedStatementCache.get(UPDATE_PREFIX + getClass().getTypeParameters()[0].getClass()));
        }
    }

    protected Map<String, PreparedStatement> preparedStatementCache;
    protected DateTimeService dateTimeService;

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
            for (PreparedStatement preparedStatement : preparedStatementCache.values()) {
                preparedStatement.close();
            }
            preparedStatementCache = null;
        }
        catch (SQLException e) {
            throw new RuntimeException("Caught exception closing statements in CachingDaoJdbc destroy method", e);
        }
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
