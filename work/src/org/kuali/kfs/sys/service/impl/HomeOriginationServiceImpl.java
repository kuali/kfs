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
package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.sys.businessobject.HomeOrigination;
import org.kuali.kfs.sys.service.HomeOriginationService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.Cacheable;

public class HomeOriginationServiceImpl implements HomeOriginationService {
    protected BusinessObjectService businessObjectService;

    /**
     * Retrieves a HomeOrigination object. Currently, there is only a single, unique HomeOriginationCode record in the database.
     */
    @Override
    @Cacheable(value=HomeOrigination.CACHE_NAME, key="'{getHomeOrigination}'")
    public HomeOrigination getHomeOrigination() {
        // no, I'm not doing null checking - if this is missing, we have other problems
        return businessObjectService.findAll(HomeOrigination.class).iterator().next();
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
