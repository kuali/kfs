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
package org.kuali.kfs.integration.kc.service.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.kc.KcUnit;
import org.kuali.kfs.module.external.kc.dto.UnitDTO;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.kfs.sys.service.impl.KfsModuleServiceImpl;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ExternalizableBusinessObjectUtils;

public class KcKfsModuleServiceImpl  extends KfsModuleServiceImpl {
    
    protected static final Logger LOG = Logger.getLogger(KcKfsModuleServiceImpl.class);
        
    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
       if(this.isResponsibleFor(businessObjectClass)) {
            getBusinessObjectFromClass(businessObjectClass);
        }
        return null; 

    }
     
    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        BusinessObject object = null;
        if(this.isResponsibleFor(businessObjectClass)) {
            object = getBusinessObjectFromClass( getExternalizableBusinessObjectImplementation(businessObjectClass));       
        } else
            return super.getExternalizableBusinessObjectsList(
                    getExternalizableBusinessObjectImplementation(businessObjectClass), fieldValues);
        List returnList = new ArrayList();
        returnList.add(object);
        return returnList;
    }

    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(Class<T> businessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
        if(this.isResponsibleFor(businessObjectClass)) {
            return getExternalizableBusinessObjectsList( businessObjectClass,fieldValues);
        } 

        return Collections.emptyList();
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
  
    
    /**
     * @return the externalizableBusinessObjectImplementations
     */
    /*
    public Class getExternalizableBusinessObjectImplementation(Class externalizableBusinessObjectInterface) {
        return externalizedBOs.get(externalizableBusinessObjectInterface);
    }
    */
    public Class getExternalizableBusinessObjectImplementation(Class businessObjectClass) {    
        if (ExternalizableBusinessObject.class.isAssignableFrom(businessObjectClass)) {
            Class externalizableBusinessObjectInterface = ExternalizableBusinessObjectUtils.determineExternalizableBusinessObjectSubInterface(businessObjectClass);
            if (externalizableBusinessObjectInterface != null) {
                Map<Class, Class> validEBOs = getModuleConfiguration().getExternalizableBusinessObjectImplementations();
                if (validEBOs != null) {
                    return validEBOs.get(externalizableBusinessObjectInterface);
                }
            }
        }
        return null;
    }
    
  
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
        if (!Modifier.isInterface(classModifiers) && !Modifier.isAbstract(classModifiers)) {
            // the interface is really a non-abstract class
            return listPrimaryKeyNamesForConcreteClass(externalizableBusinessObjectInterface);
        }
        Class clazz = getExternalizableBusinessObjectImplementation(externalizableBusinessObjectInterface);
        List primaryKeys = listPrimaryKeyNamesForConcreteClass(clazz);
        if(primaryKeys!=null)
            return primaryKeys;
        return KNSServiceLocator.getPersistenceStructureService().listPrimaryKeyFieldNames(externalizableBusinessObjectInterface);
    }

    public List listPrimaryKeyNamesForConcreteClass(Class clazz){
        List primaryKeys = new ArrayList();
        return primaryKeys;
    }
    
  
}