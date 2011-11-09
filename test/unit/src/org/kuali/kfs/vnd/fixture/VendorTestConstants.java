/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.vnd.fixture;

import java.sql.Date;

import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.businessobject.VendorShippingSpecialCondition;
import org.kuali.kfs.vnd.businessobject.VendorSupplierDiversity;
import org.kuali.rice.core.api.util.type.KualiDecimal;

class VendorTestConstants {

    static class AddressTypes {
        static final String poType = "PO";
        static final String dvType = "DV";
        static final String rmType = "RM";
    }
    
    static class AddressRequiredFields {
        static final String line1Address = "1011 S. Grant Ave.";
        static final String cityName = "Chicago";
    }

    @SuppressWarnings("deprecation")
    static class BeginEndDates {
        static final Date FIRST_DATE = new Date(108, 1, 1); // 2008-01-01
        static final Date LAST_DATE = new Date(109, 1, 1); // 2009-01-01
    }

    static class ContractPOLimitAmts {
        static KualiDecimal highLimit = new KualiDecimal(10000);
        static KualiDecimal lowLimit = new KualiDecimal(1000);
    }

    static class FaxNumbers {
        static final String fax = "123 456-7890";
        static final String defaultFormat = "123-456-7890";
        static final String shortFax = "123 456 789";
    }

    static class VendorParts {
        static final String CODE1 = "C1";
        static final String CODE2 = "C2";
        static final String NAME = "Snow Bound";

        static VendorHeader VH = new VendorHeader();
        static VendorSupplierDiversity VSD = new VendorSupplierDiversity();
        static VendorDetail VD = new VendorDetail();
        static VendorAddress VA = new VendorAddress();
        static VendorContract VC = new VendorContract();
        static VendorShippingSpecialCondition VSSC = new VendorShippingSpecialCondition();
    }

    static class StatesZips {
        static final String stateCd = "CA";
        static final String zipCode = "47401";
    }
}
