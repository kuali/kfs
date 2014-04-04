/*
 * Copyright 2010 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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