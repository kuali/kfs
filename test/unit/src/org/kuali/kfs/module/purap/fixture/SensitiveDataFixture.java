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
package org.kuali.kfs.module.purap.fixture;

import org.kuali.kfs.module.purap.businessobject.SensitiveData;

public enum SensitiveDataFixture {

    SENSITIVE_DATA_ACTIVE("ANIM", "Animal", true),
    SENSITIVE_DATA_INACTIVE("NCLR", "Nuclear Material", false),
    SENSITIVE_DATA_TO_INACTIVATE("NEW", "New for test", true),
    SENSITIVE_DATA_INACTIVATED("NEW", "New for test", false);
    
    public final String code;
    public final String description;
    public final boolean active;
    
    private SensitiveDataFixture(String code, String description, boolean active) {
        this.code = code;
        this.description = description;
        this.active = active;
    }
    
    public SensitiveData getSensitiveDataBO() {
        SensitiveData sd = new SensitiveData();
        sd.setSensitiveDataCode(code);
        sd.setSensitiveDataDescription(description);
        sd.setActive(active);
        return sd;
    }
}
