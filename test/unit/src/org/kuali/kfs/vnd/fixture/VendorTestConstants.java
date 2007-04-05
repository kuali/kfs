/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.vendor.fixtures;

import java.sql.Date;

import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.bo.VendorHeader;
import org.kuali.module.vendor.bo.VendorShippingSpecialCondition;
import org.kuali.module.vendor.bo.VendorSupplierDiversity;
import org.kuali.module.vendor.service.PhoneNumberService;

class VendorTestConstants {
       
    @SuppressWarnings("deprecation")
    static class BeginEndDates {
        static final Date FIRST_DATE = new Date(108,1,1); //2008-01-01
        static final Date LAST_DATE = new Date(109,1,1);  //2009-01-01
        static RequisitionDocument REQ = new RequisitionDocument();
        static PurchaseOrderDocument PO = new PurchaseOrderDocument();
    }
    
    static class VendorParts {
        static final String CODE1 = "C1";
        static final String CODE2 = "C2";
        static final String NAME = "Snow Bound";
        
        static VendorHeader VH = new VendorHeader();
        static VendorSupplierDiversity VSD = new VendorSupplierDiversity();
        static VendorDetail VD = new VendorDetail();
        static VendorAddress VA  = new VendorAddress();
        static VendorContract VC  = new VendorContract();
        static VendorShippingSpecialCondition VSSC = new VendorShippingSpecialCondition();
    }
    
    static class FaxNumbers {
        static String fax = "123 456-7890";
        static String defaultFormat = "123-456-7890";
        static String shortFax = "123 456 789";
    }
}
