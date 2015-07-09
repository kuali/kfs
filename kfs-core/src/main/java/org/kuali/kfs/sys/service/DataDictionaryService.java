package org.kuali.kfs.sys.service;

import java.util.List;
import java.util.Map;

public interface DataDictionaryService extends org.kuali.rice.kns.service.DataDictionaryService {
    List<Map<String, Object>> getBusinessObjectEntriesList();
}
