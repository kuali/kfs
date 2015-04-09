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
package org.kuali.kfs.module.ar.dataaccess;

import java.util.Collection;
import java.util.Date;

import org.kuali.kfs.module.ar.businessobject.Milestone;

/**
 * Implementations of this interface provide access to persisted Milestone instances.
 */
public interface MilestoneDao {

    /**
     * This method returns a list of Milestones for sending notification email from batch job.
     *
     * Criteria: milestones are active and non-billed, with a null actual completion date and
     * an expected completion date <= the expected completion limit date.
     *
     * @return a {@link Collection} of appropriate {@link Milestone}.
     */
    public Collection<Milestone> getMilestonesForNotification(Date expectedCompletionLimitDate);

}
