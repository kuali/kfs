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
package org.kuali.kfs.module.tem.document.web.bean;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.rice.kns.web.ui.ExtraButton;

public interface TravelAuthorizationMvcWrapperBean extends TravelMvcWrapperBean {
    /**
     * Gets the newEmergencyContactLine attribute.
     *
     * @return Returns the newEmergencyContactLine.
     */
    TravelerDetailEmergencyContact getNewEmergencyContactLine();


    /**
     * Sets the newEmergencyContactLine attribute value.
     *
     * @param newEmergencyContactLine The newEmergencyContactLine to set.
     */
    void setNewEmergencyContactLine(TravelerDetailEmergencyContact newEmergencyContactLine);


    @Override
    /**
     * Gets the newActualExpenseLine attribute.
     *
     * @return Returns the newActualExpenseLine.
     */
    ActualExpense getNewActualExpenseLine();

    @Override
    /**
     * Sets the newActualExpenseLine attribute value.
     *
     * @param newActualExpenseLine The newActualExpenseLine to set.
     */
    void setNewActualExpenseLine(ActualExpense newActualExpenseLine);

    @Override
    /**
     * Gets the newActualExpenseLines attribute.
     *
     * @return Returns the newActualExpenseLines.
     */
    List<ActualExpense> getNewActualExpenseLines();

    @Override
    /**
     * Sets the newActualExpenseLines attribute value.
     *
     * @param newActualExpenseLines The newActualExpenseLines to set.
     */
    void setNewActualExpenseLines(List<ActualExpense> newActualExpenseLines);


    /**
     * Gets the newGroupTravelerLine attribute.
     *
     * @return Returns the newGroupTravelerLine.
     */
    GroupTraveler getNewGroupTravelerLine();

    /**
     * Sets the newGroupTravelerLine attribute value.
     *
     * @param newGroupTravelerLine The newGroupTravelerLine to set.
     */
    void setNewGroupTravelerLine(GroupTraveler newGroupTravelerLine);

    /**
     * Gets the selectedTransportationModes attribute.
     *
     * @return Returns the selectedTransportationModes.
     */
    List<String> getSelectedTransportationModes();

    /**
     * Sets the selectedTransportationModes attribute value.
     *
     * @param selectedTransportationModes The selectedTransportationModes to set.
     */
    void setSelectedTransportationModes(List<String> selectedTransportationModes);

    List<String> getTempSelectedTransporationModes();

    /**
     * @return TravelAuthorizationDocument
     */
    TravelAuthorizationDocument getTravelAuthorizationDocument();

    void setNewTraveler(final TravelerDetail traveler);

    TravelerDetail getNewTraveler();

    @Override
    List<ExtraButton> getExtraButtons();

    boolean isCanUnmask();

    void setCanUnmask(boolean canUnmask);


    @Override
    Map<String, String> getModesOfTransportation();
}
