/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.context;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemMaintenanceDocumentEntry;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.PreCommitSuite;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.InquiryDefinition;
import org.kuali.rice.kns.datadictionary.InquirySectionDefinition;
import org.kuali.rice.kns.datadictionary.LookupDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.KualiDefaultListableBeanFactory;

@AnnotationTestSuite(PreCommitSuite.class)
@ConfigureContext
public class DataDictionaryConfigurationTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(DataDictionaryConfigurationTest.class);
    private DataDictionary dataDictionary;

    public final static String KFS_PACKAGE_NAME_PREFIX = "org.kuali.kfs";
    public final static String BUSINESS_OBJECT_PATH_QUALIFIER = "businessobject/datadictionary";
    public final static String DOCUMENT_PATH_QUALIFIER = "document/datadictionary";
    public final static String RICE_PACKAGE_NAME_PREFIX = "org.kuali.rice";
    public final static String INACTIVATEABLE_INTERFACE_CLASS = MutableInactivatable.class.getName();
    public final static String ACTIVE_FIELD_NAME = "active";

    public void testAllDataDictionaryDocumentTypesExistInWorkflowDocumentTypeTable() throws Exception {
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
        HashSet<DocumentEntry> documentEntries = new HashSet<DocumentEntry>(dataDictionary.getDocumentEntries().values());
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
                String queryString = "select distinct doc_typ_nm from KREW_DOC_TYP_T"
                    +" where doc_typ_id in (select parnt_id from KREW_DOC_TYP_T"
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

    private final static List<String> INACTIVATEABLE_LOOKUP_IGNORE_CLASSES = new ArrayList<String>();
    static {
        // org.kuali.kfs.coa.businessobject.Account is excepted from testActiveFieldExistInLookupAndResultSection because it uses the active-derived Closed? indicator instead (KFSMI-1393)
        INACTIVATEABLE_LOOKUP_IGNORE_CLASSES.add( "org.kuali.kfs.coa.businessobject.Account" );
        INACTIVATEABLE_LOOKUP_IGNORE_CLASSES.add( "org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition" );
        INACTIVATEABLE_LOOKUP_IGNORE_CLASSES.add( "org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding" );
    }
    private static final List<String> INACTIVATEABLE_LOOKUP_IGNORE_PACKAGES = new ArrayList<String>();
    static {
        INACTIVATEABLE_LOOKUP_IGNORE_PACKAGES.add( "org.kuali.kfs.pdp.businessobject" );
        INACTIVATEABLE_LOOKUP_IGNORE_PACKAGES.add( "org.kuali.kfs.module.external.kc.businessobject" );
        INACTIVATEABLE_LOOKUP_IGNORE_PACKAGES.add( "org.kuali.kfs.module.endow.businessobject" );
    }

    public void testActiveFieldExistInLookupAndResultSection() throws Exception{
        List<Class<?>> noActiveFieldClassList = new ArrayList<Class<?>>();
        List<Class<?>> notImplementInactivatableList = new ArrayList<Class<?>>();
        List<Class<?>> defaultValueWrongList = new ArrayList<Class<?>>();

        for(org.kuali.rice.krad.datadictionary.BusinessObjectEntry kradBusinessObjectEntry:dataDictionary.getBusinessObjectEntries().values()){
            BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry) kradBusinessObjectEntry;
            if ( !businessObjectEntry.getBusinessObjectClass().getName().startsWith(RICE_PACKAGE_NAME_PREFIX)
                    && !INACTIVATEABLE_LOOKUP_IGNORE_CLASSES.contains(businessObjectEntry.getBusinessObjectClass().getName())
                    && !INACTIVATEABLE_LOOKUP_IGNORE_PACKAGES.contains(businessObjectEntry.getBusinessObjectClass().getPackage().getName()) ) {
                try {
                    LookupDefinition lookupDefinition = businessObjectEntry.getLookupDefinition();
                    // Class implements MutableInactivatable but active field not used on Lookup.
                    if(Class.forName(INACTIVATEABLE_INTERFACE_CLASS).isAssignableFrom(businessObjectEntry.getBusinessObjectClass())) {
                        if(lookupDefinition != null && !(lookupDefinition.getLookupFieldNames().contains(ACTIVE_FIELD_NAME) && lookupDefinition.getResultFieldNames().contains(ACTIVE_FIELD_NAME))){
                            noActiveFieldClassList.add(businessObjectEntry.getBusinessObjectClass());
                            if ( lookupDefinition.getLookupField(ACTIVE_FIELD_NAME) != null ) {
                                //Default must be 'Y' not 'true'
                                if (!StringUtils.equals(lookupDefinition.getLookupField(ACTIVE_FIELD_NAME).getDefaultValue(), "Y")) {
                                    defaultValueWrongList.add(businessObjectEntry.getBusinessObjectClass());
                                }
                            }
                        }
                    }else{
                        // Lookup show active flag, but class does not implement MutableInactivatable.
                        if(lookupDefinition != null && (lookupDefinition.getLookupFieldNames().contains(ACTIVE_FIELD_NAME) || lookupDefinition.getResultFieldNames().contains(ACTIVE_FIELD_NAME))){
                            notImplementInactivatableList.add(businessObjectEntry.getBusinessObjectClass());
                        }
                    }
                }
                catch (ClassNotFoundException e) {
                    throw(e);
                }
            }
        }
        String errorString = "";
        if (noActiveFieldClassList.size()!=0) errorString=errorString+"Missing Active Field: "+formatErrorStringGroupByModule(noActiveFieldClassList);
        if (notImplementInactivatableList.size()!=0) errorString=errorString+"Inactivatable not implemented: "+formatErrorStringGroupByModule(notImplementInactivatableList);
        if (defaultValueWrongList.size()!=0) errorString=errorString+"Wrong default value: "+formatErrorStringGroupByModule(defaultValueWrongList);
        assertEquals(errorString, 0, noActiveFieldClassList.size()+notImplementInactivatableList.size()+defaultValueWrongList.size());
    }
    private String formatErrorStringGroupByModule(List<Class<?>> failedList){
        Map<String,Set<String>> listMap = new HashMap<String, Set<String>>();
        String module = null;
        String itemName = null;
        for (Class<?> item :failedList){
            itemName=item.getName();
            module = itemName.substring(0, itemName.lastIndexOf('.'));
            if (!listMap.keySet().contains(module)){
                listMap.put(module, new HashSet<String>());
            }
            listMap.get(module).add(itemName.substring(itemName.lastIndexOf('.')+1));
        }
        String tempString="";
        for (String moduleName : listMap.keySet()){
            tempString = tempString+"Module :"+moduleName+"\n";
            for (String errorClass : (Set<String>)listMap.get(moduleName)){
                tempString = tempString + "     "+errorClass+"\n";
            }
        }
        return "\n"+tempString;
    }

    public void testAllBusinessObjectsHaveObjectLabel() throws Exception {
        List<Class<?>> noObjectLabelClassList = new ArrayList<Class<?>>();
        for(org.kuali.rice.krad.datadictionary.BusinessObjectEntry kradBusinessObjectEntry:dataDictionary.getBusinessObjectEntries().values()){
            BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry) kradBusinessObjectEntry;
            if (StringUtils.isBlank(businessObjectEntry.getObjectLabel())) {
                noObjectLabelClassList.add(businessObjectEntry.getBusinessObjectClass());
            }
        }
        assertEquals(noObjectLabelClassList.toString(), 0, noObjectLabelClassList.size());
    }

    public void testAllParentBeansAreAbstract() throws Exception {
        Field f = dataDictionary.getClass().getDeclaredField("ddBeans");
        f.setAccessible(true);
        KualiDefaultListableBeanFactory ddBeans = (KualiDefaultListableBeanFactory)f.get(dataDictionary);
        List<String> failingBeanNames = new ArrayList<String>();
        for ( String beanName : ddBeans.getBeanDefinitionNames() ) {
            BeanDefinition beanDef = ddBeans.getMergedBeanDefinition(beanName);
            String beanClass = beanDef.getBeanClassName();
            // skip Rice classes
            if ( beanClass != null && beanClass.startsWith("org.kuali.rice") ) {
                continue;
            }
            if ( (beanName.endsWith("-parentBean") || beanName.endsWith("-baseBean"))
                    && !beanDef.isAbstract() ) {
                failingBeanNames.add(beanName + " : " + beanDef.getResourceDescription()+"\n");
            }
        }
        assertEquals( "The following parent beans are not defined as abstract:\n" + failingBeanNames, 0, failingBeanNames.size() );
    }

    public void testBusinessObjectEntriesShouldHaveParentBeans() throws Exception {
        somethingShouldHaveParentBeans(BusinessObjectEntry.class, new ArrayList<String>());
    }

    public void testDocumentEntriesShouldHaveParentBeans() throws Exception {
        somethingShouldHaveParentBeans(DocumentEntry.class, new ArrayList<String>());
    }

    protected static final List<String> EXCLUDED_ATTRIBUTE_DEFINITIONS = new ArrayList<String>();
    static {
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "Country-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "County-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "State-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "PostalCode-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "PersonImpl-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "RoleMemberBo-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "KimAttributes-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "KimDocRoleMember-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "DocRoleMember-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "Responsibility-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "PermissionBo-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "PermissionImpl-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "UberPermission-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "ReviewResponsibility-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "ResponsibilityImpl-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "UberPermissionBo-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "RuleTemplateAttribute-" );
        EXCLUDED_ATTRIBUTE_DEFINITIONS.add( "-versionNumber" );
    }

    public void testAttributeDefinitionsShouldHaveParentBeans() throws Exception {
        somethingShouldHaveParentBeans(AttributeDefinition.class, EXCLUDED_ATTRIBUTE_DEFINITIONS);
    }

    public void testMaintenanceSectionsShouldHaveParentBeans() throws Exception {
        somethingShouldHaveParentBeans(MaintainableSectionDefinition.class, new ArrayList<String>());
    }

    public void testInquirySectionsShouldHaveParentBeans() throws Exception {
        somethingShouldHaveParentBeans(InquirySectionDefinition.class, new ArrayList<String>());
    }

    public void testLookupDefinitionsShouldHaveParentBeans() throws Exception {
        somethingShouldHaveParentBeans(LookupDefinition.class, new ArrayList<String>());
    }

    public void testInquiryDefinitionsShouldHaveParentBeans() throws Exception {
        somethingShouldHaveParentBeans(InquiryDefinition.class, new ArrayList<String>() );
    }

    protected boolean doesBeanNameMatchList( String beanName, List<String> exclusions ) {
        for ( String excl : exclusions ) {
            if ( beanName.contains(excl) ) {
                return true;
            }
        }
        return false;
    }

    protected void somethingShouldHaveParentBeans( Class<?> baseClass, List<String> exclusions ) throws Exception {
        Field f = dataDictionary.getClass().getDeclaredField("ddBeans");
        f.setAccessible(true);
        KualiDefaultListableBeanFactory ddBeans = (KualiDefaultListableBeanFactory)f.get(dataDictionary);
        List<String> failingBeanNames = new ArrayList<String>();

        for ( String beanName : ddBeans.getBeanDefinitionNames() ) {
            if ( doesBeanNameMatchList(beanName, exclusions)) {
                continue ;
            }
            BeanDefinition beanDef = ddBeans.getMergedBeanDefinition(beanName);
            String beanClass = beanDef.getBeanClassName();
            if ( beanClass == null ) {
                System.err.println( "ERROR: Bean " + beanName + " has a null class." );
            }
            if ( !beanDef.isAbstract()
                    && beanClass != null
                    && baseClass.isAssignableFrom(Class.forName(beanClass) ) ) {
                try {
                    BeanDefinition parentBean = ddBeans.getBeanDefinition(beanName + "-parentBean");
                } catch ( NoSuchBeanDefinitionException ex ) {
                    failingBeanNames.add(beanName + " : " + beanDef.getResourceDescription() +"\n");
                }
            }
        }
        assertEquals( "The following " + baseClass.getSimpleName() + " beans do not have \"-parentBean\"s:\n" + failingBeanNames, 0, failingBeanNames.size() );
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

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
    }
}
