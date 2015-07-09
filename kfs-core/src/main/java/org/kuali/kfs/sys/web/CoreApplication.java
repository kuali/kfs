package org.kuali.kfs.sys.web;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.DataDictionaryService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/core")
public class CoreApplication extends Application {
    private Set<Object> resources = new HashSet<>();
    private Set<Class<?>> nonResources = new HashSet<>();

    public CoreApplication() {
        resources.add(new DataDictionaryResource(SpringContext.getBean(DataDictionaryService.class)));
    }

    @Override
    public Set<Object> getSingletons() {
        return resources;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return nonResources;
    }
}
