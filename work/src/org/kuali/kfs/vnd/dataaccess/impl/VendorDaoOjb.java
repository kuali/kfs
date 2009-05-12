/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.vnd.dataaccess.impl;

import java.util.Date;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.dataaccess.VendorDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * OJB implementation of VendorDao.
 */
public class VendorDaoOjb extends PlatformAwareDaoBaseOjb implements VendorDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VendorDaoOjb.class);

    private DateTimeService dateTimeService;

    public VendorContract getVendorB2BContract(VendorDetail vendorDetail, String campus) {
        Date now = dateTimeService.getCurrentSqlDate();

        Criteria header = new Criteria();
        Criteria detail = new Criteria();
        Criteria campusCode = new Criteria();
        Criteria beginDate = new Criteria();
        Criteria endDate = new Criteria();
        Criteria b2b = new Criteria();
//        Criteria b2b_B = new Criteria();
//        Criteria b2b_N = new Criteria();

        header.addEqualTo("VNDR_HDR_GNRTD_ID", vendorDetail.getVendorHeaderGeneratedIdentifier());
        detail.addEqualTo("VNDR_DTL_ASND_ID", vendorDetail.getVendorDetailAssignedIdentifier());
        campusCode.addEqualTo("VNDR_CMP_CD", campus);
        beginDate.addLessOrEqualThan("VNDR_CONTR_BEG_DT", now);
        endDate.addGreaterOrEqualThan("VNDR_CONTR_END_DT", now);
        b2b.addEqualTo("VNDR_B2B_IND", "Y");
//        b2b.addOrCriteria(b2b_B);
//        b2b.addOrCriteria(b2b_N);

        header.addAndCriteria(detail);
        header.addAndCriteria(campusCode);
        header.addAndCriteria(beginDate);
        header.addAndCriteria(endDate);
        header.addAndCriteria(b2b);

        VendorContract contract = (VendorContract) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(VendorContract.class, header));
        return contract;
    }
        
    /* This method is equivalent to VendorDetail.isB2BVendor, but perhaps more efficient.
    public boolean isB2BVendor(VendorDetail vendorDetail) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("VNDR_HDR_GNRTD_ID", vendorDetail.getVendorHeaderGeneratedIdentifier());
        criteria.addEqualTo("VNDR_DTL_ASND_ID", vendorDetail.getVendorDetailAssignedIdentifier());
        criteria.addEqualTo("VNDR_B2B_IND", "Y");
        Collection<VendorContract> contracts = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(VendorContract.class, criteria));
        return contracts.size() > 0;
    }
    */
    
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
