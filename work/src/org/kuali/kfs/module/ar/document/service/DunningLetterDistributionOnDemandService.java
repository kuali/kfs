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
package org.kuali.kfs.module.ar.document.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionOnDemandLookupResult;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;

public interface DunningLetterDistributionOnDemandService {


    /**
     * This method generates the actual pdf file with related invoices to the template to print.
     * 
     * @param dunningLetterTemplate
     * @param dunningLetterDistributionOnDemandLookupResult
     * @return
     */
    public byte[] createDunningLetters(DunningLetterTemplate dunningLetterTemplate, DunningLetterDistributionOnDemandLookupResult dunningLetterDistributionOnDemandLookupResult);

    /**
     * This method generates the actual pdf files to print.
     * 
     * @param mapping
     * @param form
     * @param list
     * @return
     * @throws Exception
     */
    public boolean createZipOfPDFs(byte[] report, ByteArrayOutputStream baos) throws IOException;

}
