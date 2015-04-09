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
package org.kuali.kfs.sys.context;

/**
 * Any class adding this listener class can broadcast when a step starts and finishes.
 * Any class implementing this listener can be notified when a step starts and finishes.
 */
public interface ContainerStepListener {

    /**
     * Notify the listener that the Step has started.
     *
     * @param runFile The Step's .run file descriptor
     * @param logFile The Step's log file created by its executor. The logFile is used to provide a unique identifier for this run of the Step
     */
    public void stepStarted(BatchStepFileDescriptor runFile, String logFile);

    /**
     * Notify the listener that the Step has finished.
     *
     * @param resultFile the Step's .success or .error file descriptor
     * @param logFile The Step's log file created by its executor when the step started. The logFile is used to provide a unique identifier for this run of the Step
     */
    public void stepFinished(BatchStepFileDescriptor resultFile, String logFile);
}
