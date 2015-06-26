package org.kuali.kfs.sys.dataaccess;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DocumentStatsDao {
    public List<Map<String, Integer>> reportNumInitiatedDocsByDocType() throws SQLException;
}
