/*
 * Copyright 2009 The Kuali Foundation.
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry;
import org.kuali.rice.kns.service.DataDictionaryService;

@ConfigureContext
/**
 * Tests that every property on all KFS documents are either serializable or transient
 */
public class DocumentSerializabilityTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSerializabilityTest.class);
    /**
     * Tests that every property on all KFS documents are either serializable or transient
     */
    public void testAllDocumentsAreSerializable() {
        DataDictionary dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
        Map<String, DocumentEntry> documentEntries = dataDictionary.getDocumentEntries();
        Set<Class<?>> checkedClasses = new HashSet<Class<?>>();
        Set<Class<?>> unserializableClasses = new HashSet<Class<?>>();
        String errorMessage = "";
        
        for (String documentEntryName : documentEntries.keySet()) {
            final DocumentEntry entry = documentEntries.get(documentEntryName);
                
            final Set<String> unserializableFields;
            final Class<?> testedClass;
            if (entry instanceof MaintenanceDocumentEntry) {
                testedClass = ((MaintenanceDocumentEntry)entry).getMaintainableClass();
                unserializableFields = getUnserializableFieldsFromClass(testedClass);
            } else if (entry instanceof TransactionalDocumentEntry) {
                testedClass = ((TransactionalDocumentEntry)entry).getDocumentClass();
                unserializableFields = getUnserializableFieldsFromClass(testedClass);
            } else {
                unserializableFields = null;
                testedClass = null;
            }
            
            if (testedClass != null && !checkedClasses.contains(testedClass)) {
                checkedClasses.add(testedClass);
                if (unserializableFields != null && !unserializableFields.isEmpty()) {
                    errorMessage += "Class "+testedClass.getName()+" has unserializable fields: "+StringUtils.join(unserializableFields, ',')+"\n";
                    unserializableClasses.add(testedClass);
                }
            }
        }
        
        if (!StringUtils.isBlank(errorMessage)) {
            LOG.info(errorMessage);
        }
        assertTrue(errorMessage, unserializableClasses.isEmpty());
    }
    
    /**
     * Determines if any of the fields of the given class are unserializable 
     * @param clazz the class to check
     * @return a Set of field names, the unserializable field names
     */
    protected Set<String> getUnserializableFieldsFromClass(Class<?> clazz) {
        Set<String> unserializableFields = new HashSet<String>();
        
        if (clazz.getSuperclass() != null && !clazz.equals(java.lang.Object.class)) {
            unserializableFields.addAll(getUnserializableFieldsFromClass(clazz.getSuperclass()));
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!isSerializable(field)) {
                unserializableFields.add(field.getName());
            }
        }
        
        return unserializableFields;
    }
    
    /**
     * Determines if a given field is serializable
     * @param field the field to check
     * @return true if the field is serializable, false otherwise
     */
    protected boolean isSerializable(Field field) {
        return java.io.Serializable.class.isAssignableFrom(field.getType()) || java.util.Collection.class.isAssignableFrom(field.getType()) || java.util.Map.class.isAssignableFrom(field.getType()) || Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || isPrimitive(field.getType());
    }
    
    /**
     * Determines if the given class represents one of the eight Java primitives
     * @param clazz the class to check
     * @return true if the class represents a byte, short, int, long, char, double, float, or boolean; false otherwise
     */
    protected boolean isPrimitive(Class<?> clazz) {
        return Byte.TYPE.isAssignableFrom(clazz) || Short.TYPE.isAssignableFrom(clazz) || Integer.TYPE.isAssignableFrom(clazz) || Long.TYPE.isAssignableFrom(clazz) || Float.TYPE.isAssignableFrom(clazz) || Double.TYPE.isAssignableFrom(clazz) || Character.TYPE.isAssignableFrom(clazz) || Boolean.TYPE.isAssignableFrom(clazz);
    }
}