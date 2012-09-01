/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;

/**
 * This class provides a set of utilities that can be used to build error message
 */
public class MessageBuilder {
    private static KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
    private static DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
    private static DataDictionary dataDictionary = dataDictionaryService.getDataDictionary();

    /**
     * add the given message into the given message list
     * 
     * @param messageList the given message list
     * @param message the given message
     */
    public static void addMessageIntoList(List<Message> messageList, Message message) {
        if (message != null) {
            messageList.add(message);
        }
    }

    /**
     * Build the error message with the message body and error type
     */
    public static Message buildMessage(String errorMessageKey, int errorType) {
        return MessageBuilder.buildMessage(errorMessageKey, null, errorType);
    }

    /**
     * Build the message for a fatal error with the message body and invalid value
     */
    public static Message buildMessage(String errorMessageKey, String invalidValue) {
        return buildMessage(errorMessageKey, invalidValue, Message.TYPE_FATAL);
    }

    /**
     * Build the error message with the message body, invalid value and error type
     */
    public static Message buildMessage(String errorMessageKey, String invalidValue, int errorType) {
        String errorMessageBody = getPropertyString(errorMessageKey);
        String errorMessage = formatMessageBody(errorMessageBody, invalidValue);
        return new Message(errorMessage, errorType);
    }

    /**
     * Format the error message body based on the given error message and invalid value
     */
    public static String formatMessageBody(String errorMessageBody, String invalidValue) {
        String value = StringUtils.isBlank(invalidValue) ? "" : "[" + invalidValue + "]";
        return errorMessageBody + value;
    }

    /**
     * Build the error message with the message body, invalid value and error type. The message body contains place holders.
     */
    public static Message buildMessageWithPlaceHolder1(String errorMessageKey, int errorType, Object... invalidValues) {
        String errorMessageBody = getPropertyString(errorMessageKey);
        String errorMessage = MessageFormat.format(errorMessageBody, invalidValues);
        return new Message(errorMessage, errorType);
    }

    /**
     * Build the message for a fatal error with the message body and invalid value. The message body contains place holders.
     */
    public static Message buildMessageWithPlaceHolder(String errorMessageKey, Object... invalidValues) {
        return buildMessageWithPlaceHolder(errorMessageKey, Message.TYPE_FATAL, invalidValues);
    }

    /**
     * get the message from application resource properties with the given key
     * 
     * @param messageKey the given message key
     * @return the message from application resource properties with the given key
     */
    public static String getPropertyString(String messageKey) {
        return kualiConfigurationService.getPropertyString(messageKey);
    }

    /**
     * build the error message with the given label and current value
     * 
     * @param label the given label
     * @param currentValue the given current value
     * @return the error message built from the given label and current value
     */
    public static String buildErrorMessageWithDataDictionary(Class<? extends BusinessObject> businessObjectClass, String attributeName, String currentValue) {
        String label = getShortLabel(businessObjectClass, attributeName);

        return label + ":" + currentValue;
    }

    /**
     * get the label of the specified attribute of the given business object
     * 
     * @param businessObjectClass the given business object
     * @param attributeName the specified attribute name
     * @return the label of the specified attribute of the given business object
     */
    public static String getShortLabel(Class<? extends BusinessObject> businessObjectClass, String attributeName) {
        return dataDictionary.getBusinessObjectEntry(businessObjectClass.getName()).getAttributeDefinition(attributeName).getShortLabel();
    }
}
