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
package org.kuali.kfs.sys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kuali.kfs.sys.fixture.UserNameFixture;

/**
 * Use this annotation to configure the appropriate context for the methods in a test that extends KualiTestBase.
 * 
 * @see ShouldCommitTransactions
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.TYPE })
public @interface ConfigureContext {
    /**
     * This method returns the UserNameFixture that should be used to create the appropriate UserSession.
     * 
     * @return sessionUserNameFixture
     */
    UserNameFixture session() default UserNameFixture.NO_SESSION;

    /**
     * This method indicates whether the test needs the batch schedule initialize. The scheduler will always be started. And, if
     * your test is simply grabbing Steps from Spring and executing manually, you do not need the quartz schedule loaded.
     * 
     * @return initializeBatchSchedule
     */
    boolean initializeBatchSchedule() default false;

    /**
     * This method indicates whether the database changes that occur during test code execution should be committed or rolled back.
     * 
     * @return shouldCommitTransactions
     */
    boolean shouldCommitTransactions() default false;
}
