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
package org.kuali.kfs.module.bc.service;

import org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType;
import org.kuali.kfs.module.bc.businessobject.Incumbent;
import org.kuali.kfs.module.bc.businessobject.Position;
import org.kuali.kfs.module.bc.exception.IncumbentNotFoundException;
import org.kuali.kfs.module.bc.exception.PositionNotFoundException;

/**
 * Provides service methods for obtaining information from an external payroll/hr system. Used primarily by the Budget Construction
 * and Labor Distribution modules to provide position data in the KFS.
 */
public interface HumanResourcesPayrollService {

    /**
     * @param positionUnionCode
     * @return
     */
    public boolean validatePositionUnionCode(String positionUnionCode);

    /**
     * Pulls position data from an external system for the given position identifying fields and populates a <code>Position</code>
     * object.
     * 
     * @param universityFiscalYear position fiscal year, part of record key
     * @param positionNumber position number, part of record key
     * @return <code>Position</code> object
     * @exception PositionNotFoundException thrown if no data was found for the position key
     * @see org.kuali.kfs.module.bc.businessobject.Position
     */
    public Position getPosition(Integer universityFiscalYear, String positionNumber) throws PositionNotFoundException;

    /**
     * Pulls incumbent data from an external system for the given employee id and populates a <code>Incumbent</code> object.
     * 
     * @param emplid university id for the incumbent to pull
     * @return <code>Incumbent</code> object
     * @throws IncumbentNotFoundException thrown if no data was found for the employee id
     * @see org.kuali.kfs.module.bc.businessobject.Incumbent
     */
    public Incumbent getIncumbent(String emplid) throws IncumbentNotFoundException;

    /**
     * determine whether there is an active job for the given emplid on the specified position
     * 
     * @param emplid the given employee id
     * @param positionNumber the specified position number
     * @param fiscalYear the given fiscal year
     * @param synchronizationCheckType the sync check type
     * @return true there is an active job for the given emplid on the specified position; otherwise, false
     */
    public boolean isActiveJob(String emplid, String positionNumber, Integer fiscalYear, SynchronizationCheckType synchronizationCheckType);
}
