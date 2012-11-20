/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     */
    public void updatePerDiem(List<PerDiemForLoad> perDiemList);

    /**
     * update the given per diem
     * 
     * @param perDiem the the given per diem
     */
    public void updatePerDiem(PerDiemForLoad perDiem);
}