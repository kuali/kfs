/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.businessobject.GenerateDunningLettersLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.rice.kim.api.identity.Person;

import com.lowagie.text.DocumentException;

public interface DunningLetterService {

    /**
     * Creates a byte stream of pdfs for all dunning letters created for the given GenerateDunningLettersLookupResult.
     *
     * @param results the dunning letter lookup results to create pdfs for
     * @return a byte stream of PDFS
     */
    public byte[] createDunningLettersForAllResults(Collection<GenerateDunningLettersLookupResult> results) throws DocumentException, IOException;

    /**
     * This method generates the actual pdf files to print.
     *
     * @param report
     * @param baos
     * @return
     * @throws IOException
     */
    public boolean createZipOfPDFs(byte[] report, ByteArrayOutputStream baos) throws IOException;

    /**
     * Determines if the given DunningLetterTemplate's organization is valid for the given user.
     *
     * @param template the template to check the organization of
     * @param user the user attempting to use the template
     * @return true if the current user can use the template, false otherwise
     */
    public boolean isValidOrganizationForTemplate(DunningLetterTemplate template, Person user);

    /**
     * This helper method returns a list of award lookup results based on the Generate Dunning Letters lookup
     * @param invoices the invoices to convert into DunningLetterDistributionLookupResult data transfer objects
     * @return a Collection of DunningLetterDistributionLookupResult data transfer objects
     */
    public Collection<GenerateDunningLettersLookupResult> getPopulatedGenerateDunningLettersLookupResults(Collection<ContractsGrantsInvoiceDocument> invoices);
}
