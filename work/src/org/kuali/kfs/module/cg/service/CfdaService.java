package org.kuali.module.cg.service;

import java.io.IOException;

import org.kuali.module.cg.bo.Cfda;

/**
 * 
 */
public interface CfdaService {
    CfdaUpdateResults update() throws IOException;
    public Cfda getByPrimaryId(String cfdaNumber);

}
