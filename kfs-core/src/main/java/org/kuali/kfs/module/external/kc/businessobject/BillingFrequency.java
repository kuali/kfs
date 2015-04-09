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
package org.kuali.kfs.module.external.kc.businessobject;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingFrequency;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

public class BillingFrequency implements ContractsAndGrantsBillingFrequency, MutableInactivatable {

    private String frequency;
    private String frequencyDescription;
    private Integer gracePeriodDays;
    private String kcFrequencyCode;
    private boolean active;

    @Override
    public void refresh() { }

    @Override
    public String getFrequency() {
        return frequency;
    }

    @Override
    public String getFrequencyDescription() {
        return frequencyDescription;
    }

    @Override
    public Integer getGracePeriodDays() {
        return gracePeriodDays;
    }

    public void setGracePeriodDays(Integer gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setFrequencyDescription(String frequencyDescription) {
        this.frequencyDescription = frequencyDescription;
    }

    public String getKcFrequencyCode() {
        return kcFrequencyCode;
    }

    public void setKcFrequencyCode(String kcFrequencyCode) {
        this.kcFrequencyCode = kcFrequencyCode;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

}
