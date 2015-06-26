package org.kuali.kfs.sys.web;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@Path("docStats")
public class DocumentStatsApplication extends Application {
    protected Set<Object> singletons = new HashSet<>();
    public DocumentStatsApplication() {
        singletons.add(new DocumentStatsResource());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<>();
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
