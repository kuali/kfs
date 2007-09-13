/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.datadictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.KualiModule;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.JobDescriptor;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class DataDictionaryConfigurationTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(DataDictionaryConfigurationTest.class);
    private static final String BASE_PACKAGE_PREFIX = "org.kuali.";
    private static final String BASE_MODULE_PACKAGE_PREFIX = BASE_PACKAGE_PREFIX + "module.";
    private static final String KFS_CORE_PACKAGE_PREFIX = BASE_PACKAGE_PREFIX + KFSConstants.KFS_MODULE_ID + ".";
    private DataDictionary dataDictionary;
    private Map <String,Exception> dataDictionaryLoadFailures;
    private Map<String,String> dataDictionaryWarnings;
    private Map<String, KualiModule> modules;
    private KualiModule coreModule;
    private KualiModule kfsModule;
    Map<String, Set<String>> componentNamesByModule = new TreeMap();
    
    public void testLoadDataDictionaryConfiguration() throws Exception {
        loadDataDictionary();
        StringBuffer failureMessage = new StringBuffer("Unable to load DataDictionaryEntrys for some file locations:");
        for (String key : dataDictionaryLoadFailures.keySet()) {
            failureMessage.append("\n\t").append("key: ").append(key).append(" at location: ").append(dataDictionary.getFileLocationMap().get(key)).append(" error: ").append(((Exception)dataDictionaryLoadFailures.get(key)).getMessage());
        }
        StringBuffer warningMessage = new StringBuffer("Loaded DataDictionaryEntrys for some file locations with warnings:");
        for (String key : dataDictionaryWarnings.keySet()) {
            warningMessage.append("\n\t").append("key: ").append(key).append(" at location: ").append(dataDictionary.getFileLocationMap().get(key)).append(" warning: ").append(dataDictionaryWarnings.get(key));
        }
        if (dataDictionaryWarnings.size() > 0) {
            System.err.print(warningMessage);
        }
        assertTrue(failureMessage.toString(), dataDictionaryLoadFailures.isEmpty());  
    }
    
    public void testGetComponentsByModule() throws Exception {
        loadDataDictionary();
        Map<String, Set<String>> componentNamesByClassName = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getComponentNamesByClassName();
        for (String componentClassName : componentNamesByClassName.keySet()) {
            componentNamesByModule.get(getComponentModuleId(componentClassName)).addAll(componentNamesByClassName.get(componentClassName));
        }
        for (JobDescriptor jobDescriptor : SpringContext.getBeansOfType(JobDescriptor.class).values()) {
            componentNamesByModule.get(getComponentModuleId(jobDescriptor.getSteps().get(0).getClass().getName())).add(getNiceJobName(jobDescriptor));
        }
        StringBuffer output = new StringBuffer("Components By Module:");
        for (String moduleId : componentNamesByModule.keySet()) {
            output.append("\n").append(modules.get(moduleId).getModuleCode()).append(" - ").append(modules.get(moduleId).getModuleName());
            for (String componentName : componentNamesByModule.get(moduleId)) {
                output.append("\n\t").append(componentName);
            }
        }
        LOG.info(output);
    }
    
    public void testAllDataDictionaryDocumentTypesExistInDocumentTypeTable() throws Exception {
        loadDataDictionary();
        List<String> documentTypeCodes = new ArrayList<String>();
        for (DocumentType type: (Collection<DocumentType>) SpringContext.getBean(BusinessObjectService.class).findAll(DocumentType.class)) {
            documentTypeCodes.add(type.getFinancialDocumentTypeCode());
        }
        Map<String, DocumentEntry> documentEntries = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntries();
        List<String> ddEntriesWithMissingTypes = new ArrayList<String>();
        for (DocumentEntry documentEntry: documentEntries.values()) {
            String code = documentEntry.getDocumentTypeCode();
            if (!documentTypeCodes.contains(code) && !"RUSR".equals(code)) {
                ddEntriesWithMissingTypes.add(code + " (" + documentEntry.getDocumentTypeName() + ")");
            }
        }

        assertEquals("dataDictionaryDocumentTypesNotDefinedInDatabase: " + ddEntriesWithMissingTypes, 0, ddEntriesWithMissingTypes.size());
    }

    
    private void loadDataDictionary()  throws Exception {
        for (String key : dataDictionary.getFileLocationMap().keySet()) {
            try {
                DataDictionaryEntry entry = dataDictionary.getDictionaryObjectEntry(key);
                if (entry == null) {
                    dataDictionaryWarnings.put(key, "DataDictionaryEntry is null");
                }
                else if ((entry instanceof BusinessObjectEntry) && !((BusinessObjectEntry)entry).getBusinessObjectClass().getSimpleName().equals(key)) {
                    dataDictionaryWarnings.put(key, "BusinessObjectEntry xml file name and business object class simple name are out of sync");                    
                }
                else if ((entry instanceof MaintenanceDocumentEntry) && (!(((MaintenanceDocumentEntry)entry).getBusinessObjectClass().getSimpleName() + "MaintenanceDocument").equals(key) || !((MaintenanceDocumentEntry)entry).getDocumentTypeName().equals(key)))  {
                    dataDictionaryWarnings.put(key, "MaintenanceDocumentEntry xml file name and business object class simple name or workflow document type name are out of sync");                                        
                }
                else if ((entry instanceof TransactionalDocumentEntry) && (!((TransactionalDocumentEntry)entry).getDocumentClass().getSimpleName().equals(key) || !((TransactionalDocumentEntry)entry).getDocumentTypeName().equals(key))) {
                    dataDictionaryWarnings.put(key, "TransactionalDocumentEntry xml file name and document class simple name or workflow document type name are out of sync");                                         
                }
            
            }
            catch (Exception e) {
                dataDictionaryLoadFailures.put(key, e);
            }
        }
    }
    
    private String getComponentModuleId(String className) {
        if (className.contains(BASE_MODULE_PACKAGE_PREFIX)) {
            return StringUtils.substringBefore(StringUtils.substringAfter(className, BASE_MODULE_PACKAGE_PREFIX), ".");
        }
        else if (className.contains(KFS_CORE_PACKAGE_PREFIX)){
            return kfsModule.getModuleId();
        }
        else {
            return coreModule.getModuleId();
        }
    }

    private String getNiceJobName(JobDescriptor jobDescriptor) {
        StringBuffer niceJobName = new StringBuffer();
        String jobName = StringUtils.capitalize(jobDescriptor.getJobDetail().getName());
        for (int i = 0; i < jobName.length(); i++) {
            if (Character.isLowerCase(jobName.charAt(i))) {
                niceJobName.append(jobName.charAt(i));
            }
            else {
                niceJobName.append(" ").append(jobName.charAt(i));
            }
        }
        return niceJobName.toString().trim();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
        dataDictionaryLoadFailures = new TreeMap();
        dataDictionaryWarnings = new TreeMap();
        modules = new HashMap();
        coreModule = new KualiModule();
        coreModule.setModuleId(KFSConstants.CROSS_MODULE_ID);
        coreModule.setModuleCode(KFSConstants.CROSS_MODULE_CODE);
        coreModule.setModuleName(KFSConstants.CROSS_MODULE_NAME);
        modules.put(coreModule.getModuleId(), coreModule);
        componentNamesByModule.put(coreModule.getModuleId(), new TreeSet());
        kfsModule = new KualiModule();
        kfsModule.setModuleId(KFSConstants.KFS_MODULE_ID);
        kfsModule.setModuleCode(KFSConstants.KFS_MODULE_CODE);
        kfsModule.setModuleName(KFSConstants.KFS_MODULE_NAME);
        modules.put(kfsModule.getModuleId(), kfsModule);
        componentNamesByModule.put(kfsModule.getModuleId(), new TreeSet());
        for (KualiModule module : SpringContext.getBean(KualiModuleService.class).getInstalledModules()) {
            modules.put(module.getModuleId(), module);
            componentNamesByModule.put(module.getModuleId(), new TreeSet());
        }
    }
}
