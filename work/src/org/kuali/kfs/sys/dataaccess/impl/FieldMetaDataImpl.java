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

import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.kuali.kfs.sys.dataaccess.FieldMetaData;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.util.OjbKualiEncryptDecryptFieldConversion;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.MetaDataAccessException;

public class FieldMetaDataImpl implements DatabaseMetaDataCallback, FieldMetaData {
    private Class<BusinessObject> businessObjectClass;
    private String propertyName;
    
    private String tableName;
    private String columnName;
    private String dataType;
    private int length;
    private int decimalPlaces;
    private boolean encrypted;

    public FieldMetaDataImpl(Class<BusinessObject> businessObjectClass, String propertyName) {
        this.businessObjectClass = businessObjectClass;
        this.propertyName = propertyName;
    }

    public Object processMetaData(DatabaseMetaData databaseMetaData) throws SQLException, MetaDataAccessException {
        ClassDescriptor classDescriptor = org.apache.ojb.broker.metadata.MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(businessObjectClass);
        if (classDescriptor != null) {
            tableName = classDescriptor.getFullTableName();
            if (classDescriptor.getFieldDescriptorByName(propertyName) != null) {
                columnName = classDescriptor.getFieldDescriptorByName(propertyName).getColumnName();
                ResultSet resultSet = databaseMetaData.getColumns(null, null, tableName, columnName);
                if (resultSet.next()) {
                    dataType = resultSet.getString("TYPE_NAME");
                    length = resultSet.getInt("COLUMN_SIZE");
                    decimalPlaces = resultSet.getInt("DECIMAL_DIGITS");
                    encrypted = classDescriptor.getFieldDescriptorByName(propertyName).getFieldConversion() instanceof OjbKualiEncryptDecryptFieldConversion;
                }
            }
        }
        else {
            tableName = "N/A";
            columnName = tableName;
            dataType = tableName;
            length = 0;
            decimalPlaces = 0;
            encrypted = false;
        }
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
