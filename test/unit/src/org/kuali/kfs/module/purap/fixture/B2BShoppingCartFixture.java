/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.fixture;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCart;

/**
 * Fixture class for B2BShoppingCart.
 */
public enum B2BShoppingCartFixture {

    B2B_CART_USING_VENDOR_ID (
            "200", // messageStatusCode
            "Success", // messageStatusText
            "parke", // buyerCookieText
            "500.00", // totalAmount
            B2BShoppingCartItemFixture.B2B_ITEM_USING_VENDOR_ID // itemFixture
    ),     
    
    B2B_CART_USING_VENDOR_DUNS (
            "200", // messageStatusCode
            "Success", // messageStatusText
            "parke", // buyerCookieText
            "500.00", // totalAmount
            B2BShoppingCartItemFixture.B2B_ITEM_USING_VENDOR_DUNS // itemFixture
    ),
    ;
    
    public String messageStatusCode;
    public String messageStatusText;
    public String buyerCookieText;
    public String totalAmount;
    public List<B2BShoppingCartItemFixture> itemFixturesList;

    /**
     * Constructs a B2BShoppingCartFixture with only one item.
     * @param messageStatusCode
     * @param messageStatusText
     * @param buyerCookieText
     * @param totalAmount
     * @param itemFixture
     */
    private B2BShoppingCartFixture(
            String messageStatusCode,
            String messageStatusText,
            String buyerCookieText,
            String totalAmount,
            B2BShoppingCartItemFixture itemFixture
    ) {
        this.messageStatusCode = messageStatusCode;
        this.messageStatusText = messageStatusText;
        this.buyerCookieText = buyerCookieText;
        this.totalAmount = totalAmount;
        itemFixturesList = new ArrayList<B2BShoppingCartItemFixture>();
        itemFixturesList.add(itemFixture);       
    }
    
    /**
     * Creates a B2BShoppingCart from this B2BShoppingCartFixture.
     */
    public B2BShoppingCart createB2BShoppingCart() {
        B2BShoppingCart cart = new B2BShoppingCart();
        cart.setMessageStatusCode(messageStatusCode);
        cart.setMessageStatusText(messageStatusText);
        cart.setTotal(totalAmount);
        
        for (B2BShoppingCartItemFixture itemFixture : itemFixturesList) {
            cart.addShoppingCartItem(itemFixture.createB2BShoppingCartItem());
        }
        
        return cart;
    }

}
