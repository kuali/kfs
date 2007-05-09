/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.purap;

import org.kuali.core.JstlConstants;
import org.kuali.core.authorization.AuthorizationConstants;

/**
 * Defines constants used in authorization-related code.
 * 
 * 
 */
public class PurapAuthorizationConstants extends AuthorizationConstants {

    public static class RequisitionEditMode extends EditMode {
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
        public static final String LOCK_CONTENT_ENTRY = "lockContentEntry";
        public static final String ALLOW_FISCAL_ENTRY = "allowFiscalEntry";
    }

    public static class PurchaseOrderEditMode extends EditMode {
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
        public static final String DISPLAY_RETRANSMIT_TAB = "displayRetransmitTab";
    }

    public static class PaymentRequestEditMode extends EditMode {
        public static final String LOCK_VENDOR_ENTRY = "lockVendorEntry";
        public static final String DISPLAY_INIT_TAB = "displayInitTab";
        public static final String ALLOW_FISCAL_ENTRY = "allowFiscalEntry";
    }

}
