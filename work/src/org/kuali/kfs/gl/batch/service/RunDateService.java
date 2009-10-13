/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.batch.service;

import java.util.Date;

/**
 * Sometimes it is necessary to rerun a GL process on the following day if the previous night's GL batch processes failed. This
 * service facilitates the re-running of GL batch processes by allowing the GL processes to assume that the GL processes are being
 * run the night before
 */
public interface RunDateService {
    /**
     * Returns the assumed runtime given the actual execution time.
     * 
     * @param executionDate the actual date that this method is called
     * @return the run date/time to assume when running the GL processes
     */
    public Date calculateRunDate(Date executionDate);
}
