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
