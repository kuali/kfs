/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.dataaccess.impl;

import org.kuali.kfs.sys.dataaccess.BusinessObjectMetaDataDao;
import org.kuali.kfs.sys.dataaccess.FieldMetaData;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

public class BusinessObjectMetaDataDaoJdbc extends PlatformAwareDaoBaseJdbc implements BusinessObjectMetaDataDao {

    public FieldMetaData getFieldMetaData(Class businessObjectClass, String propertyName) {
        try {
            return (FieldMetaData) JdbcUtils.extractDatabaseMetaData(getDataSource(), new FieldMetaDataImpl(businessObjectClass, propertyName));
        }
        catch (MetaDataAccessException e) {
            throw new RuntimeException(new StringBuffer("BusinessObjectMetaDataDaoJdbc unable to getFieldMetaData for businessObjectClass ").append(businessObjectClass).append(" propertyName ").append(propertyName).toString(), e);
        }
    }
}
