/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Interface of services which want to help out with building Contracts & Grants Report Services
 */
public interface ContractsGrantsReportHelperService {

    /**
     * This method generates reports for the Contracts Grants Report Services
     * @param reportDataHolder
     * @param reportInfo
     * @param baos
     * @return
     */
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ReportInfo reportInfo, ByteArrayOutputStream baos);

    /**
     * Creates a list of the property values of a given property
     * @param list a List of objects to get property names from
     * @param propertyName the property name to find values for
     * @return a List of only those values.  Evidently they're always Strings
     */
    public List<String> getListOfValuesSortedProperties(List list, String propertyName);

    /**
     * Retrieves a String property value from an object
     * @param object the object to get a property name from
     * @param propertyName the name of the property to retrieve
     * @return the property value, turned into a String and with leading and trailing whitespace removed; or a blank String
     */
    public String getPropertyValue(Object object, String propertyName);

    /**
     * Looks up the sort field names for the business object given by the businessObjectName, and returns the index'ed one of those
     * @param index the sort field name position we want to pull information from
     * @param businessObjectName the name of the business object to find sort fields for
     * @return the name of the sort field
     */
    public String getFieldNameForSorting(int index, String businessObjectName);

    /**
     * Creates a title for the given business object class being reported on
     * @param boClass the class being report on
     * @return an appropriate title
     */
    public String createTitleText(Class<? extends BusinessObject> boClass);

    /**
     * Formats a given property value by what type it is
     * @param prop the property to format
     * @param preferredFormatter the preferredFormatter to use
     * @return the formatted property
     */
    public String formatByType(Object prop, Formatter preferredFormatter);

    /**
     * Since times seem to be passed to the database by lookup service in kind of wonky ways, we're going to correct for that by
     * adding a day to dates at upper bounds of criteria to guarantee inclusion of all of the records from the previous day
     * @param dateString the date string to add a day to
     * @return the date string of the very next day
     */
    public String correctEndDateForTime(String dateString);

    /**
     * Builds and returns a document search URL for the given doc id
     *
     * @param docId document id to build doc search URL for
     * @return URL
     */
    public String getDocSearchUrl(String docId);

    /**
     * Builds and returns a URL to initiate a Collection Activity Document for the given proposal number and invoice number.
     *
     * @param proposalNumber
     * @param invoiceNumber
     * @return URL
     */
    public String getInitiateCollectionActivityDocumentUrl(String proposalNumber, String invoiceNumber);

    /**
     * Translates the date criteria to a form which the LookupService will comprehend
     * @param dateLowerBound the lower bound of the date
     * @param dateUpperBound the upper bound of the date
     * @param includeTime denotes whether time should be included on the upper bound
     * @return the date criteria, or null if nothing could be constructed
     */
    public String fixDateCriteria(String dateLowerBound, String dateUpperBound, boolean includeTime);

    /**
     * Does a lookup on the given principal name and joins the principal ids of any matches together as an or'd String, ready for another lookup
     * @param principalName principalName to find matches for
     * @return a Set of matching principalIds
     */
    public Set<String> lookupPrincipalIds(String principalName);
}