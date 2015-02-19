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
package org.kuali.kfs.module.cam.document.service;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.exception.ValidationException;

@ConfigureContext
public class AssetServiceTest extends KualiTestBase {

    private AssetService assetService;

    protected void setUp() throws Exception {
        super.setUp();
        assetService = SpringContext.getBean(AssetService.class);
    }


    public void testIsObjectSubTypeCompatible_Success() throws Exception {
        List<String> ls = new ArrayList<String>();
        ls.add("UC");
        ls.add("UF");
        ls.add("UO");
        assertTrue(assetService.isObjectSubTypeCompatible(ls));
        
        ls.clear();
        ls.add("LI");
        ls.add("LI");        
        assertTrue(assetService.isObjectSubTypeCompatible(ls));
        
        ls.clear();
        ls.add("LI");
        assertTrue(assetService.isObjectSubTypeCompatible(ls));

        // no failure because it's 1 payment
        ls.clear();
        ls.add("IF");
        assertTrue(assetService.isObjectSubTypeCompatible(ls));

        // no failure because it's of same object sub type
        ls.clear();
        ls.add("IF");
        ls.add("IF");
        assertTrue(assetService.isObjectSubTypeCompatible(ls));
    }
    
    public void testIsObjectSubTypeCompatible_Failure() throws Exception {
        List<String> ls = new ArrayList<String>();
        ls.add("BD");
        ls.add("UF");
        ls.add("UO");
        assertFalse(assetService.isObjectSubTypeCompatible(ls));
        
        ls.clear();
        ls.add("UF");
        ls.add("LI");        
        assertFalse(assetService.isObjectSubTypeCompatible(ls));
    }

    public void testIsMovableFinancialObjectSubtypeCode_Success() throws Exception {
        assertTrue(assetService.isAssetMovableCheckByPayment("C2"));
    }
    
    public void testIsMovableFinancialObjectSubtypeCode_Failure() throws Exception {
        assertFalse(assetService.isAssetMovableCheckByPayment("LI"));
        
        boolean failedAsExpected = false;

        // Test one that doesn't exist, throws exception
        try {
            assetService.isAssetMovableCheckByPayment("XY");
        }
        catch (ValidationException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }
}
