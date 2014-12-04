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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.DonorLabel;
import org.kuali.kfs.module.endow.document.service.DonorLabelService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class DonorLabelServiceImpl implements DonorLabelService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.DonorLabelService#getByPrimaryKey(java.lang.String)
     */
    public DonorLabel getByPrimaryKey(String code) {
        DonorLabel donorLabel = null;
        if (StringUtils.isNotBlank(code)) {
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(EndowPropertyConstants.KUALICODEBASE_CODE, code);

            donorLabel = (DonorLabel) businessObjectService.findByPrimaryKey(DonorLabel.class, criteria);
        }
        return donorLabel;
    }

    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
