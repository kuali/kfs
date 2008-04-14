/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cg.service;

import org.kuali.module.cg.bo.Close;

/**
 * Intended to be initiated periodically via a batch process. The default implementation of this service finds all unclosed Awards
 * that are not underwritten and all unclosed Proposals. For each of those proposals and awards the close date is set to the date on
 * which the close was executed.
 */
public interface CloseService {

    /**
     * See class description.
     */
    public void close();

    /**
     * Gets the Close record with the greatest close date.
     * 
     * @return the close with the greatest close date.
     */
    public Close getMostRecentClose();

}
