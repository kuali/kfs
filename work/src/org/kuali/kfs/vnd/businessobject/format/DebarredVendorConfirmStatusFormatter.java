/*
 * Copyright 2012 The Kuali Foundation.
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
