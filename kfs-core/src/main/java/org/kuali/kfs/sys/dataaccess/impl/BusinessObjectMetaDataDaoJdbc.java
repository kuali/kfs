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
