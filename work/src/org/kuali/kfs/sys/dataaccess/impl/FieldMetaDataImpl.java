/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.dataaccess.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.kuali.kfs.sys.dataaccess.FieldMetaData;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.util.OjbKualiEncryptDecryptFieldConversion;
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
                BusinessObject businessObject = (BusinessObject)workingBusinessObjectClass.newInstance();
                workingBusinessObjectClass = PropertyUtils.getPropertyType(businessObject, workingPropertyName.substring(0, workingPropertyName.indexOf(".")));
                if (Collection.class.isAssignableFrom(workingBusinessObjectClass)) {
                    workingBusinessObjectClass = org.apache.ojb.broker.metadata.MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(businessObject.getClass()).getCollectionDescriptorByName(workingPropertyName.substring(0, workingPropertyName.indexOf("."))).getItemClass();
                }
                if ((workingBusinessObjectClass == null) || !PersistableBusinessObject.class.isAssignableFrom(workingBusinessObjectClass)) {
                    setDummyValues();
                    return this;
                }
                else {
                    workingPropertyName = workingPropertyName.substring(workingPropertyName.indexOf(".") + 1);
                }
            }
            catch (Exception e) {
                LOG.error(new StringBuffer("Unable to traverse object tree for business object class ").append(businessObjectClass).append(" and propertyName ").append(propertyName).toString(), e);
                setDummyValues();
                return this;                
            }
        }

        ClassDescriptor classDescriptor = org.apache.ojb.broker.metadata.MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(workingBusinessObjectClass);
        if (classDescriptor != null) {
            tableName = classDescriptor.getFullTableName();
            if (classDescriptor.getFieldDescriptorByName(workingPropertyName) != null) {
                columnName = classDescriptor.getFieldDescriptorByName(workingPropertyName).getColumnName();
                ResultSet resultSet = databaseMetaData.getColumns(null, null, tableName, columnName);
                if (resultSet.next()) {
                    dataType = resultSet.getString("TYPE_NAME");
                    length = resultSet.getInt("COLUMN_SIZE");
                    decimalPlaces = resultSet.getInt("DECIMAL_DIGITS");
                    encrypted = classDescriptor.getFieldDescriptorByName(workingPropertyName).getFieldConversion() instanceof OjbKualiEncryptDecryptFieldConversion;
                }
            }
        }
        else {
            setDummyValues();
        }
        return this;
    }
    
    private void setDummyValues() {
        tableName = "N/A";
        columnName = tableName;
        dataType = tableName;
        length = 0;
        decimalPlaces = 0;
        encrypted = false;
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
