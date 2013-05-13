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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class should be used in the development environment to run the batch container, execute one or more steps, then shut down the batch container.
 */
public class BatchStepLauncher implements Runnable {
    private static final String LOG_PREFIX = BatchStepLauncher.class.getName() +": ";
    private static final String batchContainerStep = "batchContainerStep";

    private static BatchStepTriggerParameters batchStepTriggerParms;

    /**
     * This class validates the arguments, adds a shutdown hook to Runtime to clean up BatchContainer's semaphore, starts the batch container,
     * and executes steps using the BatchStepTrigger.
     *
     * The BatchStepTrigger will exit the system using calls to System.exit(). This will cause this Launcher class to exit immediately without executing any further
     * methods. Therefore the batch container cannot be shut down using the iu.stopBatchContainerStep which nicely cleans up its own semaphore. The shutdown hook has been
     * added in order for this cleanup to occur.
     *
     * @param args the String[] arguments normally passed to BatchStepTrigger
     */
    public static void main(String[] args) {

        //check arguments for the trigger
        checkArguments(args);

        //run the Batch Container in its own thread
        startBatchContainer();

        //confirm that the container started up before executing the steps
        //-uses batch container directory specified in args
        confirmStartUp();

        //execute one or more steps
        executeSteps(args);

        //the batch container will be shut down by the thread hook added to the Runtime in setUp()
    }

    /**
     * Run the Batch Container in its own thread
     */
    @Override
    public void run() {
        String[] args = new String[2];
        args[0] = batchContainerStep;
        args[1] = (batchStepTriggerParms != null ? batchStepTriggerParms.getJobName(): "unknown");

        BatchStepRunner.main(args);
    }

    /**
     * Uses the BatchStepTriggerParameters class to validate the arguments passed to the launcher.
     *
     * @param args String[] of arguments that would normally passed to BatchStepTrigger
     */
    private static void checkArguments(String[] args) {
        logToOut("checking arguments");

        batchStepTriggerParms = new BatchStepTriggerParameters(args);

        logToOut("received valid arguments");
    }

    /**
     * Start the batch container in its own Thread
     */
    private static void startBatchContainer() {
        logToOut("starting the batch container");
        Executor executor = Executors.newCachedThreadPool();

        BatchStepLauncher batchStepLauncher = new BatchStepLauncher();
        executor.execute(batchStepLauncher);
    }

    /**
     * Loops and polls for the batch container start up (looks for the .runlock)
     */
    private static void confirmStartUp() {
        logToOut("confirming batch container start up");

        while (!batchStepTriggerParms.getBatchContainerDirectory().isBatchContainerRunning()) {
            logToOut("waiting for the batch container to start up");
            try {
                Thread.sleep(batchStepTriggerParms.getSleepInterval());
            }
            catch (InterruptedException e) {
                throw new RuntimeException("BatchStepLauncher encountered interrupt exception while trying to wait for the batch container to start up", e);
            }
        }

        logToOut("batch container is running");
    }

    /**
     * Calls BatchStepTrigger to execute steps
     *
     * @param args String[] arguments normally passed to the BatchStepTrigger
     */
    private static void executeSteps(String[] args) {
        logToOut("executing step(s)");

        BatchStepTrigger.main(args);

        logToOut("finished executing step(s)");
    }

    /**
     * Removes the batch container semaphore (used by the shutdown hook added to Runtime)
     * This is necessary because the Launcher exits immediately when the Trigger exits. Shutdown hook cleans up the semaphore.
     */
    private static void removeBatchContainerSemaphore() {
        logToOut("removing batch container semaphore file");

        if (batchStepTriggerParms.getBatchContainerDirectory().isBatchContainerRunning()) {
            batchStepTriggerParms.getBatchContainerDirectory().removeBatchContainerSemaphore();
        }

        logToOut("batch container semaphore file has been removed");
    }

    /**
     * Logs statement to System.out with a prefix.
     *
     * @param statement the statement to log
     */
    private static void logToOut(String statement) {
        System.out.println(LOG_PREFIX + statement);
    }
}
