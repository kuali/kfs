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

package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.purap.PurchasingAccountsPayableSensitiveData;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Sensitive Data Business Object.
 */
public class SensitiveData extends PersistableBusinessObjectBase implements PurchasingAccountsPayableSensitiveData, MutableInactivatable{

    private String sensitiveDataCode;
    private String sensitiveDataDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public SensitiveData() {

    }

    public String getSensitiveDataCode() {
        return sensitiveDataCode;
    }

    public void setSensitiveDataCode(String sensitiveDataCode) {
        this.sensitiveDataCode = sensitiveDataCode;
    }

    public String getSensitiveDataDescription() {
        return sensitiveDataDescription;
    }

    public void setSensitiveDataDescription(String sensitiveDataDescription) {
        this.sensitiveDataDescription = sensitiveDataDescription;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("restrictedMaterialCode", this.sensitiveDataCode);
        return m;
    }

}
