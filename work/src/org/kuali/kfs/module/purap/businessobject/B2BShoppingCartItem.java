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
/*
 * Created on Aug 25, 2004
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

public class B2BShoppingCartItem {
    
    private String quantity;
    private String supplierPartId;
    private String supplierPartAuxiliaryId;
    private String unitPrice;
    private String unitPriceCurrency;
    private String description;
    private String unitOfMeasure;
    private String manufacturerPartID;
    private String manufacturerName;
    private Map<String,String> classification = new HashMap<String,String>();
    private Map<String,String> extrinsic = new HashMap<String,String>();
    private Map<String,String> supplier = new HashMap<String,String>();

    public B2BShoppingCartItem() {
    }

    public String getClassification(String domain) {
        return classification.get(domain);
    }

    public void addClassification(String domain, String value) {
        classification.put(domain, value);
    }

    public Map getClassification() {
        return classification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtrinsic(String domain) {
        return extrinsic.get(domain);
    }

    public void addExtrinsic(String domain, String value) {
        extrinsic.put(domain, value);
    }

    public Map<String,String> getExtrinsic() {
        return extrinsic;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSupplier(String domain) {
        return supplier.get(domain);
    }

    public void setSupplier(String domain, String value) {
        supplier.put(domain, value);
    }

    public Map<String,String> getSupplier() {
        return supplier;
    }

    public String getSupplierPartAuxiliaryId() {
        return supplierPartAuxiliaryId;
    }

    public void setSupplierPartAuxiliaryId(String supplierPartAuxiliaryId) {
        this.supplierPartAuxiliaryId = supplierPartAuxiliaryId;
    }

    public String getSupplierPartId() {
        return supplierPartId;
    }

    public void setSupplierPartId(String supplierPartId) {
        this.supplierPartId = supplierPartId;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnitPriceCurrency() {
        return unitPriceCurrency;
    }

    public void setUnitPriceCurrency(String unitPriceCurrency) {
        this.unitPriceCurrency = unitPriceCurrency;
    }

    public String getManufacturerPartID() {
        return manufacturerPartID;
    }

    public void setManufacturerPartID(String manufacturerPartID) {
        this.manufacturerPartID = manufacturerPartID;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }
    
    public String toString() {
        
        ToStringBuilder toString = new ToStringBuilder(this);
        toString.append("quantity", quantity);
        toString.append("supplierPartId", supplierPartId);
        toString.append("supplierPartAuxiliaryId", supplierPartAuxiliaryId);
        toString.append("unitPrice", unitPrice);
        toString.append("unitPriceCurrency", unitPriceCurrency);
        toString.append("description", description);
        toString.append("unitOfMeasure", unitOfMeasure);
        toString.append("manufacturerPartID", manufacturerPartID);
        toString.append("manufacturerName", manufacturerName);
        toString.append("classification", classification);
        toString.append("extrinsic", extrinsic);
        toString.append("supplier", supplier);

        return toString.toString();
    }

}
