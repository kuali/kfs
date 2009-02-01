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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentTypeCode;

/**
 * This class is used to retrieve financial information about certain documents by utilizing the document type name
 */
public interface FinancialSystemDocumentTypeCodeService {

    public FinancialSystemDocumentTypeCode getFinancialSystemDocumentTypeCodeByTransactionalDocumentClass(Class documentClass);

    public FinancialSystemDocumentTypeCode getFinancialSystemDocumentTypeCodeByDocumentName(String documentTypeName);

    public FinancialSystemDocumentTypeCode getFinancialSystemDocumentTypeCodeByPrimaryKey(String financialSystemDocumentTypeCode);
}
