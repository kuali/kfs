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
