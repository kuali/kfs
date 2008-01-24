/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.util;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.context.SpringContext;

/**
 * This class provides a set of utilities that can be used to build error message
 */
public class MessageBuilder {
    private static KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);

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
    public static Message buildMessageWithPlaceHolder(String errorMessageKey, int errorType, Object... invalidValues) {
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
}
