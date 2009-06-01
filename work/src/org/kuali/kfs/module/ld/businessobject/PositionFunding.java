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

package org.kuali.kfs.module.ld.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Labor business object for PositionFunding
 */
public class PositionFunding extends LaborCalculatedSalaryFoundationTracker {
    private Person ledgerPerson;

    /**
     * Gets the ledgerPerson.
     * 
     * @return Returns the ledgerPerson.
     */
    public Person getLedgerPerson() {
        if(ledgerPerson == null || !StringUtils.equals(ledgerPerson.getEmployeeId(), this.getEmplid())) {
            ledgerPerson = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(this.getEmplid());
        }
        
        return ledgerPerson;
    }

    /**
     * Sets the ledgerPerson.
     * 
     * @param ledgerPerson The ledgerPerson to set.
     */
    public void setLedgerPerson(Person ledgerPerson) {
        this.ledgerPerson = ledgerPerson;
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.LaborCalculatedSalaryFoundationTracker#getName()
     */
    @Override
    public String getName() {
        Person person = this.getLedgerPerson();
        if (ObjectUtils.isNull(person)) {
            return LaborConstants.BalanceInquiries.UnknownPersonName;
        }
        
        return person.getName();
    }
}

