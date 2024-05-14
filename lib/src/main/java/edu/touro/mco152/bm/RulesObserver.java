package edu.touro.mco152.bm;

import edu.touro.mco152.bm.externalsys.SlackManager;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * The RulesObserver class implements the Observer interface and defines behavior for observing disk run events
 * and enforcing specific rules.
 */
public class RulesObserver implements Observer {

    SlackManager slackmgr = new SlackManager("BadBm");

    @Override
    public void update(DiskRun run) {
        if (run.getIoMode() == DiskRun.IOMode.READ && run.getRunMax() > 1.03 * run.getRunAvg())
            slackmgr.postMsg2OurChannel("Read max exceeded 3% above average.");
    }
}
