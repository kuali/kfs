package org.kuali.kfs.sys.service;

import java.util.List;
import java.util.Map;

public interface DocumentStatsService {
    public List<Map<String, Integer>> reportNumInitiatedDocsByDocType(int limit, int days);

    public List<Map<String, Map<String, Integer>>> reportNumDocsByStatusByDocType(int limit, int days);


    public Map<String,Integer> reportNumDocsByStatus(int days);
}
