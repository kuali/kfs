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
package org.kuali.kfs.module.tem.document.service;

import org.kuali.kfs.module.tem.businessobject.TemProfileArranger;
import org.kuali.kfs.module.tem.document.TravelArrangerDocument;

public interface TravelArrangerDocumentService {

    public void createTravelProfileArranger(TravelArrangerDocument arrangerDoc);

    public TemProfileArranger findPrimaryTravelProfileArranger(String arrangerId, Integer profileId);

    public TemProfileArranger findTemProfileArranger(String principalId, Integer profileId);

    public void inactivateTravelProfileArranger(TravelArrangerDocument arrangerDoc);

    /**
     * Determines if the user with the given principal id is an active arranger for any profiles
     * @param principalId the principal id to check if the user is an arranger for
     * @return true if the given principal id represents a user who arranges for any travel profiles; false otherwise
     */
    public boolean hasArrangees(String principalId);

}
