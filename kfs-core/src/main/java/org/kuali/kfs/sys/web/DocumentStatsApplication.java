package org.kuali.kfs.sys.web;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("docStats")
public class DocumentStatsApplication extends Application {
    protected Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> empty = new HashSet<>();

    public DocumentStatsApplication() {
        singletons.add(new DocumentStatsResource());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return empty;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
