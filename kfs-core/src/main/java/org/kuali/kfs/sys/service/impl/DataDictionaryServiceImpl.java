package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Overriden so that addDataDictionaryLocation passes the application context along....
 */
public class DataDictionaryServiceImpl extends org.kuali.rice.kns.service.impl.DataDictionaryServiceImpl implements ApplicationContextAware, DataDictionaryService {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addDataDictionaryLocation(String location) throws IOException {
        if (!ObjectUtils.isNull(applicationContext)) {
            getDataDictionary().addConfigFileLocation(location, applicationContext);
        } else {
            getDataDictionary().addConfigFileLocation(location);
        }
    }

    @Override
    public List<Map<String, Object>> getBusinessObjectEntriesList() {
        Map<String, BusinessObjectEntry> businessObjectEntries = getDataDictionary().getBusinessObjectEntries();
        List<Map<String, Object>> entries = new ArrayList<>();
        for (Map.Entry<String, BusinessObjectEntry> keyAndEntry : businessObjectEntries.entrySet()) {
            entries.add(convertEntryToMap(keyAndEntry.getKey(), keyAndEntry.getValue()));
        }
        return entries;
    }

    private Map<String, Object> convertEntryToMap(String key, BusinessObjectEntry businessObjectEntry) {
        Map<String, Object> entryMap = new ConcurrentHashMap<>();
        entryMap.put("key", key);
        entryMap.put("className", businessObjectEntry.getBusinessObjectClass().getName());
        entryMap.put("label", businessObjectEntry.getObjectLabel());
        entryMap.put("namespace", getKualiModuleService().getNamespaceCode(businessObjectEntry.getBusinessObjectClass()));
        Map<String, String> linkMap = new ConcurrentHashMap<>();
        linkMap.put("link", this.getKualiConfigurationService().getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY)+"/core/datadictionary/businessObjectEntry/"+key);
        entryMap.put("details", linkMap);
        return entryMap;
    }
}
