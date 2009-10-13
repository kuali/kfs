/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.service;

import java.util.List;

import org.kuali.kfs.sys.document.web.AccountingLineTableRow;

/**
 * A contract of services that wish to transform accounting line renderable elements after their tablification
 */
public interface AccountingLineTableTransformation {
    /**
     * Performs transformation to the tablified rows for an accounting line
     * @param rows the tablified rows that represent a renderable accounting line
     */
    public abstract void transformRows(List<AccountingLineTableRow> rows);
}
