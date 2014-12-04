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
package org.kuali.kfs.pdp.batch.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants.PayeeIdTypeCodes;
import org.kuali.kfs.pdp.batch.service.InactivatePayeeAchAccountsService;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.pdp.service.AchService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation for InactivatePayeeAchAccountsService interface.
 */
public class InactivatePayeeAchAccountsServiceImpl implements InactivatePayeeAchAccountsService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InactivatePayeeAchAccountsServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private IdentityManagementService identityManagementService;
    private PersonService personService;
    private VendorService vendorService;
    private AchService achService;
    private ReportWriterService reportWriterService;

    /**
     * @see rg.kuali.kfs.pdp.batch.service.#inactivatePayeeAchAccounts()
     */
    @Override
    public boolean inactivatePayeeAchAccounts() {
        LOG.info("Retrieving currently active Payee ACH Accounts ...");
        List<PayeeACHAccount> accounts = achService.getActiveAchAccounts();

        LOG.info("Inactivating ACH Accounts for inactive Payees and writing to the report ...");
        reportWriterService.writeTableHeader(PayeeACHAccount.class);
        int countEmployee = 0;
        int countEntity = 0;
        int countVendor = 0;
        Date currentDate = dateTimeService.getCurrentDate();

        for (PayeeACHAccount account : accounts) {
            String idType = account.getPayeeIdentifierTypeCode();
            String idNumber = account.getPayeeIdNumber();

            // for Employee, retrieve from Person table by employee ID
            if (StringUtils.equalsIgnoreCase(idType, PayeeIdTypeCodes.EMPLOYEE)) {
                Entity entity = KimApiServiceLocator.getIdentityService().getEntityByEmployeeId(idNumber);
                // inactivate the account if the person doesn't exist anymore or is inactive
                if (ObjectUtils.isNull(entity) || !entity.isActive()) {
                    LOG.info("Inactivating Payee ACH account for employee with ID # " + idNumber);
                    account.setActive(false);

                    // set autoInactivationIndicator, and lastUpdate will be set by TimestampsBusinessObjectBase#prePersist called by OJB
                    account.setAutoInactivationIndicator(true);

                    businessObjectService.save(account);
                    countEmployee++;
                    reportWriterService.writeTableRow(account);
                }
            }

            // for Entity, retrieve from Entity table by entity ID
            else if (StringUtils.equalsIgnoreCase(idType, PayeeIdTypeCodes.ENTITY)) {
                EntityDefault entity = identityManagementService.getEntityDefaultInfo(idNumber);
                // inactivate the account if the entity doesn't exist anymore or is inactive
                if (ObjectUtils.isNull(entity) || !entity.isActive()) {
                    LOG.info("Inactivating Payee ACH account for entity with ID # " + idNumber);
                    account.setActive(false);

                    // set autoInactivationIndicator, and lastUpdate will be set by TimestampsBusinessObjectBase#prePersist called by OJB
                    account.setAutoInactivationIndicator(true);

                    businessObjectService.save(account);
                    countEntity++;
                    reportWriterService.writeTableRow(account);
                }
            }

            // for Vendor, retrieve from Vendor table by vendor number
            else if (StringUtils.equalsIgnoreCase(idType, PayeeIdTypeCodes.VENDOR_ID)) {
                /*
                // in PayeeACHAccount table, in case the vendor number only refers to the headerId,
                // with default detailId being 0, we need to add the default detailId to the vendor number
                String vendorNumber = idNumber;
                if (!StringUtils.contains(idNumber, VendorConstants.DASH)) {
                    vendorNumber += VendorConstants.DASH + VendorConstants.DEFAULT_VENDOR_DETAIL_ID;
                }
                VendorDetail vendor = vendorService.getVendorDetail(vendorNumber);
                */

                VendorDetail vendor = vendorService.getVendorDetail(idNumber);
                // inactivate the account if the vendor doesn't exist anymore or is inactive
                if (ObjectUtils.isNull(vendor) || !vendor.isActiveIndicator()) {
                    LOG.info("Inactivating Payee ACH account for vendor with vendor # " + idNumber);
                    account.setActive(false);

                    // set autoInactivationIndicator, and lastUpdate will be set by TimestampsBusinessObjectBase#prePersist called by OJB
                    account.setAutoInactivationIndicator(true);

                    businessObjectService.save(account);
                    countVendor++;
                    reportWriterService.writeTableRow(account);
                }
            }
        }

        LOG.info("Total number of Employee Payee ACH accounts inacticated:" + countEmployee);
        LOG.info("Total number of Entity Payee ACH accounts inacticated:" + countEntity);
        LOG.info("Total number of Vendor Payee ACH accounts inacticated:" + countVendor);

        reportWriterService.writeStatisticLine("%s %s", "TOTAL ACTIVE ACCOUNTS BEFORE RUNNING THE JOB:", accounts.size());
        reportWriterService.writeStatisticLine("%s %s", "TOTAL ACCOUNTS INACTIVATED FOR EMPLOYEES:    ", countEmployee);
        reportWriterService.writeStatisticLine("%s %s", "TOTAL ACCOUNTS INACTIVATED FOR ENTITIES:     ", countEntity);
        reportWriterService.writeStatisticLine("%s %s", "TOTAL ACCOUNTS INACTIVATED FOR VENDORS:      ", countVendor);
        reportWriterService.writeStatisticLine("%s %s", "ACCOUNTS INACTIVATION DATE:                  ", currentDate);

        return true;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setIdentityManagementService(IdentityManagementService identityManagementService) {
        this.identityManagementService = identityManagementService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setAchService(AchService achService) {
        this.achService = achService;
    }

    public void setReportWriterService(ReportWriterService reportWriterService) {
        this.reportWriterService = reportWriterService;
    }

}
