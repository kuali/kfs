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
package org.kuali.kfs.pdp.service;

import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.rice.krad.util.MessageMap;

public interface ResearchParticipantPaymentValidationService {

    /**
     * validate the account information provided by research participant payment file
     *
     * @param paymentFile the given payment file
     * @param errorMap the given error map that can hold the error information
     */
    boolean validatePaymentAccount(PaymentFileLoad paymentFile, MessageMap errorMap);

    /**
     * get the account detail provided by the given payment file
     *
     * @param paymentFile the given payment file
     * @return the account detail provided by the given payment file
     */
    PaymentAccountDetail getPaymentAccountDetail(PaymentFileLoad paymentFile);

    public boolean isResearchParticipantPayment(CustomerProfile customer);
}
