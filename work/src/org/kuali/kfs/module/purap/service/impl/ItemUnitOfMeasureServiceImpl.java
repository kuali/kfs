/*
 * Copyright 2005-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.service.impl;

import org.kuali.kfs.module.purap.service.ItemUnitOfMeasureService;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the ItemUnitOfMeasureService. 
 * This is the default, Kuali delivered implementation. It's currently used for dwr.
 */
@Transactional
public class ItemUnitOfMeasureServiceImpl implements ItemUnitOfMeasureService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ItemUnitOfMeasureServiceImpl.class);

    private BusinessObjectService businessObjectService;
    
    /**
     * @see org.kuali.kfs.module.purap.service.ItemUnitOfMeasureService#getByPrimaryId(java.lang.String)
     */
    public UnitOfMeasure getByPrimaryId(String itemUnitOfMeasureCode) {
        UnitOfMeasure toBeRetrieved = new UnitOfMeasure();
        toBeRetrieved.setItemUnitOfMeasureCode(itemUnitOfMeasureCode.toUpperCase());
        UnitOfMeasure uom = (UnitOfMeasure)businessObjectService.retrieve( toBeRetrieved );
        return uom;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
