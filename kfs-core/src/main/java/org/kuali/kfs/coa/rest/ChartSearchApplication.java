package org.kuali.kfs.coa.rest;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("lookup")
public class ChartSearchApplication extends Application {
    private Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> empty = new HashSet<>();

    public ChartSearchApplication() {

        singletons.add(new ChartSearchResource());
        singletons.add(new ObjectCodeSearchResource(SpringContext.getBean(UniversityDateService.class)));
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
