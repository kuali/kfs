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
package org.kuali.kfs.module.tem.batch.service;

import java.util.List;

import org.kuali.kfs.module.tem.batch.businessobject.PerDiemForLoad;
import org.kuali.kfs.sys.batch.BatchInputFileType;

/**
 * define the service methods that load federal per diem records from a file
 */
public interface PerDiemLoadService {

    /**
     * create and populate per diem table from a specified external resource
     *
     * @return true if successfully create and populate per diem table from a specified external resource
     */
    public boolean loadPerDiem();

    /**
     * create and populate per diem table according to a specified input file type
     *
     * @param fileName the absolute file name
     * @param inputFileType the specified input file type
     * @return true if successfully create and populate per diem table from a specified input file type
     */
    public boolean loadPerDiem(String dataFileName, BatchInputFileType inputFileType);

    /**
     * update the given list of per diems
     *
     * @param perDiem the given list of per diems
     * @return a List of PerDiem records which have been processed, with all records which should skip processing removed
     */
    public List<PerDiemForLoad> updatePerDiem(List<PerDiemForLoad> perDiemList);

    /**
     * update the given per diem
     *
     * @param perDiem the the given per diem
     */
    public void updatePerDiem(PerDiemForLoad perDiem);
}
