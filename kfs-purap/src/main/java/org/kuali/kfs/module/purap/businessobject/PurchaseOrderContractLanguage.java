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

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Purchase Order Contract Language Business Object.
 */
public class PurchaseOrderContractLanguage extends PersistableBusinessObjectBase implements MutableInactivatable{

    private Integer purchaseOrderContractLanguageIdentifier;
    private String campusCode;
    private String purchaseOrderContractLanguageDescription;
    private Date contractLanguageCreateDate;
    private boolean active;

    /**
     * Default constructor.
     */
    public PurchaseOrderContractLanguage() {
        this.setContractLanguageCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
    }

    public Integer getPurchaseOrderContractLanguageIdentifier() {
        return purchaseOrderContractLanguageIdentifier;
    }

    public void setPurchaseOrderContractLanguageIdentifier(Integer purchaseOrderContractLanguageIdentifier) {
        this.purchaseOrderContractLanguageIdentifier = purchaseOrderContractLanguageIdentifier;
    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public String getPurchaseOrderContractLanguageDescription() {
        return purchaseOrderContractLanguageDescription;
    }

    public void setPurchaseOrderContractLanguageDescription(String purchaseOrderContractLanguageDescription) {
        this.purchaseOrderContractLanguageDescription = purchaseOrderContractLanguageDescription;
    }

    public Date getContractLanguageCreateDate() {
        return contractLanguageCreateDate;
    }

    public void setContractLanguageCreateDate(Date contractLanguageCreateDate) {
        this.contractLanguageCreateDate = contractLanguageCreateDate;
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
        if (this.purchaseOrderContractLanguageIdentifier != null) {
            m.put("purchaseOrderContractLanguageIdentifier", this.purchaseOrderContractLanguageIdentifier.toString());
        }
        return m;
    }

}
