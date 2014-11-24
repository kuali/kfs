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

package org.kuali.kfs.module.ld.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.util.ObjectUtils;

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
        if( (ledgerPerson == null || !StringUtils.equals(ledgerPerson.getEmployeeId(), getEmplid())) && StringUtils.isNotBlank(getEmplid())) {
            ledgerPerson = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(getEmplid());
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

