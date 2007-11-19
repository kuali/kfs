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
package org.kuali.core.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.kuali.core.bo.Parameter;
import org.kuali.core.bo.ParameterDetailType;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.datadictionary.HeaderNavigation;
import org.kuali.core.datadictionary.HelpDefinition;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rules.ParameterRule;
import org.kuali.kfs.service.ParameterService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.PreCommitSuite;

@ConfigureContext
@AnnotationTestSuite(PreCommitSuite.class)
public class ParameterConfigurationTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(ParameterConfigurationTest.class);

    /**
     * 
     * This method...
     * @throws Exception
     */
    public void testValidateParameterComponents() throws Exception {
        Collection<Parameter> params = SpringContext.getBean(BusinessObjectService.class).findAll(Parameter.class);
        ParameterRule paramRule = new ParameterRule();
        StringBuffer badComponents = new StringBuffer();
        int failCount = 0;
        for (Parameter param : params) {
            if (!paramRule.checkComponent(param)) {
                badComponents.append("\n").append(param.getParameterNamespaceCode()).append("\t").append(param.getParameterDetailTypeCode()).append("\t").append(param.getParameterName()).append("\t");
                failCount++;
            }
        }
        badComponents.insert(0, "The following " + failCount + " parameters have invalid components:");
        if (failCount > 0) {
            Set<String> components = new TreeSet<String>();
            for (ParameterDetailType pdt : SpringContext.getBean(ParameterService.class).getNonDatabaseDetailTypes()) {
                components.add(pdt.getParameterNamespaceCode() + "/" + pdt.getParameterDetailTypeCode());
            }
            for (ParameterDetailType pdt : (Collection<ParameterDetailType>) SpringContext.getBean(BusinessObjectService.class).findAll(ParameterDetailType.class)) {
                components.add(pdt.getParameterNamespaceCode() + "/" + pdt.getParameterDetailTypeCode());
            }
            System.out.println("Valid Components: ");
            for (String component : components) {
                System.out.println(component);
            }
        }
        assertTrue(badComponents.toString(), failCount == 0);
    }
    
    /**
     * 
     * This method retrieves all the help parameters identified in the data dictionary classes and confirms that each parameter
     * exists in the parameters table.  Currently, help parameters are only defined for the following object types:
     * - Business Objects (defined by <objectHelp> in DD files)
     * - Maintenance or Transactional Documents (defined by <help> in DD files)
     * - Document Header Tab Navigation (defined by <pageHelp> in DD files)
     * 
     * 
     * @throws Exception
     */
    public void testAllDataDictionaryReferencedParametersExistInParameterTable() throws Exception {
        /**
         * Retrieve all business objects
         * For each object, check to see if it has a help definition defined. <objectHelp>
         * - If yes, attempt to pull parameter from DB using values defined in definition
         * - If no param found, add to error list and move on
         * 
         * Retrieve all documents
         * For each document, check to see if it has a help definition defined. <help>
         * - If yes, attempt to pull the parameter from DB using values in definition
         * - If no param found, add to error list and move on.
         * - For each document, check to see if it has a header page help definition defined. <pageHelp>
         * - - If yes, attempt to pull the parameter from DB using values in definition
         * - - If no param found, add to error list and move on.
         * 
         */

        // Force the datadictionary to load
        SpringContext.getBean(DataDictionaryService.class).forceCompleteDataDictionaryLoad();
        
        boolean exists = true;
        List<String> boParamsMissingFromDB = new ArrayList<String>();
        List<String> documentParamsMissingFromDB = new ArrayList<String>();
        List<String> documentHeaderParamsMissingFromDB = new ArrayList<String>();
        
        int documentHeaderCount=0;

        HashSet<BusinessObjectEntry> boEntries = new HashSet<BusinessObjectEntry>(SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntries().values());
        for(BusinessObjectEntry boEntry: boEntries) {
            HelpDefinition boHelp = boEntry.getHelpDefinition();
            if(ObjectUtils.isNotNull(boHelp)) {
                exists = SpringContext.getBean(ParameterService.class).parameterExists(boEntry.getBusinessObjectClass(), boHelp.getParameterName());
                if(!exists) {
                    boParamsMissingFromDB.add("Business Object Parameter: " + boHelp.getParameterNamespace() + " - " + boHelp.getParameterName() + " help parameter in " +boEntry.getBusinessObjectClass().getName()+ " is not in the database.");
                    exists = true;
                }
            }
        }

        // Using HashSet since duplicate objects would otherwise be returned
        HashSet<DocumentEntry> documentEntries = new HashSet(SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntries().values());
        List<String> ddEntriesWithMissingTypes = new ArrayList<String>();
        for (DocumentEntry documentEntry : documentEntries) {
            HelpDefinition docHelp = documentEntry.getHelpDefinition();
            if(ObjectUtils.isNotNull(docHelp)) {
                /*
                Class paramClass = null;
                if(documentEntry instanceof MaintenanceDocumentEntry) {
                    paramClass = ((MaintenanceDocumentEntry)documentEntry).getBusinessObjectClass();
                } 
                if(paramClass == null) {
                    paramClass = documentEntry.getDocumentClass();
                }
                exists = SpringContext.getBean(ParameterService.class).parameterExists(paramClass, docHelp.getParameterName());
                */
                
                HashMap<String, String> crit = new HashMap<String, String>(3);
                crit.put("parameterNamespaceCode", docHelp.getParameterNamespace());
                crit.put("parameterDetailTypeCode", docHelp.getParameterDetailType());
                crit.put("parameterName", docHelp.getParameterName());
                exists =  (Parameter) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Parameter.class, crit) != null;
                
                if(!exists) {
                    documentParamsMissingFromDB.add("Document Parameter: " + docHelp.getParameterNamespace() + " - " + docHelp.getParameterName() + " help parameter in " +documentEntry.getDocumentTypeName()+ " is not in the database.");
                    exists = true;
                }
            }
            List<HeaderNavigation> headerNavigations = Arrays.asList(documentEntry.getHeaderTabNavigation());
            for(HeaderNavigation headerNav : headerNavigations) {
                HelpDefinition headerNavHelp = headerNav.getHelpDefinition();
                if(ObjectUtils.isNotNull(headerNavHelp)) {
                    documentHeaderCount++;
                    
                    HashMap<String, String> crit = new HashMap<String, String>(3);
                    crit.put("parameterNamespaceCode", headerNavHelp.getParameterNamespace());
                    crit.put("parameterDetailTypeCode", headerNavHelp.getParameterDetailType());
                    crit.put("parameterName", headerNavHelp.getParameterName());
                    exists =  (Parameter) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Parameter.class, crit) != null;
                    
                    
                    if(!exists) {
                        documentHeaderParamsMissingFromDB.add("Document Header Parameter: " + headerNavHelp.getParameterNamespace() + " - " + headerNavHelp.getParameterName() + " help parameter in " +documentEntry.getDocumentTypeName()+ " is not in the database.");
                        exists = true;
                    }
                }
            }
        }

        /**
         * The below commented out code is useful when debugging the test locally, as it allows the errors to be posted
         * to general error output stream.
         */
        
//        Collections.sort(boParamsMissingFromDB);
//        Collections.sort(documentParamsMissingFromDB);
//        Collections.sort(documentHeaderParamsMissingFromDB);
//        
//        LOG.info("======== BO Missing Params ========");
//        for(String missingParam : boParamsMissingFromDB) {
//            LOG.info(missingParam);
//        }
//        
//        LOG.info("======== Document Missing Params ========");
//        for(String missingParam : documentParamsMissingFromDB) {
//            LOG.info(missingParam);
//        }
//
//        LOG.info("======== Document Header Missing Params ========");
//        for(String missingParam : documentHeaderParamsMissingFromDB) {
//            LOG.info(missingParam);
//        }
//        
//        LOG.info("Total number of business objects reviewed: "+boEntries.size());
//        LOG.info("Number of missing BO parameters: "+boParamsMissingFromDB.size());
//        LOG.info("Total number of documents reviewed: "+documentEntries.size());
//        LOG.info("Number of missing document parameters: "+documentParamsMissingFromDB.size());
//        LOG.info("Total number of documents headers reviewed: "+documentHeaderCount);
//        LOG.info("Number of missing document header parameters: "+documentHeaderParamsMissingFromDB.size());

        List<String> allMissingParams = new ArrayList<String>();
        
        allMissingParams.addAll(boParamsMissingFromDB);
        allMissingParams.addAll(documentParamsMissingFromDB);
        allMissingParams.addAll(documentHeaderParamsMissingFromDB);

        assertEquals("dataDictionaryReferencedParametersExistInParameterTable: " + allMissingParams, 0, allMissingParams.size());
        
//        assertEquals("dataDictionaryReferencedParametersExistInParameterTable: " + boParamsMissingFromDB, 0, boParamsMissingFromDB.size());
//        assertEquals("dataDictionaryReferencedParametersExistInParameterTable: " + documentParamsMissingFromDB, 0, documentParamsMissingFromDB.size());
//        assertEquals("dataDictionaryReferencedParametersExistInParameterTable: " + documentHeaderParamsMissingFromDB, 0, documentHeaderParamsMissingFromDB.size());
        
    }
    
}