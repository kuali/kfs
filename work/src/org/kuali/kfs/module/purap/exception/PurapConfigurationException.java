/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.exception;

/**
 * Represents an exception that is thrown when there is a configuration problem specific
 * to Purchasing Accounts Payable module.
 */
public class PurapConfigurationException extends RuntimeException {
    public PurapConfigurationException() {
        super();
    }

    public PurapConfigurationException(String msg) {
        super(msg);
    }
}
