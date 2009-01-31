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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.document.validation.impl.ParameterRule;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.PreCommitSuite;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.bo.ParameterDetailType;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.HeaderNavigation;
import org.kuali.rice.kns.datadictionary.HelpDefinition;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.ObjectUtils;

@ConfigureContext(shouldCommitTransactions=true)
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
        System.out.println("Starting Component Validation");
        for (Parameter param : params) {
            try{
            if (!paramRule.checkComponent(param)) {
                //TODO The susequent line should be removed when KFSMI-1635 is completed
                if (param.getParameterDetailTypeCode().equals("Country")||param.getParameterDetailTypeCode().equals("State")||param.getParameterDetailTypeCode().equals("PostalCode")||param.getParameterDetailTypeCode().equals("RuleAttribute")||param.getParameterDetailTypeCode().equals("RuleTemplate")||param.getParameterDetailTypeCode().equals("DocumentType"))continue;
                if (param.getParameterNamespaceCode().startsWith("KR"))continue;
                badComponents.append("\n").append(param.getParameterNamespaceCode()).append("\t").append(param.getParameterDetailTypeCode()).append("\t").append(param.getParameterName()).append("\t");
                failCount++;
            }
            }catch (Exception e){
                badComponents.append("\n").append(e.getMessage()).append(param.getParameterNamespaceCode()).append("\t").append(param.getParameterDetailTypeCode()).append("\t").append(param.getParameterName()).append("\t");
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
        //SpringContext.getBean(DataDictionaryService.class).forceCompleteDataDictionaryLoad();
        
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
            List<HeaderNavigation> headerNavigations = documentEntry.getHeaderNavigationList();
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
