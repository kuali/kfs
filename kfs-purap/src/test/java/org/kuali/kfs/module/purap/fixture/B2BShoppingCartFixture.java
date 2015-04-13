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
