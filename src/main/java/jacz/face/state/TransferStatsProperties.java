package jacz.face.state;

import jacz.face.util.Util;
import jacz.peerengineservice.util.datatransfer.TransferStatistics;
import jacz.util.concurrency.timer.Timer;
import jacz.util.concurrency.timer.TimerAction;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * Stores the latest history of recorded statistics
 *
 * todo add speeds history
 */
public class TransferStatsProperties implements TimerAction {

    private final TransferStatistics transferStatistics;

    private final Timer checkSpeedTimer;

    private final LongProperty totalUploadedBytes;

    private final LongProperty totalDownloadedBytes;

    private final DoubleProperty currentUploadSpeed;

    private final DoubleProperty currentDownloadSpeed;


    public TransferStatsProperties(TransferStatistics transferStatistics) {
        this.transferStatistics = transferStatistics;
        // todo checkSpeedTimer = new Timer(this, TransferStatistics. / 2);
        checkSpeedTimer = new Timer(1000, this);
        totalUploadedBytes = new SimpleLongProperty(transferStatistics.getUploadedBytes());
        totalDownloadedBytes = new SimpleLongProperty(transferStatistics.getDownloadedBytes());
        currentUploadSpeed = new SimpleDoubleProperty(0d);
        currentDownloadSpeed = new SimpleDoubleProperty(0d);
    }

    public LongProperty totalUploadedBytesProperty() {
        return totalUploadedBytes;
    }

    public LongProperty totalDownloadedBytesProperty() {
        return totalDownloadedBytes;
    }

    public DoubleProperty currentUploadSpeedProperty() {
        return currentUploadSpeed;
    }

    public DoubleProperty currentDownloadSpeedProperty() {
        return currentDownloadSpeed;
    }

    @Override
    public Long wakeUp(Timer timer) {
        // update properties values
        Util.setLater(totalUploadedBytesProperty(), transferStatistics.getUploadedBytes());
        Util.setLater(totalDownloadedBytesProperty(), transferStatistics.getDownloadedBytes());
        Double[] uploadSpeedRegistry = transferStatistics.getUploadSpeedRegistry();
        Double[] downloadSpeedRegistry = transferStatistics.getDownloadSpeedRegistry();
        if (uploadSpeedRegistry.length > 0) {
            Util.setLater(currentUploadSpeedProperty(), uploadSpeedRegistry[0]);
        }
        if (downloadSpeedRegistry.length > 0) {
            Util.setLater(currentDownloadSpeedProperty(), downloadSpeedRegistry[0]);
        }
        return null;
    }

    public void stop() {
        checkSpeedTimer.kill();
    }
}
