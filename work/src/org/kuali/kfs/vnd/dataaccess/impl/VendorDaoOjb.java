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
package org.kuali.kfs.vnd.dataaccess.impl;

import java.sql.Date;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.dataaccess.VendorDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * OJB implementation of VendorDao.
 */
public class VendorDaoOjb extends PlatformAwareDaoBaseOjb implements VendorDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VendorDaoOjb.class);

    public VendorContract getVendorB2BContract(VendorDetail vendorDetail, String campus, Date currentSqlDate) {

        Criteria header = new Criteria();
        Criteria detail = new Criteria();
        Criteria campusCode = new Criteria();
        Criteria beginDate = new Criteria();
        Criteria endDate = new Criteria();
        Criteria b2b = new Criteria();

        header.addEqualTo("VNDR_HDR_GNRTD_ID", vendorDetail.getVendorHeaderGeneratedIdentifier());
        detail.addEqualTo("VNDR_DTL_ASND_ID", vendorDetail.getVendorDetailAssignedIdentifier());
        campusCode.addEqualTo("VNDR_CMP_CD", campus);
        beginDate.addLessOrEqualThan("VNDR_CONTR_BEG_DT", currentSqlDate);
        endDate.addGreaterOrEqualThan("VNDR_CONTR_END_DT", currentSqlDate);
        b2b.addEqualTo("VNDR_B2B_IND", "Y");

        header.addAndCriteria(detail);
        header.addAndCriteria(campusCode);
        header.addAndCriteria(beginDate);
        header.addAndCriteria(endDate);
        header.addAndCriteria(b2b);

        VendorContract contract = (VendorContract) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(VendorContract.class, header));
        return contract;
    }

}
