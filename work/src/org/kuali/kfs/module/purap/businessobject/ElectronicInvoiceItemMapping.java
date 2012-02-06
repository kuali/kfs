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
/*
 * Created on Feb 28, 2006
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ElectronicInvoiceItemMapping extends PersistableBusinessObjectBase implements MutableInactivatable {

    private Integer invoiceMapIdentifier;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String itemTypeCode;
    private String invoiceItemTypeCode;
    private boolean active;

    private ItemType itemType;
    private ItemType invoiceItemType;

    /**
   * 
   */
    public ElectronicInvoiceItemMapping() {
        super();
    }

    /**
     * @return Returns the invoiceMapIdentifier.
     */
    public Integer getInvoiceMapIdentifier() {
        return invoiceMapIdentifier;
    }

    /**
     * @param invoiceMapIdentifier The invoiceMapIdentifier to set.
     */
    public void setInvoiceMapIdentifier(Integer id) {
        this.invoiceMapIdentifier = id;
    }

    /**
     * @return Returns the invoiceItemTypeCode.
     */
    public String getInvoiceItemTypeCode() {
        return invoiceItemTypeCode;
    }

    /**
     * @param invoiceItemTypeCode The invoiceItemTypeCode to set.
     */
    public void setInvoiceItemTypeCode(String electronicInvoiceItemTypeCode) {
        this.invoiceItemTypeCode = electronicInvoiceItemTypeCode;
    }

    /**
     * @return Returns the itemTypeCode.
     */
    public String getItemTypeCode() {
        return itemTypeCode;
    }

    /**
     * @param itemTypeCode The itemTypeCode to set.
     */
    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    /**
     * @return Returns the itemType.
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * @param itemType The itemType to set.
     */
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
        this.itemTypeCode = itemType.getItemTypeCode();
    }

    public ItemType getInvoiceItemType() {
        return invoiceItemType;
    }

    public void setInvoiceItemType(ItemType invoiceItemType) {
        this.invoiceItemType = invoiceItemType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return Returns the vendorDetailAssignedIdentifier.
     */
    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    /**
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     */
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedId) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedId;
    }

    /**
     * @return Returns the vendorHeaderGeneratedIdentifier.
     */
    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     */
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedId) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedId;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("invoiceMapIdentifier", this.invoiceMapIdentifier);
        return m;
    }
}
