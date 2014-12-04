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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.cg.businessobject.BillingFrequency;

public enum BillingFrequencyFixture {

    BILL_FREQ_ANNUALLY("ANNU", "Annually", 0, true),
    BILL_FREQ_LOCB("LOCB","LOC Billing", 0, true),
    BILL_FREQ_MON("MNTH","Monthly", 0, true),
    BILL_FREQ_MS("MILE","Milestone", 0, true),
    BILL_FREQ_PDBS("PDBS","Predetermined Billing Schedule", 0, true),
    BILL_FREQ_QUAR("QUAR","Quarterly", 0, true),
    BILL_FREQ_SEMI_ANN("SEMI","Semi Annually", 0, true);

    private String frequency;
    private String frequencyDescription;
    private Integer gracePeriodDays;
    private boolean active;

    private BillingFrequencyFixture(String frequency, String frequencyDescription, Integer gracePeriodDays, boolean active) {

        this.frequency = frequency;
        this.frequencyDescription = frequencyDescription;
        this.gracePeriodDays = gracePeriodDays;
        this.active = active;

    }

    public BillingFrequency createBillingFrequency() {
        BillingFrequency billingFrequency = new BillingFrequency();

        billingFrequency.setFrequency(frequency);
        billingFrequency.setFrequencyDescription(frequencyDescription);
        billingFrequency.setGracePeriodDays(gracePeriodDays);
        billingFrequency.setActive(active);

        return billingFrequency;
    }

}
