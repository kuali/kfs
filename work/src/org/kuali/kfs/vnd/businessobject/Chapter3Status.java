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

public class Chapter3Status extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String vendorChapter3StatusCode;
    private String vendorChapter3StatusDescription;
    private boolean active;
    /**
     * Gets the vendorChapter3StatusCode attribute.
     *
     * @return Returns the vendorChapter3StatusCode
     */

    public String getVendorChapter3StatusCode() {
        return vendorChapter3StatusCode;
    }
    /**
     * Sets the vendorChapter3StatusCode attribute.
     *
     * @param vendorChapter3StatusCode The vendorChapter3StatusCode to set.
     */
    public void setVendorChapter3StatusCode(String vendorChapter3StatusCode) {
        this.vendorChapter3StatusCode = vendorChapter3StatusCode;
    }
    /**
     * Gets the vendorChapter3StatusDescription attribute.
     *
     * @return Returns the vendorChapter3StatusDescription
     */

    public String getVendorChapter3StatusDescription() {
        return vendorChapter3StatusDescription;
    }
    /**
     * Sets the vendorChapter3StatusDescription attribute.
     *
     * @param vendorChapter3StatusDescription The vendorChapter3StatusDescription to set.
     */
    public void setVendorChapter3StatusDescription(String vendorChapter3StatusDescription) {
        this.vendorChapter3StatusDescription = vendorChapter3StatusDescription;
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
