/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab;

import org.kuali.rice.core.util.JSTLConstants;

public class CabConstants extends JSTLConstants {
    public static final String DATE_FORMAT_TS = "MM/dd/yyyy HH:mm:ss";

    public static class Parameters {
        public static final String LAST_EXTRACT_TIME = "LAST_EXTRACT_TIME";
        public static final String SUB_FUND_GROUPS = "SUB_FUND_GROUPS";
        public static final String FISCAL_PERIODS = "FISCAL_PERIODS";
        public static final String DOCUMENT_TYPES = "DOCUMENT_TYPES";
        public static final String CHARTS = "CHARTS";
        public static final String OBJECT_SUB_TYPES = "OBJECT_SUB_TYPES";
        public static final String BALANCE_TYPES = "BALANCE_TYPES";
        public static final String NAMESPACE = "KFS-CAB";
        public static final String DETAIL_TYPE_BATCH = "Batch";
    }

    public static final String PREQ = "PREQ";
    public static final String CM = "CM";
    
    public static final String TRADE_IN_TYPE_CODE = "TRDI";
    
    public static final String CB_INVOICE_LINE_ACTION = "../cabPurApLine.do";
    public static final String DOT_DOC = ".doc";
    public static final String DOT_LINE = ".line";
    
    public static class Actions {
        public static final String PROCESS = "process";
        public static final String START = "start";
        public static final String SPLIT = "split";
    }
    
}
