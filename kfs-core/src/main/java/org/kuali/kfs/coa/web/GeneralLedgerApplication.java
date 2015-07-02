package org.kuali.kfs.coa.web;

import com.sun.jersey.api.container.filter.LoggingFilter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/generalLedger")
public class GeneralLedgerApplication extends Application {
    protected Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> clazzes = new HashSet<>();

    public GeneralLedgerApplication() {
        singletons.add(new GeneralLedgerResource());
        clazzes.add(LoggingFilter.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return clazzes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
