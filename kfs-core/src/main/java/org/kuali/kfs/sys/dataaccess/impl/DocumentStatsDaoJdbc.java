package org.kuali.kfs.sys.dataaccess.impl;

import org.kuali.kfs.sys.dataaccess.DocumentStatsDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.kew.api.action.ActionRequestType;
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
    private static final String SQL_UNCOMPLETED_ACTIONS_BY_PRINCIPAL = "select krim_prncpl_t.prncpl_nm, count(*) as c from krew_actn_rqst_t join krim_prncpl_t on krew_actn_rqst_t.prncpl_id = krim_prncpl_t.prncpl_id where krew_actn_rqst_t.stat_cd = 'A' group by krim_prncpl_t.prncpl_nm order by c desc limit ?\n";
    private static final String SQL_COMPLETED_ACTIONS_BY_PRINCIPAL = "select krim_prncpl_t.prncpl_nm, count(*) as c from krew_actn_rqst_t join krim_prncpl_t on krew_actn_rqst_t.prncpl_id = krim_prncpl_t.prncpl_id where krew_actn_rqst_t.stat_cd = 'D' group by krim_prncpl_t.prncpl_nm order by c desc limit ?\n";
    private static final String SQL_UNCOMPLETED_ACTION_REQUEST_TYPES_FOR_PRINCPAL = "select krew_actn_rqst_t.actn_rqst_cd, count(*) as c from krew_actn_rqst_t join krim_prncpl_t on krew_actn_rqst_t.prncpl_id = krim_prncpl_t.prncpl_id where krew_actn_rqst_t.stat_cd = 'A' and krim_prncpl_t.prncpl_nm = ? group by krew_actn_rqst_t.actn_rqst_cd order by c desc\n";
    private static final String SQL_COMPLETED_ACTION_REQUEST_TYPES_FOR_PRINCPAL = "select krew_actn_rqst_t.actn_rqst_cd, count(*) as c from krew_actn_rqst_t join krim_prncpl_t on krew_actn_rqst_t.prncpl_id = krim_prncpl_t.prncpl_id where krew_actn_rqst_t.stat_cd = 'D' and krim_prncpl_t.prncpl_nm = ? group by krew_actn_rqst_t.actn_rqst_cd order by c desc\n";
    private static final String SQL_UNCOMPLETED_ACTIONS_BY_REQUEST_TYPE = "select count(*) as c, actn_rqst_cd from krew_actn_rqst_t where stat_cd = 'A' group by actn_rqst_cd";
    private static final String SQL_COMPLETED_ACTIONS_BY_REQUEST_TYPE = "select count(*) as c, actn_rqst_cd from krew_actn_rqst_t where stat_cd = 'D' group by actn_rqst_cd";

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

    protected void processPrincipalRow(ResultSet rs, Map<String, Map<String, Integer>> results, String sql) throws SQLException {
        final String principalName = rs.getString("prncpl_nm");
        final Map<String, Integer> result = new ConcurrentHashMap<>();
        getJdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, principalName);
                return ps;
            }
        }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                final int count = rs.getInt(2);
                final String actionRequestType = ActionRequestType.fromCode(rs.getString("actn_rqst_cd")).getLabel();
                result.put(actionRequestType, count);
            }
        });
        results.put(principalName, result);
    }

    @Override
    public Map<String, Map<String, Integer>> reportCompletedActionRequestsByPrincipal(int limit) {
        final Map<String, Map<String,Integer>> results = new ConcurrentHashMap<>();
        getJdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL_COMPLETED_ACTIONS_BY_PRINCIPAL);
                ps.setInt(1, limit);
                return ps;
            }
        }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                processPrincipalRow(rs, results, SQL_COMPLETED_ACTION_REQUEST_TYPES_FOR_PRINCPAL);
            }
        });
        return results;
    }

    @Override
    public Map<String, Map<String, Integer>> reportUncompletedActionRequestsByPrincipal(int limit) {
        final Map<String, Map<String,Integer>> results = new ConcurrentHashMap<>();
        getJdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL_UNCOMPLETED_ACTIONS_BY_PRINCIPAL);
                ps.setInt(1, limit);
                return ps;
            }
        }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                processPrincipalRow(rs, results, SQL_UNCOMPLETED_ACTION_REQUEST_TYPES_FOR_PRINCPAL);
            }
        });
        return results;
    }

    @Override
    public Map<String, Integer> reportUncompletedActionRequestsByType() {
        final Map<String, Integer> result = new ConcurrentHashMap<>();
        getJdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL_UNCOMPLETED_ACTIONS_BY_REQUEST_TYPE);
                return ps;
            }
        }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                result.put(ActionRequestType.fromCode(rs.getString("actn_rqst_cd")).getLabel(), rs.getInt(1));
            }
        });
        return result;
    }

    @Override
    public Map<String, Integer> reportCompletedActionRequestsByType() {
        final Map<String, Integer> result = new ConcurrentHashMap<>();
        getJdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL_COMPLETED_ACTIONS_BY_REQUEST_TYPE);
                return ps;
            }
        }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                result.put(ActionRequestType.fromCode(rs.getString("actn_rqst_cd")).getLabel(), rs.getInt(1));
            }
        });
        return result;
    }
}
