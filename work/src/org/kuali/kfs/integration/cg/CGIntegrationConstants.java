/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.integration.cg;

public class CGIntegrationConstants {

    // Award Invoicing Option
    public static class AwardInvoicingOption {
        public enum Types {
            AWARD("1", "Invoice by Award"), ACCOUNT("2", "Invoice by Account"), CONTRACT_CONTROL("3", "Invoice by Contract Control Account");
            private String code;
            private String name;
            Types(String code, String name) {
                this.code = code;
                this.name = name;
            }
            public String getCode() {
                return code;
            }
            public String getName() {
                return name;
            }
            public static String get(String code) {
                for(Types type : Types.values()) {
                    if(type.getCode().equals(code)){
                        return type.getName();
                    }
                }
                return null;
            }
        }
    }
}
