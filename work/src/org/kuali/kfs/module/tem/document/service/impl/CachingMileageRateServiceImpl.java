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
