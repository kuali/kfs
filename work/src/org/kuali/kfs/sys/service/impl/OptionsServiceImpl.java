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


import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.Cacheable;

@NonTransactional
public class OptionsServiceImpl implements OptionsService {
    protected UniversityDateService universityDateService;

    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME, key="'CurrentFY'")
    public SystemOptions getCurrentYearOptions() {
        Integer fy = universityDateService.getCurrentFiscalYear();
        return SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(SystemOptions.class, fy);
    }

    @Override
    @Cacheable(value=SystemOptions.CACHE_NAME, key="#p0")
    public SystemOptions getOptions(Integer universityFiscalYear) {
        return SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


}
