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
