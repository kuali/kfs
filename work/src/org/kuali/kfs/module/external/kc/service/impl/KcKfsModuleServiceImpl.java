/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.kfs.sys.service.impl.KfsModuleServiceImpl;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class KcKfsModuleServiceImpl extends KfsModuleServiceImpl  {
    
    protected static final Logger LOG = Logger.getLogger(KcKfsModuleServiceImpl.class);

    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        Class<? extends ExternalizableBusinessObject> implementationClass = getExternalizableBusinessObjectImplementation(businessObjectClass);
        return (T) getExternalizableBusinessObjectService(implementationClass).findByPrimaryKey(fieldValues);
    }

    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(Class<T> businessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
        return getExternalizableBusinessObjectsList( businessObjectClass,fieldValues);
    }

    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(Class<T> businessObjectClass, Map<String, Object> fieldValues) {                
        Class<? extends ExternalizableBusinessObject> implementationClass = getExternalizableBusinessObjectImplementation(businessObjectClass);
        return (List<T>) getExternalizableBusinessObjectService(implementationClass).findMatching(fieldValues);                   
    }

    /**
     * Finds the business object service via the class to service mapping provided in the module configuration.
     * 
     * @param clazz
     * @return
     */
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
    
    /**
     * Finds the business object service via the class to service mapping provided in the module configuration.
     * 
     * @param clazz
     * @return
     */
    private List<ExternalizableBusinessObject> getExternalizableBusinessObjectServiceByMatching(Class clazz, Map<String, Object>fieldValues){
        String serviceName = null;
        ExternalizableBusinessObjectService eboService = null;
        
        Map<Class, String> externalizableBusinessObjectServices = ((KcFinancialSystemModuleConfiguration)getModuleConfiguration()).getExternalizableBusinessObjectServiceImplementations();
        
        if(ObjectUtils.isNotNull(externalizableBusinessObjectServices) && ObjectUtils.isNotNull(clazz)){
            serviceName = (String)externalizableBusinessObjectServices.get(clazz);
            if(ObjectUtils.isNotNull(serviceName)){
                eboService = (ExternalizableBusinessObjectService)SpringContext.getService(serviceName);
                return (List<ExternalizableBusinessObject>) eboService.findMatching(fieldValues);
            }
        }
        
        // used when EBO is not handled by a web service
        return (List<ExternalizableBusinessObject>) getBusinessObjectService().findMatching(clazz, fieldValues);

    }
    

    /**
     * Gets primary key fields from the Datadictionary entries for the object.
     * 
     * @see org.kuali.rice.krad.service.impl.ModuleServiceBase#listPrimaryKeyFieldNames(java.lang.Class)
     */
    public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
        List primaryKeys = null;
        Class clazz = getExternalizableBusinessObjectImplementation(businessObjectInterfaceClass);
        final org.kuali.rice.krad.datadictionary.BusinessObjectEntry boEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(clazz.getName());        
        if (boEntry == null) {
            return null;
        }
        return boEntry.getPrimaryKeys();
    }
    
    /**
     * Changing the base url to KC url
     * 
     * @see org.kuali.rice.krad.service.impl.ModuleServiceBase#getInquiryUrl(java.lang.Class)
     */
    protected String getInquiryUrl(Class inquiryBusinessObjectClass){
        String baseUrl = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.KC_APPLICATION_URL_KEY);
        String inquiryUrl = baseUrl;
        if (!inquiryUrl.endsWith("/")) {
            inquiryUrl = inquiryUrl + "/";
        }
        return inquiryUrl + "kr/" + KRADConstants.INQUIRY_ACTION;
    }

    /**
     * Mapping the kfs classes and parameters over to KC equivalents
     * 
     * @see org.kuali.rice.krad.service.impl.ModuleServiceBase#getUrlParameters(java.lang.String, java.util.Map)
     */
    protected Properties getUrlParameters(String businessObjectClassAttribute, Map<String, String[]> parameters){
        Properties urlParameters = new Properties();
        String paramNameToConvert = null;        
        Map<String, String> kfsToKcInquiryUrlParameterMapping = ((KcFinancialSystemModuleConfiguration)getModuleConfiguration()).getKfsToKcInquiryUrlParameterMapping();
        Map<String, String> kfsToKcInquiryUrlClassMapping = ((KcFinancialSystemModuleConfiguration)getModuleConfiguration()).getKfsToKcInquiryUrlClassMapping();
        
        for (String paramName : parameters.keySet()) {
            String parameterName = paramName;
            String[] parameterValues = parameters.get(paramName);
            
            if (parameterValues.length > 0) {
                //attempt to convert parameter name if necessary
                paramNameToConvert = businessObjectClassAttribute + "." + paramName;
                if( kfsToKcInquiryUrlParameterMapping.containsKey(paramNameToConvert) ){
                    parameterName = (String)kfsToKcInquiryUrlParameterMapping.get(paramNameToConvert);
                }
                urlParameters.put(parameterName, parameterValues[0]);
            }
        }
        
        if(kfsToKcInquiryUrlClassMapping.containsKey(businessObjectClassAttribute)){
            urlParameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, kfsToKcInquiryUrlClassMapping.get(businessObjectClassAttribute));
            urlParameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, 
            		KRADConstants.CONTINUE_WITH_INQUIRY_METHOD_TO_CALL);
            urlParameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.CONTINUE_WITH_INQUIRY_METHOD_TO_CALL);
            return urlParameters;
        } else {
            return super.getUrlParameters(businessObjectClassAttribute, parameters);
        }
    }
    
    
    protected List<ExternalizableBusinessObject> getExternalizableBusinessObjectsListForLookupCustom(Class businessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
        Class<? extends ExternalizableBusinessObject> implementationClass = getExternalizableBusinessObjectImplementation(businessObjectClass);
        String serviceName = null;
        ExternalizableBusinessObjectService eboService = null;
        
        Map<Class, String> externalizableBusinessObjectServices = ((KcFinancialSystemModuleConfiguration)getModuleConfiguration()).getExternalizableBusinessObjectServiceImplementations();
        
        if(ObjectUtils.isNotNull(externalizableBusinessObjectServices) && ObjectUtils.isNotNull(implementationClass)){
            serviceName = (String)externalizableBusinessObjectServices.get(implementationClass);
            if(ObjectUtils.isNotNull(serviceName)){
                eboService = (ExternalizableBusinessObjectService)SpringContext.getService(serviceName);
                return (List<ExternalizableBusinessObject>) eboService.findMatching(fieldValues);
            }
        }
        
        // used when EBO is not handled by a web service
        return super.getExternalizableBusinessObjectsListForLookup(businessObjectClass, fieldValues, unbounded);
        
    }
        
}