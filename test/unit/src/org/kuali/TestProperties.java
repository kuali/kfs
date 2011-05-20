/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.lookup.BusinessObjectFieldConverter;
import org.kuali.kfs.gl.web.TestDataGenerator;
import org.kuali.kfs.module.ld.util.TestDataLoader;

public class TestProperties {
    
    
    private Properties modSpecificProperties;
    private Properties appResProperties;
    
    
    /**
     * Constructs a TestProperties instance, with default file names
     */
    public TestProperties() {
        
       // Load ApplicationResources Properties
       loadAppResourceProperties();
       // Load ModuleSpecific Properties
       loadModSpecificProperties();       
        
    }
    
    
    /**
     * loads application resource properties
     */
    public void loadAppResourceProperties() {
        
        List<String> propertyFiles = new ArrayList<String>();
        propertyFiles.add("work/src/org/kuali/kfs/sys/ApplicationResources.properties");
        
        System.out.println("Loading Application Resources Properties");
        appResProperties = loadProperties(propertyFiles);
        
    }
    
    /**
     * loads all modules specific properties
     */
    public void loadModSpecificProperties() {
     
        List<String> propertyFiles = new ArrayList<String>();
        
        propertyFiles.add("work/src/org/kuali/kfs/module/ar/ar-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/module/bc/bc-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/module/cab/cab-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/module/cam/cam-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/module/cg/cg-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/coa/coa-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/module/ec/ec-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/module/endow/endow-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/fp/fp-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/gl/gl-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/module/external/kc/kc-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/module/ld/ld-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/pdp/pdp-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/module/purap/purap-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/sys/sys-resources.properties");
        propertyFiles.add("work/src/org/kuali/kfs/vnd/vnd-resources.properties");
        
        System.out.println("\nLoading Module Specific Properties");
        modSpecificProperties = loadProperties(propertyFiles);
        
        /*
        Iterator propsIter = appResProperties.keySet().iterator();      
        while (propsIter.hasNext()) {
            
            String propertyName = (String) propsIter.next();
            //System.out.println("propertyName: "+propertyName);
            String propertyValue = (String) appResProperties.get(propertyName);
            //System.out.println("propertyValue: "+propertyValue);
        }
        */
     
    }
 
    /**
     * 
     */
    public void compareProperties() {
        
        //All the properties of ModSpecific Properties should exist in AppResurces properties
        compareModSpecificToAppResourcesProps(modSpecificProperties,appResProperties);
        
      //All the properties of Application Resources Properties may not exist in ModSpecific properties, so print and validate manually
        compareAppResourcesToModSpecificProps(appResProperties, modSpecificProperties);
    }
    
    
    /**
     * 
     * @param modSpecificProperties
     * @param appResProperties
     */
    public void compareModSpecificToAppResourcesProps(Properties modSpecificProperties,Properties appResProperties) {
        
        System.out.println("\n ***** Comparison Of Module Specific and Application Resource Properties *****\n");
        boolean allPropetiesExists = true;
        Iterator propsIter = modSpecificProperties.keySet().iterator();
        
        while (propsIter.hasNext()) {
            
            String propertyName = (String) propsIter.next();
            String propertyValue = (String) modSpecificProperties.get(propertyName);
            
            // verify whether this property name and value exists in AppResources
            if (appResProperties.containsKey(propertyName)) {
                
                if (!appResProperties.containsValue(propertyValue)){
                    System.out.println("PropertyValue: "+propertyValue+"*** Does Not Exists in ApplicationResources Propeties");
                    allPropetiesExists = false;
                }
            }
            else {
                System.out.println("PropertyName: "+propertyName+"*** Does Not Exists in ApplicationResources Properties");
                allPropetiesExists = false;
            }
           
        }
        
        if (allPropetiesExists) {
            System.out.println("***** All Module Specific Properties Exists in Application Resource Properties");
        }
        else {
            System.out.println("***** Some Of Module Specific Properties Do Not Exist in Application Resource Properties");
        }
        
    }
    
    /**
     * 
     * @param appResProperties
     * @param modSpecificProperties
     */
    public void compareAppResourcesToModSpecificProps(Properties appResProperties,Properties modSpecificProperties) {
        
        System.out.println("\n ***** Comparison Of Application Resource and Module Specific Properties *****\n");
        boolean allPropetiesExists = true;
        Iterator propsIter = appResProperties.keySet().iterator();
        
        while (propsIter.hasNext()) {
            
            String propertyName = (String) propsIter.next();
            String propertyValue = (String) appResProperties.get(propertyName);
            
            // verify whether this property name and value exists in ModSpecific Properties
            if (modSpecificProperties.containsKey(propertyName)) {
                
                if (!modSpecificProperties.containsValue(propertyValue)){
                    System.out.println("PropertyValue: "+propertyValue+"*** Does Not Exists in ModuleSpecific Properties");
                    allPropetiesExists = false;
                }
            }
            else {
                System.out.println("PropertyName: "+propertyName+"*** Does Not Exists in ModuleSpecific Properties");
                allPropetiesExists = false;
            }
           
        }
        
        if (allPropetiesExists) {
            System.out.println("***** All Application Resource Properties Exists in Module Specific Properties");
        }
        else {
            System.out.println("***** Some Of Application Resource Properties Do Not Exist in Module Specific Properties");
        }
        
    }
    
    /**
     * This method loads the properties from the property file
     * 
     * @param propertiesFileName the name of file containing the properties
     * @return the properties that have been populated
     */
    private Properties loadProperties(String propertiesFileName) {
        Properties properties = new Properties();

        try {
            FileInputStream fileInputStream = new FileInputStream(propertiesFileName);
            properties.load(fileInputStream);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return properties;
    }
    
    /**
     * This method loads the properties from the multiple properties files
     * 
     * @param propertiesFileNames list of Files containing the properties
     * @return the properties that have been populated
     */
    private Properties loadProperties(List fileNames) {
        
        String fileName = null;
        FileInputStream fileInputStream = null;
        Properties properties = new Properties();
        System.out.println("Number of Files: "+fileNames.size());
        try {
            for (int i=0; i<fileNames.size(); i++) {
                
                fileName = (String)fileNames.get(i);
                fileInputStream = new FileInputStream(fileName);
                int numBefore = properties.size();
                properties.load(fileInputStream);
                int numAfter = properties.size();
                System.out.println("Number of Properties in "+fileNames.get(i)+": "+(numAfter-numBefore));
            }
            System.out.println("Total Number of Properties :"+properties.size());
            
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return properties;
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        TestProperties testProperties = new TestProperties();
        testProperties.compareProperties();

    }

}
