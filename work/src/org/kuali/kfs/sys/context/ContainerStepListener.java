/*
 * Copyright 2012 The Kuali Foundation.
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
