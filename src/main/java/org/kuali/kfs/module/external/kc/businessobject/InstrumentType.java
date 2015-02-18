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

import org.kuali.kfs.integration.cg.ContractsAndGrantsInstrumentType;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

public class InstrumentType implements ContractsAndGrantsInstrumentType, MutableInactivatable {

    private String instrumentTypeCode;
    private String instrumentTypeDescription;

    @Override
    public void refresh() { }

    @Override
    public void setActive(boolean active) { }

    @Override
    /**
     * Always returns true as KC's AwardType doesn't have an active flag
     * @see org.kuali.rice.core.api.mo.common.active.Inactivatable#isActive()
     */
    public boolean isActive() {
        return true;
    }

    @Override
    public String getInstrumentTypeCode() {
        return instrumentTypeCode;
    }

    public void setInstrumentTypeCode(String instrumentTypeCode) {
        this.instrumentTypeCode = instrumentTypeCode;
    }

    @Override
    public String getInstrumentTypeDescription() {
        return instrumentTypeDescription;
    }

    public void setInstrumentTypeDescription(String instrumentTypeDescription) {
        this.instrumentTypeDescription = instrumentTypeDescription;
    }

}
