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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Purchase Order Quote Language Business Object.
 */
public class PurchaseOrderQuoteLanguage extends PersistableBusinessObjectBase implements MutableInactivatable{

    private Integer purchaseOrderQuoteLanguageIdentifier;
    private String purchaseOrderQuoteLanguageDescription;
    private Date purchaseOrderQuoteLanguageCreateDate;
    private boolean active;

    /**
     * Default constructor.
     */
    public PurchaseOrderQuoteLanguage() {

    }

    public Integer getPurchaseOrderQuoteLanguageIdentifier() {
        return purchaseOrderQuoteLanguageIdentifier;
    }

    public void setPurchaseOrderQuoteLanguageIdentifier(Integer purchaseOrderQuoteLanguageIdentifier) {
        this.purchaseOrderQuoteLanguageIdentifier = purchaseOrderQuoteLanguageIdentifier;
    }

    public String getPurchaseOrderQuoteLanguageDescription() {
        return purchaseOrderQuoteLanguageDescription;
    }

    public void setPurchaseOrderQuoteLanguageDescription(String purchaseOrderQuoteLanguageDescription) {
        this.purchaseOrderQuoteLanguageDescription = purchaseOrderQuoteLanguageDescription;
    }

    public Date getPurchaseOrderQuoteLanguageCreateDate() {
        return purchaseOrderQuoteLanguageCreateDate;
    }

    public void setPurchaseOrderQuoteLanguageCreateDate(Date purchaseOrderQuoteLanguageCreateDate) {
        this.purchaseOrderQuoteLanguageCreateDate = purchaseOrderQuoteLanguageCreateDate;
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
        if (this.purchaseOrderQuoteLanguageIdentifier != null) {
            m.put("purchaseOrderQuoteLanguageIdentifier", this.purchaseOrderQuoteLanguageIdentifier.toString());
        }
        return m;
    }

}
