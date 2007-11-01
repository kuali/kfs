/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service;


import java.io.IOException;
import java.io.OutputStream;

import org.kuali.module.financial.document.DisbursementVoucherDocument;

import com.lowagie.text.DocumentException;

/**
 * Service used for manipulating disbursement voucher cover sheets.
 */
public interface DisbursementVoucherCoverSheetService {
    /**
     * generates a disbursement voucher coversheet
     * 
     * @param templateName
     * @param document
     * @param outputStream
     */
    public void generateDisbursementVoucherCoverSheet(String templateDirectory, String templateName, DisbursementVoucherDocument document, OutputStream outputStream) throws DocumentException, IOException;
}
