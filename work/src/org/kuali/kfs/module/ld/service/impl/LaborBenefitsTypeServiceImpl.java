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
package org.kuali.kfs.module.ld.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.BenefitsType;
import org.kuali.kfs.module.ld.service.LaborBenefitsTypeService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class LaborBenefitsTypeServiceImpl implements LaborBenefitsTypeService {

    private BusinessObjectService businessObjectService;

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public Collection<BenefitsType> getBenefitsType() {

        Map fieldValues = new HashMap();

        return businessObjectService.findMatching(BenefitsType.class, fieldValues);
    }

}
