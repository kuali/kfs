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
package org.kuali.kfs.module.bc.document.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.kuali.kfs.module.bc.util.ExternalizedMessageWrapper;
import org.kuali.rice.kim.api.identity.Person;

import com.lowagie.text.DocumentException;

public interface PayrateImportService {
    
    /**
     * Parses file and creates payrate holding records for each import line
     * 
     * @param fileImportStream
     * @return
     */
    public boolean importFile(InputStream fileImportStream, List<ExternalizedMessageWrapper> messageList, String principalId);
    
    /**
     * Processes all payrate holding records
     */
    public void update(Integer budgetYear, Person user, List<ExternalizedMessageWrapper> messageList, String principalId);
    
    /**
     * Generates the log file
     * 
     * @param errorMessages
     * @param baos
     * @throws DocumentException
     */
    public void generatePdf(List<ExternalizedMessageWrapper> logMessages, ByteArrayOutputStream baos) throws DocumentException;
    
}

