package org.kuali.kfs.sys.service;

import java.util.List;
import java.util.Map;

public interface DocumentStatsService {
    List<Map<String, Integer>> reportNumInitiatedDocsByDocType(int limit, int days);
    Map<String, Map<String, Integer>> reportCompletedActionRequestsByPrincipal(int limit);
    Map<String, Map<String, Integer>> reportUncompletedActionRequstsByPrincipal(int limit);
    Map<String, Integer> reportUncompletedActionRequestsByType();
    Map<String, Integer> reportCompletedActionRequestsByType();

    List<Map<String, Map<String, Integer>>> reportNumDocsByStatusByDocType(int limit, int days);

    Map<String,Integer> reportNumDocsByStatus(int days);
}
