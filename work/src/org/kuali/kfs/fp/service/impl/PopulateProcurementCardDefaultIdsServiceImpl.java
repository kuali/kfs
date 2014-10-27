/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.fp.service.impl;

import org.kuali.kfs.fp.dataaccess.PopulateProcurementCardDefaultIdsDao;
import org.kuali.kfs.fp.service.PopulateProcurementCardDefaultIdsService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PopulateProcurementCardDefaultIdsServiceImpl implements PopulateProcurementCardDefaultIdsService {
    protected PopulateProcurementCardDefaultIdsDao populateProcurementCardDefaultIdsDao;

    /**
     * @see org.kuali.kfs.fp.service.PopulateProcurementCardDefaultIdsService#populateIdsOnProcurementCardDefaults()
     */
    @Override
    public void populateIdsOnProcurementCardDefaults() {
        getPopulateProcurementCardDefaultIdsDao().populateIdsOnProcurementCardDefaults();
    }

    public PopulateProcurementCardDefaultIdsDao getPopulateProcurementCardDefaultIdsDao() {
        return populateProcurementCardDefaultIdsDao;
    }

    public void setPopulateProcurementCardDefaultIdsDao(PopulateProcurementCardDefaultIdsDao populateProcurementCardDefaultIdsDao) {
        this.populateProcurementCardDefaultIdsDao = populateProcurementCardDefaultIdsDao;
    }
}