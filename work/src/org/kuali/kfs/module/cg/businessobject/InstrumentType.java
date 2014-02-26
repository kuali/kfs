/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsInstrumentType;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Instrument Types under Contracts and Grants section.
 */

public class InstrumentType extends PersistableBusinessObjectBase implements ContractsAndGrantsInstrumentType, MutableInactivatable {

    private String instrumentTypeCode;
    private String instrumentTypeDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public InstrumentType() {
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

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("instrumentTypeCode", this.instrumentTypeCode);
        return m;
    }
}
