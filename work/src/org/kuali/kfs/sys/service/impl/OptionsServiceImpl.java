/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.service.impl;

import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.dao.OptionsDao;
import org.kuali.kfs.service.OptionsService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OptionsServiceImpl implements OptionsService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OptionsServiceImpl.class);

    private DateTimeService dateTimeService;
    private OptionsDao optionsDao;

    public Options getCurrentYearOptions() {
        LOG.debug("getCurrentYearOptions() started");

        Integer fy = dateTimeService.getCurrentFiscalYear();
        return optionsDao.getByPrimaryId(fy);
    }

    public Options getOptions(Integer universityFiscalYear) {
        LOG.debug("getOptions() started");

        return optionsDao.getByPrimaryId(universityFiscalYear);
    }

    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }

    public void setOptionsDao(OptionsDao od) {
        optionsDao = od;
    }
}
