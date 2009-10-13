/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ld.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.dataaccess.LaborObjectDao;
import org.kuali.kfs.module.ld.service.LaborObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This interface provides its clients with access to labor object in the backend data store.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LaborObject
 */
@Transactional
public class LaborObjectServiceImpl implements LaborObjectService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborObjectServiceImpl.class);

    private LaborObjectDao laborObjectDao;

    /**
     * @see org.kuali.kfs.module.ld.service.LaborObjectService#findAllLaborObjectInPositionGroups(java.util.Map, java.util.List)
     */
    public Collection<LaborObject> findAllLaborObjectInPositionGroups(Map<String, Object> fieldValues, List<String> positionGroupCodes) {
        return laborObjectDao.findAllLaborObjectInPositionGroups(fieldValues, positionGroupCodes);
    }

    /**
     * Sets the laborObjectDao attribute value.
     * @param laborObjectDao The laborObjectDao to set.
     */
    public void setLaborObjectDao(LaborObjectDao laborObjectDao) {
        this.laborObjectDao = laborObjectDao;
    }
}
