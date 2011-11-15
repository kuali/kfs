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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

import org.apache.log4j.Logger;

public class MemoryMonitor {
    private final Collection<Listener> listeners = new ArrayList<Listener>();
    private static final Logger LOG = Logger.getLogger(MemoryMonitor.class);
    private String springContextId;

    public interface Listener {
        public void memoryUsageLow(String springContextId, Map<String, String> memoryUsageStatistics, String deadlockedThreadIds);
    }
    NotificationListener lowMemoryListener;
    public MemoryMonitor() {
        LOG.info("initializing");
        this.springContextId = "Unknown";
        ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
        ManagementFactory.getThreadMXBean().setThreadCpuTimeEnabled(true);
        lowMemoryListener = new NotificationListener() {
            public void handleNotification(Notification n, Object hb) {
                if (n.getType().equals(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED)) {
                    Map<String, String> memoryUsageStatistics = new HashMap<String, String>();
                    memoryUsageStatistics.put("MemoryMXBean: " + MemoryType.HEAP, ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().toString());
                    memoryUsageStatistics.put("MemoryMXBean:" + MemoryType.NON_HEAP, ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().toString());
                    for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
                        memoryUsageStatistics.put("MemoryPoolMXBean: " + pool.getType(), pool.getUsage().toString());
                    }
                    for (Listener listener : listeners) {
                        listener.memoryUsageLow(springContextId, memoryUsageStatistics, Arrays.toString(ManagementFactory.getThreadMXBean().findMonitorDeadlockedThreads()));
                    }
                }
            }
        };
        ((NotificationEmitter) ManagementFactory.getMemoryMXBean()).addNotificationListener(lowMemoryListener, null, null);
    }
    
    public void stop() {
        try {
            removeAllListeners();
            ((NotificationEmitter) ManagementFactory.getMemoryMXBean()).removeNotificationListener(lowMemoryListener);
        } catch (ListenerNotFoundException ex) {
            LOG.error( "Unable to unregister mbean listener", ex);
        }
    }
    
    public void removeAllListeners() {
        listeners.clear();
    }

    public MemoryMonitor(String springContextId) {
        this();
        this.springContextId = springContextId;
    }

    public boolean addListener(Listener listener) {
        return listeners.add(listener);
    }

    public boolean removeListener(Listener listener) {
        return listeners.remove(listener);
    }

    public static void setPercentageUsageThreshold(double percentage) {
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() == MemoryType.HEAP && pool.isUsageThresholdSupported()) {
                if (percentage <= 0.0 || percentage > 1.0) {
                    throw new IllegalArgumentException("percentage not in range");
                }
                long warningThreshold = (long) (pool.getUsage().getMax() * percentage);
                pool.setUsageThreshold(warningThreshold);
            }
        }
    }
}
