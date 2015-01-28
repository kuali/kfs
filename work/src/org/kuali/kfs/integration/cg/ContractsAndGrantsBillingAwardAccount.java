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
package org.kuali.kfs.integration.cg;

import java.sql.Date;

/**
 * Integration interface for AwardAccount
 */
public interface ContractsAndGrantsBillingAwardAccount extends ContractsAndGrantsAccountAwardInformation{
    /**
     * Gets the currentLastBilledDate attribute.
     *
     * @return Returns the currentLastBilledDate.
     */
    public Date getCurrentLastBilledDate();

    /**
     * Gets the previousLastBilledDate attribute.
     *
     * @return Returns the previousLastBilledDate.
     */
    public Date getPreviousLastBilledDate();

    /**
     * Gets the finalBilledIndicator attribute.
     *
     * @return Returns the finalBilledIndicator.
     */
    public boolean isFinalBilledIndicator();
}
