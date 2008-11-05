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
package org.kuali.kfs.sys.context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.PreCommitSuite;
import org.kuali.kfs.sys.suite.RelatesTo;
import org.kuali.kfs.sys.suite.RelatesTo.JiraIssue;
import org.kuali.rice.kns.bo.DocumentType;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.LookupDefinition;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;

@AnnotationTestSuite(PreCommitSuite.class)
@ConfigureContext
public class DataDictionaryConfigurationTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(DataDictionaryConfigurationTest.class);
    private DataDictionary dataDictionary;

    public void testAllDataDictionaryDocumentTypesExistInDocumentTypeTable() throws Exception {
        List<String> documentTypeCodes = new ArrayList<String>();
        for (DocumentType type : (Collection<DocumentType>) SpringContext.getBean(BusinessObjectService.class).findAll(DocumentType.class)) {
            documentTypeCodes.add(type.getDocumentTypeCode());
        }
        // Using HashSet since duplicate objects would otherwise be returned
        HashSet<DocumentEntry> documentEntries = new HashSet(dataDictionary.getDocumentEntries().values());
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
        List<String> workflowDocumentTypeNames = new ArrayList<String>();
        DataSource mySource = SpringContext.getBean(DataSource.class);
        Connection dbCon = null;
        try {

            dbCon = mySource.getConnection();
            Statement dbAsk = dbCon.createStatement();
            ResultSet dbAnswer = dbAsk.executeQuery("select DOC_TYP_NM from KREW_DOC_TYP_T where CUR_IND = 1");
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
        HashSet<DocumentEntry> documentEntries = new HashSet(dataDictionary.getDocumentEntries().values());
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
    
    private final static Class[] INACTIVATEABLE_LOOKUP_IGNORE_CLASSES = new Class[] { Account.class, BudgetConstructionPosition.class, PendingBudgetConstructionAppointmentFunding.class };
    // org.kuali.kfs.coa.businessobject.Account is excepted from testActiveFieldExistInLookupAndResultSection because it uses the active-derived Closed? indicator instead (KFSMI-1393)

    public void testActiveFieldExistInLookupAndResultSection() throws Exception{
        List<Class> noActiveFieldClassList = new ArrayList<Class>();
        
        List<Class> ignoreClasses = Arrays.asList(INACTIVATEABLE_LOOKUP_IGNORE_CLASSES);
        
        for(BusinessObjectEntry businessObjectEntry:dataDictionary.getBusinessObjectEntries().values()){
            if (!ignoreClasses.contains(businessObjectEntry.getBusinessObjectClass())) {
                List<Class> iList = Arrays.asList(businessObjectEntry.getBusinessObjectClass().getInterfaces());
                try {
                    if(iList.contains(Class.forName("org.kuali.rice.kns.bo.Inactivateable"))){
                        LookupDefinition lookupDefinition = businessObjectEntry.getLookupDefinition();
                        if(lookupDefinition != null && !(lookupDefinition.getLookupFieldNames().contains("active") && lookupDefinition.getLookupFieldNames().contains("active"))){
                            noActiveFieldClassList.add(businessObjectEntry.getBusinessObjectClass());
                        }
                    }
                }
                catch (ClassNotFoundException e) {
                    throw(e);
                }
            }
        }
        assertEquals(noActiveFieldClassList.toString(), 0, noActiveFieldClassList.size());
    }

    public void testAllBusinessObjectsHaveObjectLabel() throws Exception {
        List<Class> noObjectLabelClassList = new ArrayList<Class>();
        for(BusinessObjectEntry businessObjectEntry:dataDictionary.getBusinessObjectEntries().values()){
            if (StringUtils.isBlank(businessObjectEntry.getObjectLabel())) {
                noObjectLabelClassList.add(businessObjectEntry.getBusinessObjectClass());
            }
        }
        assertEquals(noObjectLabelClassList.toString(), 0, noObjectLabelClassList.size());
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
    }
    @RelatesTo(JiraIssue.KFSMI1545)
    public void testAllKFSDocumentNamesAgreeWithDataDictionaryLabel() throws Exception {
        String errorString = "";
        int badNameCount = 0;
        for (DocumentEntry documentEntry : dataDictionary.getDocumentEntries().values()) {
            String label = documentEntry.getLabel();
            String docType = documentEntry.getDocumentTypeCode();
            HashMap docFields = new HashMap();
            docFields.put("documentTypeCode", docType);
            String docName =  ((DocumentType) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DocumentType.class, docFields)).getDocumentName();
            if (!label.equals(docName)){
                errorString = errorString+"Name for doc type "+docType+" of "+docName+" does not match label, "+label+".\n"; 
                badNameCount = badNameCount + 1;
            }
        }
        assertTrue(badNameCount+" Doc Types exist with bad names: \n"+errorString, badNameCount == 0);   
    }
}
