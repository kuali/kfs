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
package org.kuali.kfs.gl.batch.dataaccess.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.kfs.gl.batch.dataaccess.ReconciliationDao;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.exception.ClassNotPersistableException;

/**
 * Uses OJB to determine the column name -> java attribute name mapping
 */
public class ReconciliationDaoOjb extends PlatformAwareDaoBaseOjb implements ReconciliationDao {

    private DescriptorRepository descriptorRepository;

    /**
     * Constructs a ReconciliationDaoOjb.java.
     */
    public ReconciliationDaoOjb() {
        MetadataManager metadataManager = MetadataManager.getInstance();
        descriptorRepository = metadataManager.getGlobalRepository();
    }

    /**
     * Converts a list of DB column names to a list of java attribute names. The returned list is the same size as arrap parameter
     * 
     * @param clazz a class for the OriginEntryFull class
     * @param columnNames an array of database columns
     * @param caseInsensitive whether to do matching
     * @return for every valid index in the return value and the array, the value in the array is the db column name, and the value
     *         in the list is the java attribute name
     * @see org.kuali.kfs.gl.batch.dataaccess.ReconciliationDao#convertDBColumnNamesToJavaName(java.lang.String[])
     */
    public List<String> convertDBColumnNamesToJavaName(Class<? extends OriginEntryFull> clazz, String[] columnNames, boolean caseInsensitive) {
        List<String> results = new ArrayList<String>();
        ClassDescriptor classDescriptor = getClassDescriptor(clazz);
        for (int i = 0; i < columnNames.length; i++) {
            results.add(convertDBColumnNameToJavaName(classDescriptor, columnNames[i], caseInsensitive));
        }
        return results;
    }

    /**
     * Returns the java attribute name corresponding to the column name
     * 
     * @param classDescriptor the origin entry class
     * @param columnName the DB column name
     * @param caseInsensitive whether to do case insensitive matching
     * @return the java attribute name
     */
    protected String convertDBColumnNameToJavaName(ClassDescriptor classDescriptor, String columnName, boolean caseInsensitive) {
        FieldDescriptor[] fields = classDescriptor.getFieldDescriptions();
        for (FieldDescriptor field : fields) {
            if (caseInsensitive && field.getColumnName().equalsIgnoreCase(columnName)) {
                return field.getAttributeName();
            }
            if (!caseInsensitive && field.getColumnName().equals(columnName)) {
                return field.getAttributeName();
            }
        }
        return null;
    }

    /**
     * Returns the OJB class descriptor
     * 
     * @param <E> an origin entry class
     * @param persistableClass the class
     * @return the class descriptor
     */
    protected <E extends OriginEntryFull> ClassDescriptor getClassDescriptor(Class<E> persistableClass) {
        if (persistableClass == null) {
            throw new IllegalArgumentException("invalid (null) object");
        }

        ClassDescriptor classDescriptor = null;
        DescriptorRepository globalRepository = getDescriptorRepository();
        try {
            classDescriptor = globalRepository.getDescriptorFor(persistableClass);
        }
        catch (ClassNotPersistenceCapableException e) {
            throw new ClassNotPersistableException("class '" + persistableClass.getName() + "' is not persistable", e);
        }

        return classDescriptor;
    }

    /**
     * Gets the descriptorRepository attribute.
     * 
     * @return Returns the descriptorRepository.
     */
    protected DescriptorRepository getDescriptorRepository() {
        return descriptorRepository;
    }

    /**
     * Sets the descriptorRepository attribute value.
     * 
     * @param descriptorRepository The descriptorRepository to set.
     */
    public void setDescriptorRepository(DescriptorRepository descriptorRepository) {
        this.descriptorRepository = descriptorRepository;
    }
}
