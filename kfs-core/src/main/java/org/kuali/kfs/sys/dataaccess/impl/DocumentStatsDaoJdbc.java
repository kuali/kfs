package org.kuali.kfs.sys.dataaccess.impl;

import org.kuali.kfs.sys.dataaccess.DocumentStatsDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.kew.stats.dao.impl.StatsDAOOjbImpl;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentStatsDaoJdbc extends PlatformAwareDaoBaseJdbc implements DocumentStatsDao {
    public static final String SQL_NUM_DOCS_INITIATED = "select count(*) as c, krew_doc_typ_t.doc_typ_nm from krew_doc_hdr_t, krew_doc_typ_t where krew_doc_hdr_t.crte_dt > ? and krew_doc_hdr_t.doc_typ_id = krew_doc_typ_t.doc_typ_id group by krew_doc_typ_t.doc_typ_nm";

    @Override
    public List<Map<String, Integer>> reportNumInitiatedDocsByDocType() throws SQLException {
        final List<Map<String, Integer>> results = new ArrayList<>();
        getJdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(StatsDAOOjbImpl.SQL_NUM_DOCS_INITIATED);
                ps.setTimestamp(1, new Timestamp(calculateThirtyDaysAgo().getTime().getTime()));
                return ps;
            }
        }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Map<String, Integer> result = new ConcurrentHashMap<>();
                result.put(rs.getString("doc_typ_nm"), rs.getInt(1));
                results.add(result);
            }
        });
        return results;
    }

    protected Calendar calculateThirtyDaysAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -29);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
