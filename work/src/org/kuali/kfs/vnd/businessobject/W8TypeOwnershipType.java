/*
 * Copyright 2014 The Kuali Foundation.
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

public class W8TypeOwnershipType extends PersistableBusinessObjectBase implements MutableInactivatable {

    private int id;
    private String vendorOwnershipCode;
    private String w8TypeCode;
    private boolean active;

    private W8Type w8Type;
    private OwnershipType ownershipType;


    /**
     * Gets the vendorOwnershipCode attribute.
     *
     * @return Returns the vendorOwnershipCode
     */

    public String getVendorOwnershipCode() {
        return vendorOwnershipCode;
    }
    /**
     * Sets the vendorOwnershipCode attribute.
     *
     * @param vendorOwnershipCode The vendorOwnershipCode to set.
     */
    public void setVendorOwnershipCode(String vendorOwnershipCode) {
        this.vendorOwnershipCode = vendorOwnershipCode;
    }
    /**
     * Gets the ownershipType attribute.
     *
     * @return Returns the ownershipType
     */

    public OwnershipType getOwnershipType() {
        return ownershipType;
    }
    /**
     * Sets the ownershipType attribute.
     *
     * @param ownershipType The ownershipType to set.
     */
    @Deprecated
    public void setOwnershipType(OwnershipType ownershipType) {
        this.ownershipType = ownershipType;
    }
    /**
     * Gets the w8TypeCode attribute.
     *
     * @return Returns the w8TypeCode
     */

    public String getW8TypeCode() {
        return w8TypeCode;
    }
    /**
     * Sets the w8TypeCode attribute.
     *
     * @param w8TypeCode The w8TypeCode to set.
     */
    public void setW8TypeCode(String w8TypeCode) {
        this.w8TypeCode = w8TypeCode;
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
    /**
     * Gets the w8Type attribute.
     *
     * @return Returns the w8Type
     */

    public W8Type getW8Type() {
        return w8Type;
    }
    /**
     * Sets the w8Type attribute.
     *
     * @param w8Type The w8Type to set.
     */
    @Deprecated
    public void setW8Type(W8Type w8Type) {
        this.w8Type = w8Type;
    }
    /**
     * Gets the id attribute.
     *
     * @return Returns the id
     */

    public int getId() {
        return id;
    }
    /**
     * Sets the id attribute.
     *
     * @param id The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

}
