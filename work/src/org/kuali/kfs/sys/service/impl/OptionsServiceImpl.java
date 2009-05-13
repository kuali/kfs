/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.sys.service.impl;


import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.dataaccess.OptionsDao;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.util.spring.CacheNoCopy;
import org.kuali.rice.kns.util.spring.Cached;

@NonTransactional
public class OptionsServiceImpl implements OptionsService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OptionsServiceImpl.class);

    private OptionsDao optionsDao;
    private UniversityDateService universityDateService;

    @CacheNoCopy
    public SystemOptions getCurrentYearOptions() {
        LOG.debug("getCurrentYearOptions() started");
        Integer fy = universityDateService.getCurrentFiscalYear();
        return optionsDao.getByPrimaryId(fy);
    }

    public SystemOptions getOptions(Integer universityFiscalYear) {
        LOG.debug("getOptions() started");

        return optionsDao.getByPrimaryId(universityFiscalYear);
    }

    public void setOptionsDao(OptionsDao od) {
        optionsDao = od;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


}
