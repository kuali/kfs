/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cg.document.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.businessobject.AgencyAddress;
import org.kuali.kfs.module.cg.dataaccess.AgencyAddressDao;
import org.kuali.kfs.module.cg.document.service.AgencyAddressService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This Class implements the services defined in AgencyAddressService.
 */
@Transactional
public class AgencyAddressServiceImpl implements AgencyAddressService {
    private AgencyAddressDao agencyAddressDao;

    private BusinessObjectService businessObjectService;
    private SequenceAccessorService sequenceAccessorService;
    private DateTimeService dateTimeService;
    protected static final String AGENCY_ADDR_ID_SEQ = "AGENCY_ADDR_ID_SEQ";

    /**
     * @see org.kuali.kfs.module.cg.document.service.AgencyAddressService#getByPrimaryKey(java.lang.String, java.lang.Integer)
     */
    @SuppressWarnings("unchecked")
    public AgencyAddress getByPrimaryKey(String agencyNumber, Integer agencyAddressIdentifier) {

        AgencyAddress agencyAddress = null;
        if (StringUtils.isNotBlank(agencyNumber) && ObjectUtils.isNotNull(agencyAddressIdentifier)) {
            Map criteria = new HashMap();
            criteria.put(KFSPropertyConstants.AGENCY_NUMBER, agencyNumber);
            criteria.put("agencyAddressIdentifier", agencyAddressIdentifier);

            agencyAddress = (AgencyAddress) businessObjectService.findByPrimaryKey(AgencyAddress.class, criteria);
        }
        return agencyAddress;
    }

    /**
     * @see org.kuali.kfs.module.cg.document.service.AgencyAddressService#getPrimaryAddress(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public AgencyAddress getPrimaryAddress(String agencyNumber) {
        AgencyAddress primaryAddress = null;
        if (StringUtils.isNotBlank(agencyNumber)) {
            Map criteria = new HashMap();
            criteria.put(KFSPropertyConstants.AGENCY_NUMBER, agencyNumber);
            criteria.put("agencyAddressTypeCode", "P");

            primaryAddress = (AgencyAddress) businessObjectService.findMatching(AgencyAddress.class, criteria).iterator().next();
        }

        return primaryAddress;
    }

    /**
     * This method returns true if agency address is active
     * 
     * @param agencyNumber
     * @param agencyAddressIdentifier
     * @return
     */
    public boolean agencyAddressActive(String agencyNumber, Integer agencyAddressIdentifier) {

        AgencyAddress agencyAddress = getByPrimaryKey(agencyNumber, agencyAddressIdentifier);

        if (ObjectUtils.isNotNull(agencyAddress)) {
            if (ObjectUtils.isNotNull(agencyAddress.getAgencyAddressEndDate())) {
                Timestamp currentDateTimestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());
                Timestamp addressEndDateTimestamp = new Timestamp(agencyAddress.getAgencyAddressEndDate().getTime());
                if (addressEndDateTimestamp.before(currentDateTimestamp)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @see org.kuali.kfs.module.cg.document.service.AgencyAddressService#agencyAddressExists(java.lang.String, java.lang.Integer)
     */
    public boolean agencyAddressExists(String agencyNumber, Integer agencyAddressIdentifier) {
        return ObjectUtils.isNotNull(getByPrimaryKey(agencyNumber, agencyAddressIdentifier));
    }


    /**
     * This method sets agency address dao
     * 
     * @return
     */
    public AgencyAddressDao getAgencyAddressDao() {
        return agencyAddressDao;
    }

    /**
     * This method gets agency address dao
     * 
     * @param agencyAddressDao
     */
    public void setAgencyAddressDao(AgencyAddressDao agencyAddressDao) {
        this.agencyAddressDao = agencyAddressDao;
    }

    /**
     * @see org.kuali.kfs.module.cg.document.service.AgencyAddressService#getNextAgencyAddressIdentifier()
     */
    public Integer getNextAgencyAddressIdentifier() {

        Long nextId = sequenceAccessorService.getNextAvailableSequenceNumber(AGENCY_ADDR_ID_SEQ, AgencyAddress.class);

        return nextId.intValue();

    }

    /**
     * This method gets the sequenceAccessorService
     * 
     * @return
     */
    public SequenceAccessorService getSequenceAccessorService() {
        return sequenceAccessorService;
    }

    /**
     * This method sets the sequenceAccessorService
     * 
     * @param sequenceAccessorService
     */
    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
