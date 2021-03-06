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

import java.io.IOException;

import org.kuali.kfs.module.cg.businessobject.CFDA;
import org.kuali.kfs.module.cg.businessobject.CfdaUpdateResults;

/**
 * CFDA (Catalog of Federal Domestic Assistance) service methods.
 */
public interface CfdaService {

    /**
     * This method updates the KFS CFDA table from the US federal government record.
     *
     * @return
     * @throws IOException
     */
    CfdaUpdateResults update() throws IOException;

    /**
     * This method returns a CFDA object corresponding to a cfdaNumber.
     *
     * @param cfdaNumber
     * @return
     */
    public CFDA getByPrimaryId(String cfdaNumber);

}
