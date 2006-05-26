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

public interface ScrubberValidator {
    public List validateTransaction(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate);
    public String validateAccount(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate);
    public String validateBalanceType(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateChart(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateDocumentNumber(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateDocumentType(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateEncumbranceUpdateCode(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateFiscalYear(OriginEntry originEntry, OriginEntry scrubbedEntry,UniversityDate universityRunDate);
    public String validateObjectCode(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateObjectType(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateOrigination(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateProjectCode(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateReferenceDocument(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateReversalDate(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateSubAccount(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateSubObjectCode(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateTransactionAmount(OriginEntry originEntry, OriginEntry scrubbedEntry);
    public String validateTransactionDate(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate);
    public String validateUniversityFiscalPeriodCode(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate);
}
