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
package org.kuali.kfs.module.external.kc.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.kc.KcUnit;
import org.kuali.kfs.module.external.kc.dto.UnitDTO;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsModuleServiceImpl;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.BusinessObjectRelationship;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.XmlObjectSerializerService;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

public class UnitDTOExternalizableBusinessObjectService  extends KfsModuleServiceImpl {
    
    protected static final Logger LOG = Logger.getLogger(UnitDTOExternalizableBusinessObjectService.class);
    
    private static final Map<Class, Class> externalizedBOs;
    
    private Map<Class, BusinessObjectEntry> entries = null;
    
    static{
        externalizedBOs = new HashMap<Class, Class>(); 
        externalizedBOs.put(KcUnit.class, UnitDTO.class);     
        externalizedBOs.put(UnitDTO.class, UnitDTO.class);       
    }
   
    protected ModuleConfiguration moduleConfiguration;
    
    /**
     * @return the externalizableBusinessObjectImplementations
     */
    public Class getExternalizableBusinessObjectImplementation(Class externalizableBusinessObjectInterface) {
        return externalizedBOs.get(externalizableBusinessObjectInterface);
    }
    
  
    boolean hasPrinted = false;
    @Override
    public boolean isResponsibleFor(Class businessObjectClass) {
        /*if (!hasPrinted) {
            Map<Class, BusinessObjectEntry> entries = new HashMap<Class, BusinessObjectEntry>();
            for (Class c : externalizedBOs.keySet()) {
                Class implClass = externalizedBOs.get(c);
                BusinessObjectEntry boe = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(implClass.getName());
                entries.put(c, boe);
            }
            XmlObjectSerializerService x = SpringContext.getBean(XmlObjectSerializerService.class);
            LOG.error(x.toXml(entries));
            hasPrinted = true;
        }*/
        if (externalizedBOs.containsKey(businessObjectClass) == true) return true;
        return super.isResponsibleFor(businessObjectClass);
    }


    public boolean isExternalizable(Class businessObjectClass) {
        return externalizedBOs.containsValue(businessObjectClass);
    }

    public boolean isExternalizableBusinessObjectInquirable(Class businessObjectClass) {
        return externalizedBOs.containsValue(businessObjectClass);
    }
    
    public boolean isExternalizableBusinessObjectLookupable(Class businessObjectClass) {
        return externalizedBOs.containsValue(businessObjectClass);
    }

    /***
     * @see org.kuali.rice.kns.service.ModuleService#getExternalizableBusinessObject(java.lang.Class, java.util.Map)
     */
    public BusinessObject getExternalizableBusinessObject(
            Class externalizableBusinessObjectInterface, Map<String, Object> fieldValues) {
        return getBusinessObjectFromClass(getExternalizableBusinessObjectImplementation(externalizableBusinessObjectInterface));
    }

    private BusinessObject getBusinessObjectFromClass(Class clazz){
        if(clazz==null) return null;
        try{
            return (BusinessObject)clazz.newInstance();
        } catch(Exception ex){
          return null; //  return new ExternalCfdaDiffPackage();
        }
    }
    
    /***
     * @see org.kuali.rice.kns.service.ModuleService#getExternalizableBusinessObject(java.lang.Class, java.util.Map)
     */
    public <T extends org.kuali.rice.kns.bo.ExternalizableBusinessObject> java.util.List<T> getExternalizableBusinessObjectsList(java.lang.Class<T> externalizableexternalizableBusinessObjectInterface,
            java.util.Map<java.lang.String,java.lang.Object> fieldValues) {
        
        BusinessObject object = null;
        if(this.isResponsibleFor(externalizableexternalizableBusinessObjectInterface)) {
            object = getBusinessObjectFromClass( getExternalizableBusinessObjectImplementation(UnitDTO.class));       
        } else
            return super.getExternalizableBusinessObjectsList(
                    getExternalizableBusinessObjectImplementation(externalizableexternalizableBusinessObjectInterface), fieldValues);
        List returnList = new ArrayList();
       // returnList.add(object);
        returnList.add(object);
        return returnList;
    }

     /***
     * 
     * This method assumes that the property type for externalizable relationship in the business object is an interface
     * and gets the concrete implementation for it
     *  
     * @see org.kuali.rice.kns.service.ModuleService#retrieveExternalizableBusinessObjectIfNecessary(org.kuali.rice.kns.bo.BusinessObject, org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
     */
    public BusinessObject retrieveExternalizableBusinessObjectIfNecessary(
            BusinessObject businessObject, BusinessObject currentInstanceExternalizableBO, String externalizableRelationshipName) {
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
        return  this.getExternalizableBusinessObject(clazz, null);
    }

    /***
     * 
     * This method assumes that the externalizableClazz is an interface
     * and gets the concrete implementation for it
     * 
     * @see org.kuali.rice.kns.service.ModuleService#retrieveExternalizableBusinessObjectIfNecessary(org.kuali.rice.kns.bo.BusinessObject, org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
     */
 
    public List<? extends ExternalizableBusinessObject> retrieveExternalizableBusinessObjectsList(
            BusinessObject businessObject, String externalizableRelationshipName, Class externalizableClazz) {
        return (List<? extends ExternalizableBusinessObject>) getExternalizableBusinessObjectsList(null, null);
    }

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
        if(clazz == UnitDTO.class){
            primaryKeys.add("unitNumber");
        } 
        return primaryKeys;
    }
    public <T extends org.kuali.rice.kns.bo.ExternalizableBusinessObject> T getExternalizableBusinessObject(java.lang.Class<T> clazz,
            java.util.Map<java.lang.String,java.lang.Object> fieldValues) {
        if(clazz == UnitDTO.class){
            getBusinessObjectFromClass(clazz);
        } 
         return null;
     }


    
   public  <T extends org.kuali.rice.kns.bo.ExternalizableBusinessObject> java.util.List<T> getExternalizableBusinessObjectsListForLookup(java.lang.Class<T> clazz,
                java.util.Map<java.lang.String,java.lang.Object> fieldValues, boolean unbounded) {
       if(clazz == UnitDTO.class){
           return getExternalizableBusinessObjectsList( clazz,fieldValues);
       } 

        return null;
    }
}
