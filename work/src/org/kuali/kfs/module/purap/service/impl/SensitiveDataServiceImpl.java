/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignment;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignmentDetail;
import org.kuali.kfs.module.purap.dataaccess.SensitiveDataDao;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;

public class SensitiveDataServiceImpl implements SensitiveDataService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SensitiveDataServiceImpl.class);
    
    private SensitiveDataDao sensitiveDataDao;
    //private PurchaseOrderService purchaseOrderService;

    public SensitiveDataDao getSensitiveDataDao() {
        return sensitiveDataDao;
    }

    public void setSensitiveDataDao(SensitiveDataDao sensitiveDataDao) {
        this.sensitiveDataDao = sensitiveDataDao;
    }

    /**
     * @see org.kuali.kfs.integration.service.SensitiveDataService#getSensitiveDataByCode(java.lang.String)
     */
    public SensitiveData getSensitiveDataByCode(String sensitiveDataCode) {
        Map primaryKeys = new HashMap();
        primaryKeys.put("sensitiveDataCode", sensitiveDataCode);
        return (SensitiveData)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SensitiveData.class, primaryKeys);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.SensitiveDataService#getAllSensitiveDataEntries()
     */
    public List<SensitiveData> getAllSensitiveDatas() {
        List<SensitiveData> sensitiveDataEntries = new ArrayList<SensitiveData>();
        Collection sensitiveDataColl = SpringContext.getBean(BusinessObjectService.class).findAll(SensitiveData.class);
        for (Object sd: sensitiveDataColl) {
            sensitiveDataEntries.add((SensitiveData)sd);
        }
        return sensitiveDataEntries;
    }
    
    public List<SensitiveData> getSensitiveDatasAssignedByPoId(Integer poId) {
        LOG.debug("getSensitiveDataByPoId(Integer) started");        
        return sensitiveDataDao.getSensitiveDatasAssignedByPoId(poId);
    }

    public List<SensitiveData> getSensitiveDatasAssignedByReqId(Integer reqId) {
        LOG.debug("getSensitiveDataByReqId(Integer) started");        
        return sensitiveDataDao.getSensitiveDatasAssignedByReqId(reqId);
    }

    public void deletePurchaseOrderSensitiveDatas(Integer poId) {
        LOG.debug("deletePurchaseOrderSensitiveData(Integer) started");
        sensitiveDataDao.deletePurchaseOrderSensitiveDatas(poId);
    }

    public void savePurchaseOrderSensitiveDatas(List<PurchaseOrderSensitiveData> posds) {
        LOG.debug("savePurchaseOrderSensitiveData(List<PurchaseOrderSensitiveData>) started");
        sensitiveDataDao.savePurchaseOrderSensitiveDatas(posds);
    }

    public SensitiveDataAssignment getLastSensitiveDataAssignment(Integer poId) {
        LOG.debug("getSensitiveDataAssignment(Integer) started");
        return sensitiveDataDao.getLastSensitiveDataAssignment(poId);
    }
    
    public Integer getLastSensitiveDataAssignmentId(Integer poId) {
        LOG.debug("getSensitiveDataAssignmentId(Integer) started");
        return getLastSensitiveDataAssignment(poId).getSensitiveDataAssignmentIdentifier();
    }
    
    public void saveSensitiveDataAssignment(SensitiveDataAssignment sda) {
        LOG.debug("saveSensitiveDataAssignment(SensitiveDataAssignment) started");
        sensitiveDataDao.saveSensitiveDataAssignment(sda);
    }

    public void saveSensitiveDataAssignmentDetails(List<SensitiveDataAssignmentDetail> sdads) {
        LOG.debug("saveSensitiveDataAssignmentDetails(List<SensitiveDataAssignmentDetail>) started");
        sensitiveDataDao.saveSensitiveDataAssignmentDetails(sdads);
    }
    
}
