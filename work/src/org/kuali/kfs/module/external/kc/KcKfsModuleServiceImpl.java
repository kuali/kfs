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
package org.kuali.kfs.module.external.kc;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.external.kc.service.KcFinancialSystemModuleConfig;
import org.kuali.kfs.sys.service.impl.KfsModuleServiceImpl;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.bo.ModuleConfiguration;

public class KcKfsModuleServiceImpl  extends KfsModuleServiceImpl  {
    
    protected static final Logger LOG = Logger.getLogger(KcKfsModuleServiceImpl.class);
    private static Map<Class, Class> externalizedWebBOs = new HashMap<Class, Class>();;

    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        if (! externalizedWebBOs.containsKey(businessObjectClass)) return super.getExternalizableBusinessObject(businessObjectClass, fieldValues);
        return (T) getBusinessObjectFromClass(getExternalizableBusinessObjectImplementation(businessObjectClass));
    }
     
    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        BusinessObject object = null;
        if (! externalizedWebBOs.containsValue(businessObjectClass)) return super.getExternalizableBusinessObjectsList(businessObjectClass, fieldValues);
        List returnList = new ArrayList();
        object = getBusinessObjectFromClass( getExternalizableBusinessObjectImplementation(businessObjectClass));   
        returnList.add(object);
        return returnList;
     }

    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(Class<T> businessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
        if (! externalizedWebBOs.containsValue(businessObjectClass)) return super.getExternalizableBusinessObjectsListForLookup(businessObjectClass, fieldValues,unbounded);
        return getExternalizableBusinessObjectsList( businessObjectClass,fieldValues);
     }

    /***
     * 
     * This method assumes that the externalizableClazz is an interface
     * and gets the concrete implementation for it
     * 
     * @see org.kuali.rice.kns.service.ModuleService#retrieveExternalizableBusinessObjectIfNecessary(org.kuali.rice.kns.bo.BusinessObject, org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
     */

    @SuppressWarnings("unchecked")
    public List<? extends ExternalizableBusinessObject> retrieveExternalizableBusinessObjectsList(
            BusinessObject businessObject, String externalizableRelationshipName, Class externalizableClazz) {
        if (! externalizedWebBOs.containsKey(businessObject)) return super.retrieveExternalizableBusinessObjectsList(businessObject, externalizableRelationshipName, externalizableClazz);
        return (List<? extends ExternalizableBusinessObject>) getExternalizableBusinessObjectsList(null, null);
    }
  
    /***
     * 
     * This method assumes that the property type for externalizable relationship in the business object is an interface
     * and gets the concrete implementation for it
     *  
     * @see org.kuali.rice.kns.service.ModuleService#retrieveExternalizableBusinessObjectIfNecessary(org.kuali.rice.kns.bo.BusinessObject, org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
     */

    public <T extends ExternalizableBusinessObject> T retrieveExternalizableBusinessObjectIfNecessary(BusinessObject businessObject, T currentInstanceExternalizableBO, String externalizableRelationshipName) {
        if (! externalizedWebBOs.containsKey(businessObject)) return super.retrieveExternalizableBusinessObjectIfNecessary(businessObject, currentInstanceExternalizableBO,externalizableRelationshipName);
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
        return  (T) this.getExternalizableBusinessObject(clazz, null);

    };
  

    /***
     * @see org.kuali.rice.kns.service.ModuleService#getExternalizableBusinessObject(java.lang.Class, java.util.Map)
     */

    private BusinessObject getBusinessObjectFromClass(Class clazz){
        if(clazz==null) return null;
        try{
            return (BusinessObject)clazz.newInstance();
        } catch(Exception ex){
          return null; //  return new ExternalCfdaDiffPackage();
        }
    }


  
    /***
     * 
     * This method assumes that the externalizableClazz is an interface
     * and gets the concrete implementation for it
     * 
     * @see org.kuali.rice.kns.service.ModuleService#retrieveExternalizableBusinessObjectIfNecessary(org.kuali.rice.kns.bo.BusinessObject, org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
     */

    public List listPrimaryKeyFieldNames(Class externalizableBusinessObjectInterface){
        int classModifiers = externalizableBusinessObjectInterface.getModifiers();
        if (! externalizedWebBOs.containsValue(externalizableBusinessObjectInterface)) return super.listPrimaryKeyFieldNames(externalizableBusinessObjectInterface);
        
        if (!Modifier.isInterface(classModifiers) && !Modifier.isAbstract(classModifiers)) {
            // the interface is really a non-abstract class
            return listPrimaryKeyNamesForConcreteClass(externalizableBusinessObjectInterface);
        }
        Class clazz = getExternalizableBusinessObjectImplementation(externalizableBusinessObjectInterface);
        List primaryKeys = listPrimaryKeyNamesForConcreteClass(clazz);
        if(primaryKeys!=null)  return primaryKeys;
        return super.listPrimaryKeyFieldNames(externalizableBusinessObjectInterface);
    }

    public List listPrimaryKeyNamesForConcreteClass(Class clazz){
        List primaryKeys = new ArrayList();
         return primaryKeys;
    }
    
  
    /**
     * @see org.kuali.rice.kns.service.impl.ModuleServiceBase#setModuleConfiguration(org.kuali.rice.kns.bo.ModuleConfiguration)
     */
    @Override
    public void setModuleConfiguration(ModuleConfiguration moduleConfiguration) {   
          KcFinancialSystemModuleConfig kcModuleConfiguration = (KcFinancialSystemModuleConfig) moduleConfiguration;
          List<Class> webos = kcModuleConfiguration.getExternalizableWebBusinessObjectImplementations();
          
          if (webos != null) {
              Map<Class,Class> ebos = moduleConfiguration.getExternalizableBusinessObjectImplementations();
              for (Class webo : webos) {
                  if (ebos.containsKey(webo)) {
                      externalizedWebBOs.put(webo, ebos.get(webo));
                  }
              }
          }
          super.setModuleConfiguration(moduleConfiguration);
    }

    /**
     * @see org.kuali.rice.kns.service.impl.ModuleServiceBase#isExternalizableBusinessObjectLookupable(java.lang.Class)
     */
    @Override
    public boolean isExternalizableBusinessObjectLookupable(Class boClass) {
        // TODO Auto-generated method stub
        return super.isExternalizableBusinessObjectLookupable(boClass);
    }

    /**
     * @see org.kuali.rice.kns.service.impl.ModuleServiceBase#isExternalizable(java.lang.Class)
     */
    @Override
    public boolean isExternalizable(Class boClazz) {
        // TODO Auto-generated method stub
        return super.isExternalizable(boClazz);
    }

    /**
     * @see org.kuali.rice.kns.service.impl.ModuleServiceBase#isResponsibleFor(java.lang.Class)
     */
    @Override
    public boolean isResponsibleFor(Class arg0) {
        // TODO Auto-generated method stub
        return super.isResponsibleFor(arg0);
    }

   
}