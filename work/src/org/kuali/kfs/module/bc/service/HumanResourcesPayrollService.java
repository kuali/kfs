/*
 * Copyright 2008 The Kuali Foundation
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
