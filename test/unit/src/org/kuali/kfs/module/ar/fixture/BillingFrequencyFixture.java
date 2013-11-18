/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.cg.businessobject.BillingFrequency;

public enum BillingFrequencyFixture {

    BILL_FREQ_ANNUALLY("ANNUALLY", "Annually", "0", true),
    BILL_FREQ_BILL("BILL","Billing", "0", true),
    BILL_FREQ_LOCB("LOCB","LOC Billing", "0", true),
    BILL_FREQ_MON("MON","Monthly", "0", true),
    BILL_FREQ_MS("MS","Milestone", "0", true),
    BILL_FREQ_PDBS("PDBS","Predetermined Billing Schedule", "0", true),
    BILL_FREQ_QUAR("QUAR","Quarterly", "0", true),
    BILL_FREQ_SEMI_ANN("SEMI-ANN","Semi Annually", "0", true);

    private String frequency;
    private String frequencyDescription;
    private String gracePeriodDays;
    private boolean active;

    private BillingFrequencyFixture(String frequency, String frequencyDescription, String gracePeriodDays, boolean active) {

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
