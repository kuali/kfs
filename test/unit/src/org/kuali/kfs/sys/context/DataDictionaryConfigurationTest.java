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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.test.ConfigureContext;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.PreCommitSuite;

@AnnotationTestSuite(PreCommitSuite.class)
@ConfigureContext
public class DataDictionaryConfigurationTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(DataDictionaryConfigurationTest.class);
    private DataDictionary dataDictionary;
    private Map<String, Exception> dataDictionaryLoadFailures;
    private Map<String, String> dataDictionaryWarnings;

    public void testLoadDataDictionaryConfiguration() throws Exception {
        loadDataDictionary();
        StringBuffer failureMessage = new StringBuffer("Unable to load DataDictionaryEntrys for some file locations:");
        for (String key : dataDictionaryLoadFailures.keySet()) {
            failureMessage.append("\n\t").append("key: ").append(key).append(" at location: ").append(dataDictionary.getFileLocationMap().get(key)).append(" error: ").append(((Exception) dataDictionaryLoadFailures.get(key)).getMessage());
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

    public void testAllDataDictionaryDocumentTypesExistInDocumentTypeTable() throws Exception {
        loadDataDictionary();
        List<String> documentTypeCodes = new ArrayList<String>();
        for (DocumentType type : (Collection<DocumentType>) SpringContext.getBean(BusinessObjectService.class).findAll(DocumentType.class)) {
            documentTypeCodes.add(type.getFinancialDocumentTypeCode());
        }
        // Using HashSet since duplicate objects would otherwise be returned
        HashSet<DocumentEntry> documentEntries = new HashSet(SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntries().values());
        List<String> ddEntriesWithMissingTypes = new ArrayList<String>();
        for (DocumentEntry documentEntry : documentEntries) {
            String code = documentEntry.getDocumentTypeCode();
            if (!documentTypeCodes.contains(code) && !"RUSR".equals(code) &&!"PRPL".equals(code)) { //PRPL is added here because two doc types reference it.  This should be fixed
                ddEntriesWithMissingTypes.add(code + " (" + documentEntry.getDocumentTypeName() + ")");
            }
            else {
                documentTypeCodes.remove(code);
            }
        }
        if (documentTypeCodes.size() > 0) {
            System.err.print("superfluousTypesDefinedInDatabase: " + documentTypeCodes);
        }

        assertEquals("dataDictionaryDocumentTypesNotDefinedInDatabase: " + ddEntriesWithMissingTypes, 0, ddEntriesWithMissingTypes.size());

    }

    public void testAllDataDicitionaryDocumentTypesExistInWorkflowDocumentTypeTable() throws Exception {
        loadDataDictionary();
        List<String> workflowDocumentTypeNames = new ArrayList<String>();
        DataSource mySource = SpringContext.getBean(DataSource.class);
        Connection dbCon = null;
        try {

            dbCon = mySource.getConnection();
            Statement dbAsk = dbCon.createStatement();
            ResultSet dbAnswer = dbAsk.executeQuery("select DOC_TYP_NM from EN_DOC_TYP_T where DOC_TYP_CUR_IND = 1");
            while (dbAnswer.next()) {
                String docName = dbAnswer.getString(1);
                if (StringUtils.isNotBlank(docName)) {
                    workflowDocumentTypeNames.add(docName);
                }
            }
        }
        catch (Exception e) {
            throw (e);
        }
        // Using HashSet since duplicate objects would otherwise be returned
        HashSet<DocumentEntry> documentEntries = new HashSet(SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntries().values());
        List<String> ddEntriesWithMissingTypes = new ArrayList<String>();
        for (DocumentEntry documentEntry : documentEntries) {
            String name = documentEntry.getDocumentTypeName();
            if (!workflowDocumentTypeNames.contains(name) && !"RiceUserMaintenanceDocument".equals(name)) {
                ddEntriesWithMissingTypes.add(name);
            }
            else {
                workflowDocumentTypeNames.remove(name);
            }
        }

        if (workflowDocumentTypeNames.size() > 0) {
            System.err.print("superfluousTypesDefinedInWorkflowDatabase: " + workflowDocumentTypeNames);
        }
        assertEquals("documentTypesNotDefinedInWorkflowDatabase: " + ddEntriesWithMissingTypes, 0, ddEntriesWithMissingTypes.size());
    }


    private void loadDataDictionary() throws Exception {
        for (String key : dataDictionary.getFileLocationMap().keySet()) {
            try {
                DataDictionaryEntry entry = dataDictionary.getDictionaryObjectEntry(key);
                if (entry == null) {
                    dataDictionaryWarnings.put(key, "DataDictionaryEntry is null");
                }
                else if ((entry instanceof BusinessObjectEntry) && !((BusinessObjectEntry) entry).getBusinessObjectClass().getSimpleName().equals(key)) {
                    dataDictionaryWarnings.put(key, "BusinessObjectEntry xml file name and business object class simple name are out of sync");
                }
                else if ((entry instanceof MaintenanceDocumentEntry) && (!(((MaintenanceDocumentEntry) entry).getBusinessObjectClass().getSimpleName() + "MaintenanceDocument").equals(key) || !((MaintenanceDocumentEntry) entry).getDocumentTypeName().equals(key))) {
                    dataDictionaryWarnings.put(key, "MaintenanceDocumentEntry xml file name and business object class simple name or workflow document type name are out of sync");
                }
                else if ((entry instanceof TransactionalDocumentEntry) && (!((TransactionalDocumentEntry) entry).getDocumentClass().getSimpleName().equals(key) || !((TransactionalDocumentEntry) entry).getDocumentTypeName().equals(key))) {
                    dataDictionaryWarnings.put(key, "TransactionalDocumentEntry xml file name and document class simple name or workflow document type name are out of sync");
                }

            }
            catch (Exception e) {
                dataDictionaryLoadFailures.put(key, e);
            }
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
        dataDictionaryLoadFailures = new TreeMap();
        dataDictionaryWarnings = new TreeMap();
    }
}
