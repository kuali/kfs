/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.gl.service;

import java.io.File;

/**
 * An interface of methods to interact with Origin Entry Groups
 */
public interface OriginEntryGroupService {
    /**
     * Create the backup group which has all the entries from all the groups where all the flags are set Y.
     */
    public void createBackupGroup();
    
    public File createGroup(String fileName);

    public boolean getGroupExists(String groupId);
    
    public String getNewestScrubberErrorFileName();
    
    public File[] getAllFileInBatchDirectory();
    
    public File getFileWithFileName(String fileName);
    
    public void deleteFile(String fileName);
}
