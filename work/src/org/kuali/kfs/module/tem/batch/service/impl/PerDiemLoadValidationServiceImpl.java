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
package org.kuali.kfs.module.tem.batch.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.batch.service.PerDiemLoadValidationService;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * implement the validation methods against the given per diem
 */
public class PerDiemLoadValidationServiceImpl implements PerDiemLoadValidationService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PerDiemLoadValidationServiceImpl.class);

    private DictionaryValidationService dictionaryValidationService;
    private PerDiemService perDiemService;

    /**
     * @see org.kuali.kfs.module.tem.batch.service.PerDiemLoadValidationService#validate(java.util.List)
     */
    @Override
    public <T extends PerDiem> boolean validate(List<T> perDiemList) {
        MessageMap messageMap = GlobalVariables.getMessageMap();

        for(T perDiem: perDiemList){
            List<Message> errorMessages = this.validate(perDiem);

            this.populateMessageMap(messageMap, errorMessages);

            if(ObjectUtils.isNotNull(messageMap) && messageMap.hasErrors()){
                return false;
            }
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.PerDiemLoadValidationService#validate(org.kuali.kfs.module.tem.businessobject.PerDiem)
     */
    @Override
    public <T extends PerDiem> List<Message> validate(T perDiem) {
        List<Message> meesageList = new ArrayList<Message>();

        this.getDictionaryValidationService().isBusinessObjectValid(perDiem);
        MessageMap messageMap = GlobalVariables.getMessageMap();

        if(messageMap.hasErrors()){
            List<Message> message = this.translateErrorsFromErrorMap(messageMap);
            meesageList.addAll(message);

            messageMap.clearErrorMessages();
        }

        if(!isValidMealsAndIncidentals(perDiem)){
            Message message = MessageBuilder.buildMessageWithPlaceHolder(TemKeyConstants.ERROR_PER_DIEM_MEAL_INCIDENTAL_NON_POSITIVE_AMOUNT, perDiem.getPrimaryDestination().getRegion().getRegionName(), perDiem.getPrimaryDestination(), perDiem.getLineNumber(), perDiem.getMealsAndIncidentals());
            meesageList.add(message);
        }

        return meesageList;
    }

    protected boolean isValidMealsAndIncidentals(PerDiem perDiem){
        KualiDecimal mealsAndIncidentals = perDiem.getMealsAndIncidentals();

        return ObjectUtils.isNotNull(mealsAndIncidentals) && mealsAndIncidentals.isPositive();
    }

    /**
     * put the error message into the given message map
     */
    protected void populateMessageMap(MessageMap messageMap, List<Message> errorMessages) {
        for(Message message : errorMessages){
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, message.getMessage());
        }
    }

    /**
     * Builds actual error message from error key and parameters.
     */
    protected List<Message> translateErrorsFromErrorMap(MessageMap errorMap) {
        List<Message> errors = new ArrayList<Message>();

        for (String errorKey : errorMap.getPropertiesWithErrors()) {

            for (Object message : errorMap.getMessages(errorKey)) {
                ErrorMessage errorMessage = (ErrorMessage)message;

                Message messageWithPlaceHolder = MessageBuilder.buildMessageWithPlaceHolder(errorMessage.getErrorKey(), (Object[])errorMessage.getMessageParameters());

                errors.add(messageWithPlaceHolder);
            }
        }

        return errors;
    }

    /**
     * Gets the dictionaryValidationService attribute.
     * @return Returns the dictionaryValidationService.
     */
    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    /**
     * Sets the dictionaryValidationService attribute value.
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    /**
     * Gets the perDiemService attribute.
     * @return Returns the perDiemService.
     */
    public PerDiemService getPerDiemService() {
        return perDiemService;
    }

    /**
     * Sets the perDiemService attribute value.
     * @param perDiemService The perDiemService to set.
     */
    public void setPerDiemService(PerDiemService perDiemService) {
        this.perDiemService = perDiemService;
    }
}
