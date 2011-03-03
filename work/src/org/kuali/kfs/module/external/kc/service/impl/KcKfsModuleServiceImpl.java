/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.service.KcFinancialSystemModuleConfig;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsModuleServiceImpl;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.kns.datadictionary.RelationshipDefinition;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ObjectUtils;

public class KcKfsModuleServiceImpl  extends KfsModuleServiceImpl  {
    
    protected static final Logger LOG = Logger.getLogger(KcKfsModuleServiceImpl.class);
    private static Map<Class, Class> externalizedWebBOs = new HashMap<Class, Class>();
    //private static Map<Class, String> externalWebBusinessObjectPrimaryKeys = new HashMap<Class,String>();
    
    public <T extends ExternalizableBusinessObject> T retrieveExternalizableBusinessObjectIfNecessary(BusinessObject businessObject, T currentInstanceExternalizableBO, String externalizableRelationshipName) {
                
        if(businessObject==null) return null;
        
        Class<org.kuali.rice.kns.bo.ExternalizableBusinessObject> clazz;
        
        try{
            clazz = getExternalizableBusinessObjectImplementation(
                    PropertyUtils.getPropertyType(businessObject, externalizableRelationshipName));
        } catch(Exception iex){
            LOG.warn("Exception:"+iex+" thrown while trying to get property type for property:"+externalizableRelationshipName+
                    " from business object:"+businessObject);
            return null;
        }
        
        //Get the business object entry for this business object from data dictionary
        //using the class name (without the package) as key
        BusinessObjectEntry entry =
            KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntries().get(
                    businessObject.getClass().getSimpleName());
        RelationshipDefinition relationshipDefinition = entry.getRelationshipDefinition(externalizableRelationshipName);
        List<PrimitiveAttributeDefinition> primitiveAttributeDefinitions = relationshipDefinition.getPrimitiveAttributes();

        Map<String, Object> fieldValuesInEBO = new HashMap<String, Object>();
        Object sourcePropertyValue;
        Object targetPropertyValue = null;
        boolean sourceTargetPropertyValuesSame = true;
        for(PrimitiveAttributeDefinition primitiveAttributeDefinition: primitiveAttributeDefinitions){
            sourcePropertyValue = ObjectUtils.getPropertyValue(
                    businessObject, primitiveAttributeDefinition.getSourceName());
            if(currentInstanceExternalizableBO!=null)
                targetPropertyValue = ObjectUtils.getPropertyValue(currentInstanceExternalizableBO, primitiveAttributeDefinition.getTargetName());
            if(sourcePropertyValue==null){
                return null;
            } else if(targetPropertyValue==null || (targetPropertyValue!=null && !targetPropertyValue.equals(sourcePropertyValue))){
                sourceTargetPropertyValuesSame = false;
            }
            fieldValuesInEBO.put(primitiveAttributeDefinition.getTargetName(), sourcePropertyValue);
        }

        if(!sourceTargetPropertyValuesSame)
            return (T) getExternalizableBusinessObject(clazz, fieldValuesInEBO);
        return currentInstanceExternalizableBO;        
    };

    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {        
        return (T) getExternalizableBusinessObjectService(businessObjectClass).findByPrimaryKey(fieldValues);
    }

    @SuppressWarnings("unchecked")
    public List<? extends ExternalizableBusinessObject> retrieveExternalizableBusinessObjectsList(
            BusinessObject businessObject, String externalizableRelationshipName, Class externalizableClazz) {        
        if(businessObject==null) return null;
        //Get the business object entry for this business object from data dictionary
        //using the class name (without the package) as key
        String className = businessObject.getClass().getName();
        String key = className.substring(className.lastIndexOf(".")+1);
        BusinessObjectEntry entry =
            KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntries().get(key);
        RelationshipDefinition relationshipDefinition = entry.getRelationshipDefinition(externalizableRelationshipName);
        List<PrimitiveAttributeDefinition> primitiveAttributeDefinitions = relationshipDefinition.getPrimitiveAttributes();
        Map<String, Object> fieldValuesInEBO = new HashMap<String, Object>();
        Object sourcePropertyValue;
        for(PrimitiveAttributeDefinition primitiveAttributeDefinition: primitiveAttributeDefinitions){
            sourcePropertyValue = ObjectUtils.getPropertyValue(
                    businessObject, primitiveAttributeDefinition.getSourceName());
            if(sourcePropertyValue==null){
                return null;
            }
            fieldValuesInEBO.put(primitiveAttributeDefinition.getTargetName(), sourcePropertyValue);
        }
        return getExternalizableBusinessObjectsList(
                getExternalizableBusinessObjectImplementation(externalizableClazz), fieldValuesInEBO);
    }

    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(Class<T> businessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
        return getExternalizableBusinessObjectsList( businessObjectClass,fieldValues);
    }

    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        BusinessObject object = null;        
        Class clazz = getExternalizableBusinessObjectImplementation(businessObjectClass);
        return (List<T>) getExternalizableBusinessObjectService(clazz).findMatching(fieldValues);                   
    }

    private ExternalizableBusinessObjectService getExternalizableBusinessObjectService(Class clazz){
        String serviceName = null;
        ExternalizableBusinessObjectService eboService = null;
        
        Map<Class, String> externalizableBusinessObjectServices = ((KcFinancialSystemModuleConfiguration)getModuleConfiguration()).getExternalizableBusinessObjectServiceImplementations();
        
        if(ObjectUtils.isNotNull(externalizableBusinessObjectServices) && ObjectUtils.isNotNull(clazz)){
            serviceName = (String)externalizableBusinessObjectServices.get(clazz);
            eboService = (ExternalizableBusinessObjectService)SpringContext.getService(serviceName);            
        }
        
        return eboService;
    }

/*    protected Class getInterfaceToObj(Class ebo) {
        if (ebo.isInterface()) return ebo;
        return ebo.getInterfaces()[0];
    }
*/  

    public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass){
        Class clazz = getExternalizableBusinessObjectImplementation(businessObjectInterfaceClass);
        List primaryKeyFieldNames = new ArrayList();
        
        return primaryKeyFieldNames;
    }

  /*
    public BusinessObjectEntry getExternalizableBusinessObjectDictionaryEntry(Class businessObjectInterfaceClass) {
            Class boClass = businessObjectInterfaceClass;
            if(businessObjectInterfaceClass.isInterface()) {
                boClass = getExternalizableBusinessObjectImplementation(businessObjectInterfaceClass);
                if (boClass == null) return null;
                   DataDictionary dataDictionary = KNSServiceLocator.getDataDictionaryService().getDataDictionary();
                   Map<String, BusinessObjectEntry> boEntries = dataDictionary.getBusinessObjectEntries();
                   BusinessObjectEntry businessObjectEntry = boEntries.get(boClass.getName());
                   if (businessObjectEntry != null) return businessObjectEntry;
                   // try again but look for the simple name
                   businessObjectEntry = boEntries.get(boClass.getSimpleName());
                   return businessObjectEntry;
             }
            DataDictionary dataDictionary = KNSServiceLocator.getDataDictionaryService().getDataDictionary();
            Map<String, BusinessObjectEntry> boEntries = dataDictionary.getBusinessObjectEntries();
            BusinessObjectEntry businessObjectEntry = boEntries.get(boClass.getName());
            if (businessObjectEntry != null) return businessObjectEntry;
            businessObjectEntry = boEntries.get(boClass.getSimpleName());
            return businessObjectEntry;
        }
*/
/*  
    @Override
    public boolean isExternalizableBusinessObjectLookupable(Class boClass) {
        // TODO Auto-generated method stub
        Class boClassInterface = getInterfaceToObj(boClass);
        if (externalWebBusinessObjectPrimaryKeys.containsKey(boClassInterface)) {
            String primaryKey = externalWebBusinessObjectPrimaryKeys.get(boClassInterface);
            if (primaryKey.length() > 1) return true;
            return false;
         }
        return super.isExternalizableBusinessObjectLookupable(boClass);
    }
    
   public String getExternalizableBusinessObjectLookupUrl(Class inquiryBusinessObjectClass, Map<String, String> parameters) {
        Properties urlParameters = new Properties();

        String riceBaseUrl = KNSServiceLocator.getKualiConfigurationService().getPropertyString(KNSConstants.APPLICATION_URL_KEY);
        String lookupUrl = riceBaseUrl;
        if (!lookupUrl.endsWith("/")) {
            lookupUrl = lookupUrl + "/";
        }
        if (parameters.containsKey(KNSConstants.MULTIPLE_VALUE)) {
            lookupUrl = lookupUrl + "kr/" + KNSConstants.MULTIPLE_VALUE_LOOKUP_ACTION;
        }
        else {
            lookupUrl = lookupUrl + "kr/" + KNSConstants.LOOKUP_ACTION;
        }
        for (String paramName : parameters.keySet()) {
            urlParameters.put(paramName, parameters.get(paramName));
        }

        Class clazz = getExternalizableBusinessObjectImplementation(inquiryBusinessObjectClass);
        urlParameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, clazz==null?"":clazz.getName());

        return UrlFactory.parameterizeUrl(lookupUrl, urlParameters);
    }
*/
    
    @Override
    public void setModuleConfiguration(ModuleConfiguration moduleConfiguration) {   
          KcFinancialSystemModuleConfig kcModuleConfiguration = (KcFinancialSystemModuleConfig) moduleConfiguration;
          
          Map<Class,String> externalWebBusinessObjects = kcModuleConfiguration.getExternalizableBusinessObjectServiceImplementations();
 
          Iterable<Class> webos = externalWebBusinessObjects.keySet();
          if (webos != null) {
              Map<Class,Class> ebos = moduleConfiguration.getExternalizableBusinessObjectImplementations();
              for (Class webo : webos) {
                  if (ebos.containsKey(webo)) {
                      externalizedWebBOs.put(webo, ebos.get(webo));
                      externalizedWebBOs.put(ebos.get(webo), ebos.get(webo));
                      //externalWebBusinessObjectPrimaryKeys.put(webo, externalWebBusinessObjects.get(webo));
                   }
              }
          }
                    
          super.setModuleConfiguration(moduleConfiguration);
    }
    
}