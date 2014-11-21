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
package org.kuali.kfs.sys.dataaccess.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.kuali.kfs.sys.dataaccess.FieldMetaData;
import org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiEncryptDecryptFieldConversion;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.MetaDataAccessException;

public class FieldMetaDataImpl implements DatabaseMetaDataCallback, FieldMetaData {
    private static final Logger LOG = Logger.getLogger(FieldMetaDataImpl.class);
    
    private Class businessObjectClass;
    private String propertyName;
    
    private String tableName;
    private String columnName;
    private String dataType;
    private int length;
    private int decimalPlaces;
    private boolean encrypted;

    public FieldMetaDataImpl(Class businessObjectClass, String propertyName) {
        this.businessObjectClass = businessObjectClass;
        this.propertyName = propertyName;
    }

    public Object processMetaData(DatabaseMetaData databaseMetaData) throws SQLException, MetaDataAccessException {
        Class workingBusinessObjectClass = businessObjectClass;
        String workingPropertyName = propertyName;
        while (workingPropertyName.contains(".")) {
            try {
                workingBusinessObjectClass = org.apache.ojb.broker.metadata.MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(workingBusinessObjectClass).getObjectReferenceDescriptorByName(workingPropertyName.substring(0, workingPropertyName.indexOf("."))).getItemClass();
            }
            catch (Exception e1) {
                LOG.debug(new StringBuffer("Unable to get property type via reference descriptor for property ").append(workingPropertyName.substring(0, workingPropertyName.indexOf("."))).append(" of BusinessObject class ").append(workingBusinessObjectClass).toString(), e1);
                try {
                    workingBusinessObjectClass = org.apache.ojb.broker.metadata.MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(workingBusinessObjectClass).getCollectionDescriptorByName(workingPropertyName.substring(0, workingPropertyName.indexOf("."))).getItemClass();                        
                }
                catch (Exception e2) {
                    LOG.debug(new StringBuffer("Unable to get property type via collection descriptor of property ").append(workingPropertyName.substring(0, workingPropertyName.indexOf("."))).append(" of BusinessObject class ").append(workingBusinessObjectClass).toString(), e2);
                    BusinessObject businessObject = null;
                    try {
                        businessObject = (BusinessObject)workingBusinessObjectClass.newInstance();
                    }
                    catch (Exception e3) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Unable to instantiate BusinessObject class " + workingBusinessObjectClass, e3);
                        }
                        return populateAndReturnNonPersistableInstance();
                    }
                    try {
                        workingBusinessObjectClass = PropertyUtils.getPropertyType(businessObject, workingPropertyName.substring(0, workingPropertyName.indexOf(".")));
                    }
                    catch (Exception e4) {
                        LOG.debug(new StringBuffer("Unable to get type of property ").append(workingPropertyName.substring(0, workingPropertyName.indexOf("."))).append(" for BusinessObject class ").append(workingBusinessObjectClass).toString(), e4);
                        return populateAndReturnNonPersistableInstance();
                    }
                }
            }
            if (workingBusinessObjectClass == null) {
                return populateAndReturnNonPersistableInstance();
            }
            else {
                workingPropertyName = workingPropertyName.substring(workingPropertyName.indexOf(".") + 1);
            }
        }
        if (!PersistableBusinessObject.class.isAssignableFrom(workingBusinessObjectClass)) {
            return populateAndReturnNonPersistableInstance();
        }
        ClassDescriptor classDescriptor = org.apache.ojb.broker.metadata.MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(workingBusinessObjectClass);
        if (classDescriptor == null) {
            return populateAndReturnNonPersistableInstance();
        }
        tableName = classDescriptor.getFullTableName();
        if (classDescriptor.getFieldDescriptorByName(workingPropertyName) == null) {
            return populateAndReturnNonPersistableInstance();
        }
        columnName = classDescriptor.getFieldDescriptorByName(workingPropertyName).getColumnName();
        ResultSet resultSet = databaseMetaData.getColumns(null, null, tableName, columnName);
        if (resultSet.next()) {
            dataType = resultSet.getString("TYPE_NAME");
            length = resultSet.getInt("COLUMN_SIZE");
            decimalPlaces = resultSet.getInt("DECIMAL_DIGITS");
            encrypted = classDescriptor.getFieldDescriptorByName(workingPropertyName).getFieldConversion() instanceof OjbKualiEncryptDecryptFieldConversion;
        }
        resultSet.close();
        return this;
    }
    
    protected FieldMetaData populateAndReturnNonPersistableInstance() {
        tableName = "N/A";
        columnName = tableName;
        dataType = tableName;
        length = 0;
        decimalPlaces = 0;
        encrypted = false;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public int getLength() {
        return length;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public boolean isEncrypted() {
        return encrypted;
    }
}
