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
     * a poster mode, where the poster will post indirect cost recovery encumbrance entries
     */
    public static int MODE_ICRENCMB = 4;

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
     * Post ICR Encumbrance GL entries to GL tables.
     */
    public void postIcrEncumbranceEntries();

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
