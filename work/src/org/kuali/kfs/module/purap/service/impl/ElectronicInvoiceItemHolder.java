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
package org.kuali.kfs.module.purap.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.util.ElectronicInvoiceUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class ElectronicInvoiceItemHolder {
    
    private ElectronicInvoiceOrderHolder orderHolder;
    private ElectronicInvoiceRejectItem rejectItem;
    private ElectronicInvoiceItem invoiceItem;
    private PurchaseOrderItem poItem;
    private Map<String,ElectronicInvoiceItemMapping> itemTypeMappings;
    
    private boolean isRejectItemHolder;
    private boolean validateHeader;
    
    public ElectronicInvoiceItemHolder(ElectronicInvoiceRejectItem rejectItem,
                                       Map itemTypeMappings,
                                       PurchaseOrderItem poItem,
                                       ElectronicInvoiceOrderHolder orderHolder){
        
        if (rejectItem == null){
            throw new NullPointerException("ElectronicInvoiceRejectItem should not be null");
        }
        
        this.rejectItem = rejectItem;
        this.poItem = poItem;
        this.orderHolder = orderHolder;
        
        isRejectItemHolder = true;
    }
    
    public ElectronicInvoiceItemHolder(ElectronicInvoiceItem invoiceItem,
                                        Map itemTypeMappings,
                                        PurchaseOrderItem poItem,
                                        ElectronicInvoiceOrderHolder orderHolder){
        
        if (invoiceItem == null){
            throw new NullPointerException("ElectronicInvoiceItem should not be null");
        }
        
        this.invoiceItem = invoiceItem;
        this.poItem = poItem;
        this.orderHolder = orderHolder;
        
        isRejectItemHolder = false;
    }
 
    public Integer getInvoiceItemLineNumber(){
        if (isRejectItemHolder()){
            return rejectItem.getInvoiceReferenceItemLineNumber();
        }else{
           return invoiceItem.getReferenceLineNumberInteger();
        }
    }
    
    public String getInvoiceItemDescription(){
        if (isRejectItemHolder()){
            return rejectItem.getInvoiceReferenceItemDescription();
        }else{
           return invoiceItem.getReferenceDescription();
        }
    }
    
    public PurchaseOrderItem getPurchaseOrderItem(){
        return poItem;
    }
        
    public String getCatalogNumberStripped(){
        if (isRejectItemHolder()){
            return ElectronicInvoiceUtils.stripSplChars(rejectItem.getInvoiceItemCatalogNumber());
        }else{
            return ElectronicInvoiceUtils.stripSplChars(invoiceItem.getCatalogNumber());
        }
    }
    
    public BigDecimal getUnitPrice(){
        if (isRejectItemHolder()){
            return rejectItem.getInvoiceItemUnitPrice();
        }else{
            return invoiceItem.getInvoiceLineUnitCostBigDecimal();
        }
    }
    
    public String getUnitPriceCurrency(){
        if (isRejectItemHolder()){
            return rejectItem.getInvoiceItemUnitPriceCurrencyCode();
        }else{
            return invoiceItem.getUnitPriceCurrency();
        }
    }
    
    public BigDecimal getQuantity(){
        if (isRejectItemHolder()){
            return rejectItem.getInvoiceItemQuantity();
        }else{
            return invoiceItem.getInvoiceLineQuantityBigDecimal();
        }
    }
    
    public KualiDecimal getSubTotalAmount(){
        if (isRejectItemHolder()){
            return new KualiDecimal(rejectItem.getInvoiceItemSubTotalAmount().setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR));
        }else{
            return new KualiDecimal(invoiceItem.getInvoiceLineSubTotalAmountBigDecimal());
        }
    }
    
    public boolean isRejectItemHolder(){
        return isRejectItemHolder;
    }
    
    public ElectronicInvoiceItemMapping getItemMapping(String invoiceItemTypeCode){
        if (itemTypeMappings == null){
            return null;
        }else{
            return itemTypeMappings.get(invoiceItemTypeCode);
        }
    }
    
    public String getItemTypeCode(String invoiceItemTypeCode) {
        
        ElectronicInvoiceItemMapping itemMapping = getItemMapping(invoiceItemTypeCode);
        
        if (itemMapping != null) {
            return itemMapping.getItemTypeCode();
        }
        else {
            return null;
        }
    }

    public String getInvoiceItemCatalogNumber(){
        if (isRejectItemHolder()){
            return rejectItem.getInvoiceItemCatalogNumber();
        }else{
            return invoiceItem.getReferenceItemIDSupplierPartID();
        }
    }
    
    public String getInvoiceItemUnitOfMeasureCode(){
        if (isRejectItemHolder()){
            return rejectItem.getInvoiceItemUnitOfMeasureCode();
        }else{
            return invoiceItem.getUnitOfMeasure();
        }
    }
    
    public boolean isUnitOfMeasureAcceptIndicatorEnabled() {
        if (isRejectItemHolder()){
            return rejectItem.isUnitOfMeasureAcceptIndicator();
        }else{
            return false;
        }
    }
    
    public boolean isCatalogNumberAcceptIndicatorEnabled() {
        if (isRejectItemHolder()){
            return rejectItem.isCatalogNumberAcceptIndicator();
        }else{
            return false;
        }
    }
    
    public BigDecimal getInvoiceItemUnitPrice(){
        if (isRejectItemHolder()){
            return rejectItem.getInvoiceItemUnitPrice();
        }else{
            return invoiceItem.getInvoiceLineUnitCostBigDecimal();
        }
    }
    
    public BigDecimal getInvoiceItemQuantity(){
        if (isRejectItemHolder()){
            return rejectItem.getInvoiceItemQuantity();
        }else{
            return invoiceItem.getInvoiceLineQuantityBigDecimal();
        }
    }
    
    public BigDecimal getInvoiceItemSubTotalAmount(){
        if (isRejectItemHolder()){
            return rejectItem.getInvoiceItemSubTotalAmount();
        }else{
            return invoiceItem.getInvoiceLineSubTotalAmountBigDecimal();
        }
    }
    
    public BigDecimal getTaxAmount(){
        if (isRejectItemHolder()){
            return rejectItem.getInvoiceItemTaxAmount();
        }else{
            return invoiceItem.getInvoiceLineTaxAmountBigDecimal();
        }
    }
    
    public ElectronicInvoiceOrderHolder getInvoiceOrderHolder(){
        return orderHolder;
    }
}
