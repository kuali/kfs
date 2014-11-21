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
package org.kuali.kfs.module.tem.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.module.tem.batch.AgencyDataXmlInputFileType;
import org.kuali.kfs.module.tem.businessobject.AgencyEntryFull;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.service.AgencyEntryGroupService;
import org.kuali.kfs.module.tem.service.AgencyEntryService;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class AgencyEntryServiceImpl implements AgencyEntryService {
    private AgencyEntryGroupService agencyEntryGroupService;

    protected DateTimeService dateTimeService;
    protected String batchFileErrorDirectoryName;
    protected String batchFileDirectoryName;
    protected AgencyDataXmlInputFileType agencyDataXmlInputFileType;

    List<AgencyEntryFull> agencyGroup = new ArrayList<AgencyEntryFull>();

    @Override
    public void createEntry(AgencyEntryFull agencyEntry, PrintStream ps) {
        // TODO Auto-generated method stub

    }

    @Override
    public void flatFile(Iterator<AgencyEntryFull> entries, BufferedOutputStream bw) {
        // TODO Auto-generated method stub

    }

    @Override
    public Integer getGroupCount(String fileNameWithPath) {
        File file = new File(batchFileErrorDirectoryName + "/" + fileNameWithPath);
        if (agencyGroup.size() == 0) {
            try {
                FileInputStream fileContents = new FileInputStream(file);

                byte[] fileByteContent = IOUtils.toByteArray(fileContents);
                List<AgencyStagingData> temp = (List<AgencyStagingData>) this.getAgencyDataXmlInputFileType().parse(fileByteContent);
                int entryId = 0;
                for (AgencyStagingData agency : temp) {
                    AgencyEntryFull fullAgency = new AgencyEntryFull(agency, entryId);
                    entryId++;
                    agencyGroup.add(fullAgency);
                }

            }
            catch (Exception ex) {
                // figure it out
            }
        }


        return agencyGroup.size();
    }

    @Override
    public Map getEntriesByBufferedReader(BufferedReader inputBufferedReader, List<AgencyEntryFull> agencyEntryList) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map getEntriesByGroupIdWithPath(String fileNameWithPath, List<AgencyEntryFull> agencyEntryList) {
        Map errorMap = new HashMap<String, String>();
        if (agencyGroup.size() == 0) {
            File file = new File(batchFileErrorDirectoryName + "/" + fileNameWithPath);
            try {
                FileInputStream fileContents = new FileInputStream(file);

                byte[] fileByteContent = IOUtils.toByteArray(fileContents);
                List<AgencyStagingData> temp = (List<AgencyStagingData>) this.getAgencyDataXmlInputFileType().parse(fileByteContent);
                int entryId = 1;
                for (AgencyStagingData agency : temp) {
                    AgencyEntryFull fullAgency = new AgencyEntryFull(agency, entryId);
                    fullAgency.setEntryId(entryId);
                    entryId++;
                    agencyGroup.add(fullAgency);
                }
                agencyEntryList.addAll(agencyGroup);

            }
            catch (Exception ex) {
                // figure it out
            }
        }
        else {
            agencyEntryList.addAll(agencyGroup);
        }

        return errorMap;
    }

    /**
     * Gets the agencyEntryGroupService attribute.
     *
     * @return Returns the agencyEntryGroupService.
     */
    public AgencyEntryGroupService getAgencyEntryGroupService() {
        return agencyEntryGroupService;
    }

    /**
     * Sets the agencyEntryGroupService attribute value.
     *
     * @param agencyEntryGroupService The agencyEntryGroupService to set.
     */
    public void setAgencyEntryGroupService(AgencyEntryGroupService agencyEntryGroupService) {
        this.agencyEntryGroupService = agencyEntryGroupService;
    }

    /**
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the batchFileDirectoryName attribute.
     *
     * @return Returns the batchFileDirectoryName.
     */
    public String getBatchFileErrorDirectoryName() {
        return batchFileErrorDirectoryName;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     *
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileErrorDirectoryName(String batchFileErrorDirectoryName) {
        this.batchFileErrorDirectoryName = batchFileErrorDirectoryName;
    }

    /**
     * Gets the agencyDataXmlInputFileType attribute.
     *
     * @return Returns the agencyDataXmlInputFileType.
     */
    public AgencyDataXmlInputFileType getAgencyDataXmlInputFileType() {
        return agencyDataXmlInputFileType;
    }

    /**
     * Sets the agencyDataXmlInputFileType attribute value.
     *
     * @param agencyDataXmlInputFileType The agencyDataXmlInputFileType to set.
     */
    public void setAgencyDataXmlInputFileType(AgencyDataXmlInputFileType agencyDataXmlInputFileType) {
        this.agencyDataXmlInputFileType = agencyDataXmlInputFileType;
    }

    /**
     * Gets the batchFileDirectoryName attribute.
     * @return Returns the batchFileDirectoryName.
     */
    public String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
