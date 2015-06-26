package org.kuali.kfs.sys.dataaccess.impl;

import org.kuali.kfs.sys.dataaccess.DocumentStatsDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentStatsDaoJdbc extends PlatformAwareDaoBaseJdbc implements DocumentStatsDao {
    public static final String SQL_NUM_DOCS_INITIATED = "select count(*) as c, krew_doc_typ_t.doc_typ_nm from krew_doc_hdr_t, krew_doc_typ_t where krew_doc_hdr_t.crte_dt > ? and krew_doc_hdr_t.doc_typ_id = krew_doc_typ_t.doc_typ_id group by krew_doc_typ_t.doc_typ_nm order by c desc limit ?";
    public static final String SQL_UNCOMPLETED_ACTIONS = "select krim_prncpl_t.prncpl_nm, count(*) as c from krew_actn_rqst_t join krim_prncpl_t on krew_actn_rqst_t.prncpl_id = krim_prncpl_t.prncpl_id where krew_actn_rqst_t.stat_cd = 'A' group by krim_prncpl_t.prncpl_nm order by c desc limit ?\n";
    public static final String SQL_COMPLETED_ACTIONS = "select krim_prncpl_t.prncpl_nm, count(*) as c from krew_actn_rqst_t join krim_prncpl_t on krew_actn_rqst_t.prncpl_id = krim_prncpl_t.prncpl_id where krew_actn_rqst_t.stat_cd = 'D' group by krim_prncpl_t.prncpl_nm order by c desc limit ?\n";

    @Override
    public List<Map<String, Integer>> reportNumInitiatedDocsByDocType(int limit, int days) throws SQLException {
        final List<Map<String, Integer>> results = new ArrayList<>();
        getJdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL_NUM_DOCS_INITIATED);
                ps.setTimestamp(1, new Timestamp(calculateDaysAgo(days).getTime().getTime()));
                ps.setInt(2, limit);
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

    protected Calendar calculateDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days * -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @Override
    public List<Map<String, Integer>> reportCompletedActionRequestsByPrincipal(int limit) throws SQLException {
        final List<Map<String, Integer>> results = new ArrayList<>();
        getJdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL_COMPLETED_ACTIONS);
                ps.setInt(1, limit);
                return ps;
            }
        }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Map<String, Integer> result = new ConcurrentHashMap<>();
                result.put(rs.getString("prncpl_nm"), rs.getInt(2));
                results.add(result);
            }
        });
        return results;
    }

    @Override
    public List<Map<String, Integer>> reportUncompletedActionRequstsByPrincipal(int limit) throws SQLException {
        final List<Map<String, Integer>> results = new ArrayList<>();
        getJdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL_UNCOMPLETED_ACTIONS);
                ps.setInt(1, limit);
                return ps;
            }
        }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Map<String, Integer> result = new ConcurrentHashMap<>();
                result.put(rs.getString("prncpl_nm"), rs.getInt(2));
                results.add(result);
            }
        });
        return results;
    }
}
