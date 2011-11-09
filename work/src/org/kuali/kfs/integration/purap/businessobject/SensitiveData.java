/*
 * Copyright 2006-2009 The Kuali Foundation
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

package org.kuali.kfs.integration.purap.businessobject;

import org.kuali.kfs.integration.purap.PurchasingAccountsPayableSensitiveData;

/**
 * Sensitive Data Business Object.
 */
public class SensitiveData implements PurchasingAccountsPayableSensitiveData {

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

    public void prepareForWorkflow() {}
    public void refresh() {}

}
