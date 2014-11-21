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
package org.kuali.kfs.module.purap.document.service;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.B2BInformation;
import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCart;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;

/**
 * These items will allow a user to punch out for shopping and will create requisitions from an order.
 */
public interface B2BShoppingService {

    /**
     * Get URL to punch out to
     *
     * @param User ID punching out
     * @return URL to punch out to
     */
    public String getPunchOutUrl(Person user);

    /**
     * Create requisition(s) from cxml and return list for display
     *
     * @param cxml cXml string
     * @param user User doing the requisitioning
     * @return List of requisitions
     */
    public List createRequisitionsFromCxml(B2BShoppingCart message, Person user) throws WorkflowException;

    /**
     * Get cxml punch out request xml.
     * @return xml for punch out request
     */
    public String getPunchOutSetupRequestMessage(Person user, B2BInformation b2bInformation);

}
