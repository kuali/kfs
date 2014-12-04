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
package org.kuali.kfs.module.ar.report.service;

import java.util.List;

import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Interface for services which want to build AR Contracts & Grants Billing reports
 */
public interface ContractsGrantsReportDataBuilderService {

    /**
     * Builds a new ContractsGrantsReportDataHolder object for a set of details.
     *
     * @param displayList the set of looked-up data to build a report from
     * @param sortPropertyName the name of the property to sort everything by
     * @return a ContractsGrantsReportDataHolder for a class
     */
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<? extends BusinessObject> displayList, String sortPropertyName);

    /**
     * This method returns the class of the details that the given implementation builds report details out of.
     *
     * @return the class of the details that the given implementation builds report details out of
     */
    public Class<? extends BusinessObject> getDetailsClass();

    /**
     * This method returns the ReportInfo object associated with this report.
     *
     * @return the ReportInfo object associated with this report
     */
    public ReportInfo getReportInfo();

}
