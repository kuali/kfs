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
package org.kuali.kfs.gl.batch.service;

import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * An interface of methods to run the Poster
 */
public interface PosterService {
    /**
     * a poster mode, where the poster will post entries
     */
    public static int MODE_ENTRIES = 1;
    /**
     * a poster mode, where the poster will post reversals that are due to reverse
     */
    public static int MODE_REVERSAL = 2;
    /**
     * a poster mode, where the poster will post indirect cost recovery entries
     */
    public static int MODE_ICR = 3;

    /**
     * Post scrubbed GL entries to GL tables.
     */
    public void postMainEntries();

    /**
     * Post reversal GL entries to GL tables.
     */
    public void postReversalEntries();

    /**
     * Post ICR GL entries to GL tables.
     */
    public void postIcrEntries();

    /**
     * Generate ICR GL entries.
     */
    public void generateIcrTransactions();

    /**
     * Sets the dateTimeAttribute of the service
     * 
     * @param dateTimeService the dateTimeService implementation to set
     */
    public void setDateTimeService(DateTimeService dateTimeService);
}
