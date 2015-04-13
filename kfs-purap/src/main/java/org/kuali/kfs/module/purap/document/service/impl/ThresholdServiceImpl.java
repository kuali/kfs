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
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.businessobject.ReceivingThreshold;
import org.kuali.kfs.module.purap.document.dataaccess.ThresholdDao;
import org.kuali.kfs.module.purap.document.service.ThresholdService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ThresholdServiceImpl implements ThresholdService{

    private static Logger LOG = Logger.getLogger(ThresholdServiceImpl.class);

    private ThresholdDao dao;

    public void setThresholdDao(ThresholdDao dao) {
        this.dao = dao;
    }
    
    public Collection<ReceivingThreshold> findByChart(String chartCode) {
        return dao.findByChart(chartCode);
    }

    public Collection<ReceivingThreshold> findByChartAndFund(String chartCode, String fund) {
        return dao.findByChartAndFund(chartCode,fund);
    }

    public Collection<ReceivingThreshold> findByChartAndSubFund(String chartCode, String subFund) {
        return dao.findByChartAndSubFund(chartCode,subFund);
    }

    public Collection<ReceivingThreshold> findByChartAndCommodity(String chartCode, String commodity) {
        return dao.findByChartAndCommodity(chartCode,commodity);
    }

    public Collection<ReceivingThreshold> findByChartAndObjectCode(String chartCode, String objectCode) {
        return dao.findByChartAndObjectCode(chartCode,objectCode);
    }

    public Collection<ReceivingThreshold> findByChartAndOrg(String chartCode, String org) {
        return dao.findByChartAndOrg(chartCode,org);
    }

    public Collection<ReceivingThreshold> findByChartAndVendor(String chartCode, 
                                                      String vendorHeaderGeneratedIdentifier,
                                                      String vendorDetailAssignedIdentifier){
        return dao.findByChartAndVendor(chartCode,vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier);
    }

}
