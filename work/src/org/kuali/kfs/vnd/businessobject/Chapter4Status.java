/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.vnd.businessobject;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class Chapter4Status extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String vendorChapter4StatusCode;
    private String vendorChapter4StatusDescription;
    private boolean active;
    /**
     * Gets the vendorChapter4StatusCode attribute.
     *
     * @return Returns the vendorChapter4StatusCode
     */

    public String getVendorChapter4StatusCode() {
        return vendorChapter4StatusCode;
    }
    /**
     * Sets the vendorChapter4StatusCode attribute.
     *
     * @param vendorChapter4StatusCode The vendorChapter4StatusCode to set.
     */
    public void setVendorChapter4StatusCode(String vendorChapter4StatusCode) {
        this.vendorChapter4StatusCode = vendorChapter4StatusCode;
    }
    /**
     * Gets the vendorChapter4StatusDescription attribute.
     *
     * @return Returns the vendorChapter4StatusDescription
     */

    public String getVendorChapter4StatusDescription() {
        return vendorChapter4StatusDescription;
    }
    /**
     * Sets the vendorChapter4StatusDescription attribute.
     *
     * @param vendorChapter4StatusDescription The vendorChapter4StatusDescription to set.
     */
    public void setVendorChapter4StatusDescription(String vendorChapter4StatusDescription) {
        this.vendorChapter4StatusDescription = vendorChapter4StatusDescription;
    }
    /**
     * Gets the active attribute.
     *
     * @return Returns the active
     */

    @Override
    public boolean isActive() {
        return active;
    }
    /**
     * Sets the active attribute.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }



}
