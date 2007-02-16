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

package org.kuali.module.financial.document;

/**
 * This is the business object that represents the <code>{@link YearEndDocument}</code> version of
 * <code>{@link GeneralErrorCorrectionDocument}</code> in Kuali. This is a transactional document that will eventually post
 * transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines: from and to. From lines
 * are the source lines, to lines are the target lines. This document is exactly the same as the non-<code>{@link YearEndDocument}</code>
 * version except that it has slightly different routing and that it only allows posting to the year end accounting period for a
 * year.
 */
public class YearEndGeneralErrorCorrectionDocument extends GeneralErrorCorrectionDocument implements YearEndDocument {

    /**
     * Initializes the array lists and some basic info.
     */
    public YearEndGeneralErrorCorrectionDocument() {
        super();
    }
}
