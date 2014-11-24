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
package org.kuali.kfs.module.tem.businessobject;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * This class stores the Traveler information for Travel Request/Reimbursement
 */
@Entity
@Table(name = "TEM_TRAVELER_DTL_T")
public class TravelerDetail extends BaseTemProfile {
    
    @OneToMany(mappedBy = "id")
    private List<TravelerDetailEmergencyContact> emergencyContacts = new ArrayList<TravelerDetailEmergencyContact>();
    
    /**
     * Reset emergency contact lists when switching the traveler/tem profile
     */
    public void resetEmergencyContacts(){
        emergencyContacts = new ArrayList<TravelerDetailEmergencyContact>();
    }
    
    public List<TravelerDetailEmergencyContact> getEmergencyContacts() {
        return emergencyContacts;
    }

    public void setEmergencyContacts(List<TravelerDetailEmergencyContact> emergencyContacts) {
        this.emergencyContacts = emergencyContacts;
    }
}
