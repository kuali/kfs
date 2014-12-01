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
    @Cacheable(value=SystemOptions.CACHE_NAME, key="'universityFiscalYear='+#p0")
    public SystemOptions getOptions(Integer universityFiscalYear) {
        return SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


}
