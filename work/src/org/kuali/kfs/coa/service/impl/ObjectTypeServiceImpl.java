/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.service.impl;

import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.dao.ObjectTypeDao;
import org.kuali.module.chart.service.ObjectTypeService;

/**
 *  
 */

public class ObjectTypeServiceImpl implements ObjectTypeService {

    private ObjectTypeDao objectTypeDao;

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.chart.service.ObjectTypeService#getByPrimaryKey(java.lang.String)
     */
    public ObjectType getByPrimaryKey(String objectTypeCode) {
        return objectTypeDao.getByPrimaryKey(objectTypeCode);
    }

    /**
     * @param objectTypeDao The objectTypeDao to set.
     */
    public void setObjectTypeDao(ObjectTypeDao objectTypeDao) {
        this.objectTypeDao = objectTypeDao;
    }

}
