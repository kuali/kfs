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
package org.kuali.kfs.coa.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectLevel;

/**
 * This service interface defines methods necessary for retrieving fully populated ObjLevel business objects from the database
 * that are necessary for transaction processing in the application.
 */
public interface ObjectLevelService {
    /**
     * Retrieves an object level object instance by its composite primary id.
     *
     * @param chartOfAccountsCode
     * @param ObjectLevelCode
     * @return An ObjLevel object instance.
     */
    public ObjectLevel getByPrimaryId(String chartOfAccountsCode, String ObjectLevelCode);

    /**
     * This method returns a list of Object Levels that correspond to a list of Consolidation Object Codes.
     *
     * @param consolidationIds List of Consolidation Object Codes used to retrieve Object Levels
     * @return List of Object Levels
     */
    public List<ObjectLevel> getObjectLevelsByConsolidationsIds(List<String> consolidationIds);

    /**
     * This method returns a list of Object Levels that correspond to a list of Object Level Codes.
     *
     * @param levelCodes List of Object Level Codes used to retrieve Object Levels
     * @return List of Object Levels
     */
    public List<ObjectLevel> getObjectLevelsByLevelIds(List<String> levelCodes);
}
