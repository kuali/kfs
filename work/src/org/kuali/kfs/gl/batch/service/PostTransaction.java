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
/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.kfs.gl.batch.service;

import java.util.Date;

import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.service.ReportWriterService;

/**
 * An interface that delcares the methods that the Poster needs to post a transaction.
 */
public interface PostTransaction {
    /**
     * Post a single transaction to a single destination.
     * 
     * @param t Transaction to post
     * @param mode PosterService.MODE_ENTRIES or PosterService.MODE_REVERSAL
     * @param postDate post date/time
     * @param posterReportWriterService the writer service where the poster is writing its report 
     * @return The letter I if a row was inserted, U updated, D deleted. The string can have multiple codes.
     */
    public String post(Transaction t, int mode, Date postDate, ReportWriterService posterReportWriterService);

    /**
     * The name of the destination for the post (ie, the database table name where resultant records will be posted)
     * 
     * @return name
     */
    public String getDestinationName();
}
