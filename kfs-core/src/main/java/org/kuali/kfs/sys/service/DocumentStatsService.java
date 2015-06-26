package org.kuali.kfs.sys.service;

import java.util.List;
import java.util.Map;

public interface DocumentStatsService {
    public List<Map<String, Integer>> reportNumInitiatedDocsByDocType(int limit, int days);
    List<Map<String, Integer>> reportCompletedActionRequestsByPrincipal(int limit);
    List<Map<String, Integer>> reportUncompletedActionRequstsByPrincipal(int limit);
    Map<String, Integer> reportUncompletedActionRequestsByType();
    Map<String, Integer> reportCompletedActionRequestsByType();
}
