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
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;

public class KcKfsModuleServiceImpl  extends KfsModuleServiceImpl  {
    
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

    public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass){
        Class clazz = getExternalizableBusinessObjectImplementation(businessObjectInterfaceClass);
        List primaryKeyFieldNames = new ArrayList();        
        return primaryKeyFieldNames;
    }    
    
    public String getExternalizableBusinessObjectInquiryUrl(Class inquiryBusinessObjectClass, Map<String, String[]> parameters) {
        if(!ExternalizableBusinessObject.class.isAssignableFrom(inquiryBusinessObjectClass)) {
            return KNSConstants.EMPTY_STRING;
        }
        String businessObjectClassAttribute;
        if(inquiryBusinessObjectClass.isInterface()){
            Class implementationClass = getExternalizableBusinessObjectImplementation(inquiryBusinessObjectClass);
            if (implementationClass == null) {
                LOG.error("Can't find ExternalizableBusinessObject implementation class for interface " + inquiryBusinessObjectClass.getName());
                throw new RuntimeException("Can't find ExternalizableBusinessObject implementation class for interface " + inquiryBusinessObjectClass.getName());
            }
            businessObjectClassAttribute = implementationClass.getName();
        }else{
            LOG.warn("Inquiry was invoked with a non-interface class object " + inquiryBusinessObjectClass.getName());
            businessObjectClassAttribute = inquiryBusinessObjectClass.getName();
        }
        return UrlFactory.parameterizeUrl(
                getInquiryUrl(inquiryBusinessObjectClass),
                getUrlParameters(businessObjectClassAttribute, parameters));
    }


    protected String getInquiryUrl(Class inquiryBusinessObjectClass){
        String riceBaseUrl = KNSServiceLocator.getKualiConfigurationService().getPropertyString(KNSConstants.APPLICATION_URL_KEY);
        String inquiryUrl = riceBaseUrl;
        if (!inquiryUrl.endsWith("/")) {
            inquiryUrl = inquiryUrl + "/";
        }
        return inquiryUrl + "kr/" + KNSConstants.INQUIRY_ACTION;
    }

}