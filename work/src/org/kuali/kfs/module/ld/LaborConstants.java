/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor;

/**
 * This class...
 */
public class LaborConstants {
    public static class PayrollDocumentTypeCode{
        public static final String NORMAL_PAY = "PAY";
        public static final String RETROACTIVE_ADJUSTMENT = "RETR";
        
        public static final String ENCUMBRANCE = "PAYE";
        public static final String CHECK_CANCELLATION = "PAYC";
        public static final String OVERPAYMENT = "OPAY";
        
        public static final String HAND_DRAWN_CHECK = "HDRW";
        public static final String ACCRUALS = "PAYA";
        public static final String ACCRUALS_REVERSAL = "PAYN";
        
        public static final String EXPENSE_TRANSFER_ST = "ST";
        public static final String EXPENSE_TRANSFER_BT = "BT";
        public static final String EXPENSE_TRANSFER_ET = "ET";
        public static final String EXPENSE_TRANSFER_SACH = "SACH";
        public static final String EXPENSE_TRANSFER_YEST = "YEST";
        public static final String EXPENSE_TRANSFER_YEBT = "YEBT";
    }
}
