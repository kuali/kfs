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

import java.util.List;

import org.kuali.kfs.pdp.batch.service.AchAdviceNotificationService;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * @see org.kuali.kfs.pdp.batch.service.AchAdviceNotificationService
 */
public class AchAdviceNotificationServiceImpl implements AchAdviceNotificationService {
    private PdpEmailService pdpEmailService;
    private PaymentGroupService paymentGroupService;

    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;

    /**
     * Set to NonTransactional so the payment advice email sent date will be updated and saved after the email is sent
     * 
     * @see org.kuali.kfs.pdp.batch.service.AchAdviceNotificationService#sendAdviceNotifications()
     */
    @NonTransactional
    public void sendAdviceNotifications() {
        // get list of payments to send notification for
        List<PaymentGroup> paymentGroups = paymentGroupService.getAchPaymentsNeedingAdviceNotification();
        for (PaymentGroup paymentGroup : paymentGroups) {
            CustomerProfile customer = paymentGroup.getBatch().getCustomerProfile();

            // verify the customer profile is setup to create advices
            if (customer.getAdviceCreate()) {
                for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {
                    pdpEmailService.sendAchAdviceEmail(paymentGroup, paymentDetail, customer);
                }
            }

            // update advice sent date on payment
            paymentGroup.setAdviceEmailSentDate(dateTimeService.getCurrentTimestamp());
            businessObjectService.save(paymentGroup);
        }
    }

    /**
     * Sets the pdpEmailService attribute value.
     * 
     * @param pdpEmailService The pdpEmailService to set.
     */
    @NonTransactional
    public void setPdpEmailService(PdpEmailService pdpEmailService) {
        this.pdpEmailService = pdpEmailService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    @NonTransactional
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the paymentGroupService attribute value.
     * 
     * @param paymentGroupService The paymentGroupService to set.
     */
    @NonTransactional
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }
}
