package org.kuali.rice.krad.datadictionary;

import java.util.Map;
import java.util.Set;

public interface DictionaryIndex {
    Map<String, DocumentEntry> getDocumentEntries();

    Map<Class, DocumentEntry> getDocumentEntriesByBusinessObjectClass();

    Map<Class, DocumentEntry> getDocumentEntriesByMaintainableClass();

    Map<String, DataDictionaryEntry> getEntriesByJstlKey();

    public Map<Class, Set<InactivationBlockingMetadata>> getInactivationBlockersForClass();

    public Map<String, BusinessObjectEntry> getBusinessObjectEntries();

    void index();
}
