/*
 * Copyright 2007 The Kuali Foundation
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
