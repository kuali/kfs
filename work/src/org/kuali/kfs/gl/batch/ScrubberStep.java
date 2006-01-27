package org.kuali.module.gl.batch;

import org.kuali.core.batch.Step;
import org.kuali.module.gl.service.ScrubberService;

/**
 * @author aapotts
 *
 */
public class ScrubberStep implements Step {
    private ScrubberService scrubberService;

    public String getName() {
        return "GL ScrubberStep";
    }

    public boolean performStep() {
        scrubberService.scrubEntries();
        return true;
    }

    public void setScrubberService(ScrubberService ss) {
        scrubberService = ss;
    }
}
