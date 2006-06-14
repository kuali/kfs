/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service;

import java.util.List;

import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.service.impl.scrubber.Message;

public interface ScrubberValidator {
    public List<Message> validateTransaction(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate);

    public Message validateAccount(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate);

    public Message validateBalanceType(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateChart(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateDocumentNumber(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateDocumentType(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateFiscalYear(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate);

    public Message validateObjectCode(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateObjectType(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateOrigination(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateProjectCode(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateReferenceDocumentFields(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateReversalDate(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateSubAccount(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateSubObjectCode(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateTransactionAmount(OriginEntry originEntry, OriginEntry scrubbedEntry);

    public Message validateTransactionDate(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate);

    public Message validateUniversityFiscalPeriodCode(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate);
}
