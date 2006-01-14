package org.kuali.module.gl.batch;

import org.kuali.core.batch.Step;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ScrubberService;

/**
 * @author aapotts
 *
 */
public class ScrubberStep implements Step {
    private ScrubberService scrubberService;
    private OriginEntryService originEntryService;

    public String getName() {
        return "GL ScrubberStep";
    }

    public boolean performStep() {
        scrubberService.scrubEntries();
        return true;
    }

    public void setOriginEntryService(OriginEntryService oes) {
        originEntryService = oes;
    }

    public void setScrubberService(ScrubberService ss) {
        scrubberService = ss;
    }
}
