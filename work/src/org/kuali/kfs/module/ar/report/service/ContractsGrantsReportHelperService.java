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
     * Adds an end time (the last moment in the day) to a date string if it is not already there
     * @param dateString the date string to append an end time to
     * @return the String with the appended end time if it didn't have it already
     */
    public String appendEndTimeToDate(String dateString);

    /**
     * Builds and returns a document search URL for the given doc id
     *
     * @param docId document id to build doc search URL for
     * @return URL
     */
    public String getDocSearchUrl(String docId);

}