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
package org.kuali.kfs.module.endow.batch.service;

public interface UpdateCorpusService {

    /**
     * Updates the corpus records in four main methods:
     * 1) If first day of fiscal year, Copies KEMID Corpus current year values to prior year values.
     * 2) Sums Tran Archive table and adds to Current Endow Corpus, then updates KEMID Corpus
     * 3) Updates every record in Current Endow Corpus for PRIN_MVAL with values from Current balance view.
     * 4) If last day of fiscal year, copy all records from Current Endow Corpus to Endow Corpus.
     * 
     * @return
     */
    public boolean updateCorpusTransactions();
}
