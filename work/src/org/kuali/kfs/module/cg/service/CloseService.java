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
package org.kuali.kfs.module.cg.service;

import java.sql.Date;

import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;

/**
 * Intended to be initiated periodically via a batch process. The default implementation of this service finds all unclosed Awards
 * that are not underwritten and all unclosed Proposals. For each of those proposals and awards the close date is set to the date on
 * which the close was executed.
 */
public interface CloseService {

    /**
     * See class description.
     */
    public boolean close();

    /**
     * Gets the approved instance with the latest close date.
     *
     * @return the persisted instance with the latest close date.
     */
    public ProposalAwardCloseDocument getMostRecentClose();

    /**
     * Gets the persisted instance with the latest close date.
     *
     * @param currentSqlMidnight
     * @return the persisted instance with the latest close date.
     */
    public ProposalAwardCloseDocument getMaxApprovedClose(Date currentSqlMidnight);

}
