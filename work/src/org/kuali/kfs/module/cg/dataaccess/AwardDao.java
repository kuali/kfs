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
package org.kuali.kfs.module.cg.dataaccess;

import java.util.Collection;

import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;

/**
 * Implementations of this interface provide access to persisted Awards.
 */
public interface AwardDao {

    /**
     * Get a {@link Collection} of {@link Award}s to close. This is used by the {@link CloseBatchStep}.
     * 
     * @param c
     * @return
     */
    public Collection<Award> getAwardsToClose(ProposalAwardCloseDocument c);

    /**
     * Delete all Awards.
     */
    public void deleteAll();
}
