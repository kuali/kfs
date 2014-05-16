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
import java.util.Collection;

import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionLookupResult;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.rice.kim.api.identity.Person;

import com.lowagie.text.DocumentException;

public interface DunningLetterDistributionService {
    /**
     * Creates a byte stream of pdfs for all dunning letters created for the given DunningLetterDistributionLookupResults
     * @param results the dunning letter lookup results to create pdfs for
     * @return a byte stream of PDFS
     */
    public byte[] createDunningLettersForAllResults(Collection<DunningLetterDistributionLookupResult> results) throws DocumentException, IOException;

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

    /**
     * Determines if the given DunningLetterTemplate's organization is valid for the given user
     * @param template the template to check the organization of
     * @param user the user attempting to use the template
     * @return true if the current user can use the template, false otherwise
     */
    public boolean isValidOrganizationForTemplate(DunningLetterTemplate template, Person user);

}
