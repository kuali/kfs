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
