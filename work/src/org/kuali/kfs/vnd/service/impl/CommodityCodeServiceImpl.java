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
package org.kuali.kfs.vnd.service.impl;

import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.dataaccess.CommodityCodeDao;
import org.kuali.kfs.vnd.service.CommodityCodeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the CommodityCodeService. 
 * This is the default, Kuali delivered implementation. It's currently used for dwr and
 * for searching for wild card commodity code which is used by commodity code routing
 * rules.
 */
@Transactional
public class CommodityCodeServiceImpl implements CommodityCodeService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CommodityCodeServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private CommodityCodeDao commodityCodeDao;
    
    /**
     * @see org.kuali.module.vnd.service.CommodityCodeService#getByPrimaryId(java.lang.String)
     */
    public CommodityCode getByPrimaryId(String purchasingCommodityCode) {
        CommodityCode ccToBeRetrieved = new CommodityCode();
        ccToBeRetrieved.setPurchasingCommodityCode(purchasingCommodityCode.toUpperCase());
        CommodityCode cc = (CommodityCode)businessObjectService.retrieve( ccToBeRetrieved );
        return cc;
    }

    /**
     * @see org.kuali.kfs.vnd.service.CommodityCodeService#wildCardCommodityCodeExists(java.lang.String)
     */
    public boolean wildCardCommodityCodeExists(String wildCardCommodityCode) {
        return commodityCodeDao.wildCardCommodityCodeExists(wildCardCommodityCode);
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    public void setCommodityCodeDao(CommodityCodeDao commodityCodeDao) {
        this.commodityCodeDao = commodityCodeDao;    
    }

}
