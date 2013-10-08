/*
 * Copyright 2006-2008 The Kuali Foundation
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
