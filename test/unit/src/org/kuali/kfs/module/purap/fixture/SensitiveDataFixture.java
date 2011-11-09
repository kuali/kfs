/*
 * Copyright 2008 The Kuali Foundation
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
