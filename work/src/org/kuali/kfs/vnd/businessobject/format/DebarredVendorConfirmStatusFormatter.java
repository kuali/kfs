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
package org.kuali.kfs.vnd.businessobject.format;

import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.rice.core.web.format.Formatter;

public class DebarredVendorConfirmStatusFormatter extends Formatter {
    @Override
    public Object format(Object target) {
        if (target == null) {
            return null;
        }
        String status = (String)target;
        if (status.equals(VendorConstants.DEBARRED_VENDOR_UNPROCESSED)) {
            status = VendorConstants.DEBARRED_VENDOR_UNPROCESSED_LABEL;
        } else if (status.equals(VendorConstants.DEBARRED_VENDOR_CONFIRMED)){
            status = VendorConstants.DEBARRED_VENDOR_CONFIRMED_LABEL;
        } else if (status.equals(VendorConstants.DEBARRED_VENDOR_DENIED)) {
            status = VendorConstants.DEBARRED_VENDOR_DENIED_LABEL;
        }
        return status;
    }
}
