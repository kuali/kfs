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
package org.kuali.kfs.module.ld.batch.service;

import org.kuali.kfs.sys.batch.InitiateDirectory;


/**
 * The enterprise feeder is a component that feeds in origin entries from an external data source
 */
public interface EnterpriseFeederService extends InitiateDirectory{
    public static final String DONE_FILE_SUFFIX = ".done";
    public static final String DATA_FILE_SUFFIX = ".data";
    public static final String RECON_FILE_SUFFIX = ".recon";

    /**
     * This method does the feeding
     * 
     * @param processName this merely identifies the process executing the feed. It will be used to build any error messages, but
     *        computationally it should be ignored.
     * @param performNotifications whether notifications should be sent out
     */
    public void feed(String processName, boolean performNotifications);

    /**
     * Returns the directory name from which files are loaded.
     * @return the staging directory name
     */
    public String getDirectoryName();
}
