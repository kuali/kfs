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
package org.kuali.kfs.module.purap.util.cxml;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.businessobject.B2BShoppingCartItem;

public class B2BShoppingCart extends B2BShoppingCartBase {
    
    private final static Logger log = Logger.getLogger(B2BShoppingCart.class);
    
    private String messageStatusCode;
    private String messageStatusText;
    private String buyerCookieText;
    private String totalAmount;
    //Not used
    private CxmlHeader cxmlHeader;
    private List<B2BShoppingCartItem> itemsList;
    

    public void addShoppingCartItem(B2BShoppingCartItem item){
        if (itemsList == null){
            itemsList = new ArrayList<B2BShoppingCartItem>();
        }
        itemsList.add(item);
    }
    
    public B2BShoppingCartItem[] getShoppingCartItems(){
        if (itemsList != null){
            B2BShoppingCartItem[] tempItems = new B2BShoppingCartItem[itemsList.size()];
            return itemsList.toArray(tempItems);
        }
        return null;
    }
    
    public List getItems(){
        return itemsList;
    }
    
    public CxmlHeader getCxmlHeader() {
        return cxmlHeader;
    }

    public void setCxmlHeader(CxmlHeader cxmlHeader) {
        this.cxmlHeader = cxmlHeader;
    }
    
    public String getBuyerCookieText() {
        return buyerCookieText;
    }

    public void setBuyerCookieText(String buyerCookieText) {
        this.buyerCookieText = buyerCookieText;
    }
    
    public String getTotal() {
        return totalAmount;
    }

    public void setTotal(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getMessageStatusCode() {
        return messageStatusCode;
    }

    public void setMessageStatusCode(String messageStatusCode) {
        this.messageStatusCode = messageStatusCode;
    }

    public String getMessageStatusText() {
        return messageStatusText;
    }

    public void setMessageStatusText(String messageStatusText) {
        this.messageStatusText = messageStatusText;
    }
    
    public String toString(){
        
        ToStringBuilder toString = new ToStringBuilder(this);
        
        toString.append("messageStatusCode",getMessageStatusCode());
        toString.append("messageStatusText",getMessageStatusText());
        toString.append("statusCode",getStatusCode());
        toString.append("statusText",getStatusText());
        toString.append("buyerCookieText",getBuyerCookieText());
        toString.append("totalAmount",getTotal());
        toString.append("CXMLHeader",getCxmlHeader());
        toString.append("Items",itemsList);
        
        return toString.toString();
    }

}
