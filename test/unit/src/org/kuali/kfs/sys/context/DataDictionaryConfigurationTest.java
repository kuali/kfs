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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.KualiModule;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.JobDescriptor;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext
public class DataDictionaryConfigurationTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(DataDictionaryConfigurationTest.class);
    private static final String BASE_MODULE_PACKAGE_PREFIX = "org.kuali.module.";
    private static final String KFS_CORE_PACKAGE_PREFIX = "org.kuali.kfs.";
    private DataDictionary dataDictionary;
    private Map <String,Exception> dataDictionaryLoadFailures;
    private List<String> dataDictionaryNullEntries;
    private Map<String, KualiModule> modules;
    private KualiModule coreModule;
    private KualiModule kfsModule;
    Map<String, Set<String>> componentNamesByModule = new TreeMap();
    
    public void testLoadDataDictionaryConfiguration() throws Exception {
        loadDataDictionary();
        StringBuffer failureMessage = new StringBuffer("Unable to load DataDictionaryEntrys for some file locations:");
        for (String key : dataDictionaryLoadFailures.keySet()) {
            failureMessage.append("\n\t").append(new StringBuffer("Unable to load DataDictionaryEntry for key: ").append(key).append(" at location: ").append(dataDictionary.getFileLocationMap().get(key)));
        }
        StringBuffer warningMessage = new StringBuffer("DataDictionaryEntrys for some file locations are null (probably because the workflow document type name does not match the xml / bo name for the maintenance document):");
        for (String key : dataDictionaryNullEntries) {
            warningMessage.append("\n\t").append(key).append(" at location: ").append(dataDictionary.getFileLocationMap().get(key));
        }
        if (dataDictionaryNullEntries.size() > 0) {
            LOG.warn(warningMessage);
        }
        assertTrue(failureMessage.toString(), dataDictionaryLoadFailures.isEmpty());      
    }
    
    public void testGetComponentsByModule() throws Exception {
        loadDataDictionary();
        Map<String, Set<String>> componentNamesByClassName = SpringServiceLocator.getDataDictionaryService().getDataDictionary().getComponentNamesByClassName();
        for (String componentClassName : componentNamesByClassName.keySet()) {
            componentNamesByModule.get(getComponentModuleId(componentClassName)).addAll(componentNamesByClassName.get(componentClassName));
        }
        for (JobDescriptor jobDescriptor : SpringServiceLocator.getJobDescriptors()) {
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
    
    private void loadDataDictionary()  throws Exception {
        for (String key : dataDictionary.getFileLocationMap().keySet()) {
            try {
                DataDictionaryEntry entry = dataDictionary.getDictionaryObjectEntry(key);
                if (entry == null) {
                    dataDictionaryNullEntries.add(key);
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
        dataDictionary = SpringServiceLocator.getDataDictionaryService().getDataDictionary();
        dataDictionaryLoadFailures = new TreeMap();
        dataDictionaryNullEntries = new ArrayList();
        modules = new HashMap();
        coreModule = new KualiModule();
        coreModule.setModuleId("core");
        coreModule.setModuleCode(KFSConstants.CROSS_MODULE_CODE);
        coreModule.setModuleName(KFSConstants.CROSS_MODULE_NAME);
        modules.put(coreModule.getModuleId(), coreModule);
        componentNamesByModule.put(coreModule.getModuleId(), new TreeSet());
        kfsModule = new KualiModule();
        kfsModule.setModuleId("kfs");
        kfsModule.setModuleCode("FS");
        kfsModule.setModuleName("Financial System");
        modules.put(kfsModule.getModuleId(), kfsModule);
        componentNamesByModule.put(kfsModule.getModuleId(), new TreeSet());
        for (KualiModule module : SpringServiceLocator.getKualiModuleService().getInstalledModules()) {
            modules.put(module.getModuleId(), module);
            componentNamesByModule.put(module.getModuleId(), new TreeSet());
        }
    }
}
