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
package org.kuali.kfs.module.ar.service;

import java.util.Collection;
import java.util.List;

import javax.mail.MessagingException;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.rice.krad.exception.InvalidAddressException;

/**
 * Defines methods for sending AR emails.
 */
public interface AREmailService {

    /**
     * This method is used to send emails to the agency
     *
     * @param invoices
     * @return true if all invoices were sent successfully, false otherwise
     * @throws InvalidAddressException
     * @throws MessagingException
     */
    public boolean sendInvoicesViaEmail(Collection<ContractsGrantsInvoiceDocument> invoices) throws InvalidAddressException, MessagingException;

    /**
     * Send email for upcoming milestones for Award
     *
     * @param milestones
     * @param award
     */
    public void sendEmailNotificationsForMilestones(List<Milestone> milestones, ContractsAndGrantsBillingAward award);

}
