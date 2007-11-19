/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.purap.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Purchase Order Quote Language Business Object.
 * 
 * THIS CODE IS NOT USED IN RELEASE 2 BUT THE CODE WAS LEFT IN TO
 * FACILITATE TURNING IT BACK ON EARLY IN THE DEVELOPMENT CYCLE OF RELEASE 3.
 */
public class PurchaseOrderQuoteLanguage extends PersistableBusinessObjectBase {

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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.purchaseOrderQuoteLanguageIdentifier != null) {
            m.put("purchaseOrderQuoteLanguageIdentifier", this.purchaseOrderQuoteLanguageIdentifier.toString());
        }
        return m;
    }

}
