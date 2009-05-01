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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemMaintenanceDocumentEntry;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.PreCommitSuite;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.LookupDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;

@AnnotationTestSuite(PreCommitSuite.class)
@ConfigureContext
public class DataDictionaryConfigurationTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(DataDictionaryConfigurationTest.class);
    private DataDictionary dataDictionary;
    
    public final static String KFS_PACKAGE_NAME = "org.kuali.kfs";

    public void testAllDataDicitionaryDocumentTypesExistInWorkflowDocumentTypeTable() throws Exception {
        HashSet<String> workflowDocumentTypeNames = new HashSet<String>();
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
            String testName = new String(" ");
            if (documentEntry instanceof FinancialSystemMaintenanceDocumentEntry){
                testName=((FinancialSystemMaintenanceDocumentEntry)documentEntry).getBusinessObjectClass().getName();
            }else{
                testName=documentEntry.getDocumentClass().getName();
            }
            if (!workflowDocumentTypeNames.contains(name) && !"RiceUserMaintenanceDocument".equals(name) && !testName.contains("rice")) {
                ddEntriesWithMissingTypes.add(name);
            }
            else {
                workflowDocumentTypeNames.remove(name);
            }
        }

        if (workflowDocumentTypeNames.size() > 0) {
            try{
                //If documents are parent docs, then they aren't superfluous.
                String queryString = "select distinct doc_typ_nm from krew_doc_typ_t"
                    +" where doc_typ_id in (select parnt_id from krew_doc_typ_t"
                    +" where actv_ind = 1"
                    +" and cur_ind = 1)";
                Statement dbAsk = dbCon.createStatement();
                ResultSet dbAnswer = dbAsk.executeQuery(queryString);
                while (dbAnswer.next()) {
                    String docName = dbAnswer.getString(1);
                    if (StringUtils.isNotBlank(docName)) {
                        workflowDocumentTypeNames.remove(docName);
                    }
                }
            }catch (Exception e){
                throw (e);
            }
        
        System.err.print("superfluousTypesDefinedInWorkflowDatabase: " + workflowDocumentTypeNames);
    }
    assertEquals("documentTypesNotDefinedInWorkflowDatabase: " + ddEntriesWithMissingTypes, 0, ddEntriesWithMissingTypes.size());
}

    private final static Class[] INACTIVATEABLE_LOOKUP_IGNORE_CLASSES = new Class[] { Account.class, BudgetConstructionPosition.class, PendingBudgetConstructionAppointmentFunding.class };
    // org.kuali.kfs.coa.businessobject.Account is excepted from testActiveFieldExistInLookupAndResultSection because it uses the
    // active-derived Closed? indicator instead (KFSMI-1393)

    public void testActiveFieldExistInLookupAndResultSection() throws Exception{
        List<Class> noActiveFieldClassList = new ArrayList<Class>();
        
        List<Class> ignoreClasses = Arrays.asList(INACTIVATEABLE_LOOKUP_IGNORE_CLASSES);
        
        for(BusinessObjectEntry businessObjectEntry:dataDictionary.getBusinessObjectEntries().values()){
            if ( !businessObjectEntry.getBusinessObjectClass().getName().startsWith("org.kuali.rice")
                    && !ignoreClasses.contains(businessObjectEntry.getBusinessObjectClass())) {
                List<Class> iList = (List<Class>)Arrays.asList(businessObjectEntry.getBusinessObjectClass().getInterfaces());
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
    
    /**
     * checke if the business object data dictionaries still have summary property for beans. If so, report errors.
     * The error can be caused by the parent beans referenced by the tested bean definition.
     */
    public void testAllBusinessObjectsHaveNoSummaryProperty() throws Exception {
        Map<String, Set<String>> errorReport = new HashMap<String, Set<String>>();       
        for(BusinessObjectEntry businessObjectEntry:dataDictionary.getBusinessObjectEntries().values()){
            
            if(businessObjectEntry.getBusinessObjectClass().getName().startsWith(KFS_PACKAGE_NAME)) {            
                for(AttributeDefinition attributeDefinition : businessObjectEntry.getAttributes()) {                    
                    if(attributeDefinition.getSummary() != null) {
                        String boClassName = businessObjectEntry.getBusinessObjectClass().getName();
                        
                        reportErrorAttribute(errorReport, attributeDefinition, boClassName);
                    }
                }
            }
        }
        
        printReport(errorReport);
        assertEquals("Please see the more information in LOG/Console", 0, errorReport.size());
    }
    
    /**
     * checke if the business object data dictionaries still have description property for beans. If so, report errors.
     * The error can be caused by the parent beans referenced by the tested bean definition.
     */
    public void testAllBusinessObjectsHaveNoDescriptionProperty() throws Exception {
        Map<String, Set<String>> errorReport = new HashMap<String, Set<String>>();   
        for(BusinessObjectEntry businessObjectEntry:dataDictionary.getBusinessObjectEntries().values()){
            
            if(businessObjectEntry.getBusinessObjectClass().getName().startsWith(KFS_PACKAGE_NAME)) {            
                for(AttributeDefinition attributeDefinition : businessObjectEntry.getAttributes()) {                    
                    if(attributeDefinition.getDescription() != null) {
                        String boClassName = businessObjectEntry.getBusinessObjectClass().getName();
                        
                        reportErrorAttribute(errorReport, attributeDefinition, boClassName);
                    }
                }
            }
        }
        
        printReport(errorReport);       
        assertEquals("Please see the more information in LOG/Console", 0, errorReport.size());
    }
    
    /**
     * checke if the document data dictionaries still have summary property for beans. If so, report errors.
     * The error can be caused by the parent beans referenced by the tested bean definition.
     */
    public void testAllDocumentEntriesHaveNoSummaryProperty() throws Exception {
        Map<String, Set<String>> errorReport = new HashMap<String, Set<String>>();        
        for(DocumentEntry documentEntry:dataDictionary.getDocumentEntries().values()){
            
            if(documentEntry.getDocumentClass().getName().startsWith(KFS_PACKAGE_NAME)) {            
                for(AttributeDefinition attributeDefinition : documentEntry.getAttributes()) {                    
                    if(attributeDefinition.getSummary() != null) {
                        String boClassName = documentEntry.getDocumentClass().getName();
                        
                        reportErrorAttribute(errorReport, attributeDefinition, boClassName);
                    }
                }
            }
        }
        
        printReport(errorReport);        
        assertEquals("Please see the more information in LOG/Console", 0, errorReport.size());
    }
    
    /**
     * checke if the document data dictionaries still have description property for beans. If so, report errors.     
     * The error can be caused by the parent beans referenced by the tested bean definition.
     */
    public void testAllDocumentEntriesHaveNoDescriptionProperty() throws Exception {
        Map<String, Set<String>> errorReport = new HashMap<String, Set<String>>();       
        for(DocumentEntry documentEntry:dataDictionary.getDocumentEntries().values()){
            
            if(documentEntry.getDocumentClass().getName().startsWith(KFS_PACKAGE_NAME)) {            
                for(AttributeDefinition attributeDefinition : documentEntry.getAttributes()) {                    
                    if(attributeDefinition.getDescription() != null) {
                        String boClassName = documentEntry.getDocumentClass().getName();
                        
                        reportErrorAttribute(errorReport, attributeDefinition, boClassName);
                    }
                }
            }
        }
        
        printReport(errorReport);        
        assertEquals("Please see the more information in LOG/Console", 0, errorReport.size());
    }

    private void reportErrorAttribute(Map<String, Set<String>> reports, AttributeDefinition attributeDefinition, String boClassName) {
        Set<String> attributeSet = reports.containsKey(boClassName) ? reports.get(boClassName) : new TreeSet<String>(); 
        attributeSet.add(attributeDefinition.getName());
        reports.put(boClassName, attributeSet);
    }
    
    private StringBuilder convertReportsAsText(Map<String, Set<String>> reports) {
        StringBuilder reportsAsText = new StringBuilder();
        for(String key : new TreeSet<String>(reports.keySet())) {
            reportsAsText.append(key + "\n");
            for(String value : reports.get(key)) {
                reportsAsText.append("\t--").append(value).append("\n");
            }
        }
        return reportsAsText;
    }
    
    private void printReport(Map<String, Set<String>> reports) {
        StringBuilder reportsAsText = convertReportsAsText(reports);       
        System.out.println(reportsAsText);
        LOG.info("\n" + reportsAsText);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
    } 
}
