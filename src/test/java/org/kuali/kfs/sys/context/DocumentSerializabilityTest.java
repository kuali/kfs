/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.context;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.service.DataDictionaryService;

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
            DocumentEntry entry = documentEntries.get(documentEntryName);

            Set<String> unserializableFields = null;
            Class<?> testedClass;
            if (entry instanceof org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry) {
                testedClass = ((org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry)entry).getMaintainableClass();
                if ( testedClass == null ) {
                    errorMessage += "Maintenance Document entry: " + documentEntryName + " has a null maintainable class";
                } else {
                    unserializableFields = getUnserializableFieldsFromClass(testedClass);
                }
            } else if (entry instanceof org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry) {
                testedClass = ((org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry)entry).getDocumentClass();
                if ( testedClass == null ) {
                    errorMessage += "Transactional Document entry: " + documentEntryName + " has a null document class";
                } else {
                    unserializableFields = getUnserializableFieldsFromClass(testedClass);
                }
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
        assertTrue(errorMessage, StringUtils.isEmpty(errorMessage) );
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
        return java.io.Serializable.class.isAssignableFrom(field.getType())
                || java.util.Collection.class.isAssignableFrom(field.getType())
                || java.util.Map.class.isAssignableFrom(field.getType())
                || field.getName().equals("dataObject")
                || Modifier.isTransient(field.getModifiers())
                || Modifier.isStatic(field.getModifiers())
                || isPrimitive(field.getType());
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
