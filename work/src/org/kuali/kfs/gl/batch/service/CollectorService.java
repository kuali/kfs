/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.service;


public interface CollectorService {
    /**
     * Given an inputStream to the collector XML, load the data into the origin entry table. If there are errors loading the file,
     * throw a CollectorLoadException that has a list of all the issues.
     * 
     * @param inputStream inputStream of collector XML
     * @return origin entry group ID
     */
    public void loadCollectorFile(String fileName);

    /**
     * Given a group ID, scrub all of the transactions. If there are errors with the data, return a list of them. If there are no
     * errors, return an empty list.
     * 
     * @return string staging directory
     */
    public String getStagingDirectory();
}
