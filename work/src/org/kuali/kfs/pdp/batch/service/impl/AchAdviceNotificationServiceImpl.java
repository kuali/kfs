/*
 * Copyright 2008-2009 The Kuali Foundation
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
