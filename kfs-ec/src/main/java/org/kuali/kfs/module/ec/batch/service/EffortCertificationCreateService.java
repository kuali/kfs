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
package org.kuali.kfs.module.ec.batch.service;

/**
 * The interface defines the methods that creates effort certification documents from the temporary build documents and routes them
 * for approval.
 */
public interface EffortCertificationCreateService {

    /**
     * create effort certification documents and submit it for approval. The reporting period can be determined by both of fiscal
     * year and report number declared as system parameters.
     */
    public void create();

    /**
     * create effort certification documents and submit it for approval. The reporting period can be determined by both of fiscal
     * year and report number.
     * 
     * @param fiscalYear the given fiscal year with which Labor ledgers can be extracted.
     * @param reportNumber the given report number to run.
     */
    public void create(Integer fiscalYear, String reportNumber);
}
