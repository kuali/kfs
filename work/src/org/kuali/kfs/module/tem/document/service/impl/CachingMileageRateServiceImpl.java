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
package org.kuali.kfs.module.tem.document.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.document.service.CachingMileageRateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.Cacheable;

/*
 * So this guy exists so that we can get the calls to go trough spring and our @Cachable notation
 * takes effect.
 *
 */

public class CachingMileageRateServiceImpl implements CachingMileageRateService {
    private BusinessObjectService businessObjectService;

    @Override
    @Cacheable(value=MileageRate.CACHE_NAME, key="'findAllMileageRates'")
    public List<MileageRate> findAllMileageRates() {
        List<MileageRate> retval = new ArrayList<MileageRate>();
        for (MileageRate mileageRate : businessObjectService.findAll(MileageRate.class)) {
            retval.add(mileageRate);
        }
        return retval;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
