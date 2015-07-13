package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.businessobject.datadictionary.FinancialSystemBusinessObjectEntry;

import java.util.List;
import java.util.Map;

public interface DataDictionaryService extends org.kuali.rice.kns.service.DataDictionaryService {
    List<Map<String, Object>> getBusinessObjectEntriesList();


    void updateDictionaryEntry(FinancialSystemBusinessObjectEntry financialSystemBusinessObjectEntry);
}
