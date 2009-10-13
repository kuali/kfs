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
