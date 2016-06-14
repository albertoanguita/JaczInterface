package jacz.face.state;

import jacz.database.DatabaseMediator;
import jacz.face.util.Util;
import jacz.peerengineclient.DownloadInfo;
import jacz.peerengineclient.PeerEngineClient;
import jacz.peerengineservice.util.datatransfer.TransferStatistics;
import jacz.peerengineservice.util.datatransfer.master.DownloadManager;
import jacz.peerengineservice.util.datatransfer.master.DownloadState;
import jacz.util.concurrency.timer.Timer;
import jacz.util.concurrency.timer.TimerAction;
import jacz.util.numeric.NumericUtil;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.util.Collection;
import java.util.Date;

/**
 * Stores the latest history of recorded statistics
 * <p>
 * todo add speeds history
 * <p>
 * todo add assigned part, shared part, downloaded part... (like e-mule)
 * todo add download providers detail (peerId, time providing, speed...)
 */
public class TransferStatsProperties extends GenericStateProperties implements TimerAction {

    public static class TransferPropertyInfo {

        protected final String transferId;

        protected final String fileHash;

        protected final String fileName;

        protected final Date creationDate;

        protected final LongProperty transferredSize;

        protected final DoubleProperty speed;

        public TransferPropertyInfo(String transferId) {
            this(transferId, null, null, null, 0L);
        }

        public TransferPropertyInfo(String transferId, String fileHash, String fileName, Date creationDate, long transferredSize) {
            this.transferId = transferId;
            this.fileHash = fileHash;
            this.fileName = fileName;
            this.creationDate = creationDate;
            this.transferredSize = new SimpleLongProperty(transferredSize);
            this.speed = new SimpleDoubleProperty(0d);
        }

        public String getTransferId() {
            return transferId;
        }

        public String getFileHash() {
            return fileHash;
        }

        public String getFileName() {
            return fileName;
        }

        public Date getCreationDate() {
            return creationDate;
        }

        public long getTransferredSize() {
            return transferredSize.get();
        }

        public LongProperty transferredSizeProperty() {
            return transferredSize;
        }

        public double getSpeed() {
            return speed.get();
        }

        public DoubleProperty speedProperty() {
            return speed;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TransferPropertyInfo)) return false;

            TransferPropertyInfo that = (TransferPropertyInfo) o;

            return transferId.equals(that.transferId);
        }

        @Override
        public int hashCode() {
            return transferId.hashCode();
        }
    }

    public static class DownloadPropertyInfo extends TransferPropertyInfo {

        private final DownloadManager downloadManager;

        private final String containerTitle;

        private final DatabaseMediator.ItemType containerType;

        private final Integer containerId;

        private final Integer superContainerId;

        private final Integer itemId;

        ///////////////////

        private final ObjectProperty<DownloadState> downloadState;

        private final ObjectProperty<Long> fileSize;

        private final ObjectProperty<Integer> perTenThousandDownloaded;

        private final FloatProperty priority;

        private final DoubleProperty streamingNeed;

        private final IntegerProperty providersCount;

        public DownloadPropertyInfo(String transferId) {
            super(transferId);
            downloadManager = null;
            containerTitle = null;
            containerType = null;
            containerId = null;
            superContainerId = null;
            itemId = null;
            downloadState = null;
            fileSize = null;
            perTenThousandDownloaded = null;
            priority = null;
            streamingNeed = null;
            providersCount = null;
        }

        public DownloadPropertyInfo(DownloadInfo downloadInfo, DownloadManager downloadManager, PeerEngineClient client) {
            super(downloadManager.getId(), downloadInfo.fileHash, downloadInfo.fileName, downloadManager.getStatistics().getCreationDate(), downloadManager.getStatistics().getDownloadedSizeThisResource());
            this.downloadManager = downloadManager;
            containerTitle = buildTitle(downloadInfo.title, downloadInfo.containerType);
            containerType = downloadInfo.containerType;
            containerId = downloadInfo.containerId;
            superContainerId = downloadInfo.superContainerId;
            itemId = downloadInfo.itemId;
            downloadState = new SimpleObjectProperty<>(downloadManager.getState());
            fileSize = new SimpleObjectProperty<>(downloadManager.getLength());
            perTenThousandDownloaded = new SimpleObjectProperty<>(calculatePerTenThousand(downloadManager.getStatistics().getDownloadedSizeThisResource(), downloadManager.getLength()));
            priority = new SimpleFloatProperty(downloadManager.getPriority());
            streamingNeed = new SimpleDoubleProperty(downloadManager.getStreamingNeed());
            providersCount = new SimpleIntegerProperty(downloadManager.getStatistics().getProviders().size());
        }

        private String buildTitle(DownloadInfo.Title title, DatabaseMediator.ItemType containerType) {
            return title.title;
//            if (containerType == DatabaseMediator.ItemType.MOVIE) {
//                return title.title;
//            } else {
//                return title.chapterTitle;
//            }
        }

        private Integer calculatePerTenThousand(long downloadedSize, Long fileSize) {
            return fileSize != null ? (int) NumericUtil.displaceInRange(downloadedSize, 0, fileSize, 0, 10000) : null;
        }

        public DownloadManager getDownloadManager() {
            return downloadManager;
        }

        public String getContainerTitle() {
            return containerTitle;
        }

        public DatabaseMediator.ItemType getContainerType() {
            return containerType;
        }

        public Integer getContainerId() {
            return containerId;
        }

        public Integer getSuperContainerId() {
            return superContainerId;
        }

        public Integer getItemId() {
            return itemId;
        }

        public ObjectProperty<DownloadState> downloadStateProperty() {
            return downloadState;
        }

        public Long getFileSize() {
            return fileSize.get();
        }

        public ObjectProperty<Long> fileSizeProperty() {
            return fileSize;
        }

        public Integer getPerTenThousandDownloaded() {
            return perTenThousandDownloaded.get();
        }

        public ObjectProperty<Integer> perTenThousandDownloadedProperty() {
            return perTenThousandDownloaded;
        }

        public FloatProperty priorityProperty() {
            return priority;
        }

        public DoubleProperty streamingNeedProperty() {
            return streamingNeed;
        }

        public IntegerProperty providersCountProperty() {
            return providersCount;
        }
    }

    public static class UploadPropertyInfo extends TransferPropertyInfo {

        public UploadPropertyInfo(String transferId) {
            super(transferId);
        }
    }

    private TransferStatistics transferStatistics;

    private Timer checkSpeedTimer;

    private final LongProperty totalUploadedBytes;

    private final LongProperty totalDownloadedBytes;

    private final DoubleProperty currentUploadSpeed;

    private final DoubleProperty currentDownloadSpeed;

    private final ObservableList<DownloadPropertyInfo> observedDownloads;

    private final ObservableList<UploadPropertyInfo> observedUploads;


    public TransferStatsProperties() {
        checkSpeedTimer = null;
        totalUploadedBytes = new SimpleLongProperty(0L);
        totalDownloadedBytes = new SimpleLongProperty(0L);
        currentUploadSpeed = new SimpleDoubleProperty(0d);
        currentDownloadSpeed = new SimpleDoubleProperty(0d);

        observedDownloads = FXCollections.observableArrayList(new Callback<DownloadPropertyInfo, Observable[]>() {
            @Override
            public Observable[] call(DownloadPropertyInfo d) {
                return new Observable[]{
                        d.transferredSize,
                        d.speed,
                        d.downloadStateProperty(),
                        d.fileSizeProperty(),
                        d.priorityProperty(),
                        d.streamingNeedProperty(),
                        d.providersCountProperty()};
            }
        });

        observedUploads = FXCollections.observableArrayList(new Callback<UploadPropertyInfo, Observable[]>() {
            @Override
            public Observable[] call(UploadPropertyInfo d) {
                return new Observable[]{
                        d.transferredSize,
                        d.speed};
            }
        });
    }

    @Override
    public void setClient(PeerEngineClient client) {
        super.setClient(client);
        this.transferStatistics = client.getTransferStatistics();
        checkSpeedTimer = new Timer(TransferStatistics.SPEED_MONITOR_FREQUENCY, this);
        updateProperties();
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

    public ObservableList<DownloadPropertyInfo> getObservedDownloads() {
        return observedDownloads;
    }

    public ObservableList<UploadPropertyInfo> getObservedUploads() {
        return observedUploads;
    }

    @Override
    public Long wakeUp(Timer timer) {
        // update properties values
        updateProperties();
        return null;
    }

    private void updateProperties() {
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
    }

    public synchronized void addInitialStoppedDownloads(Collection<DownloadManager> initialStoppedDownloads) {
        for (DownloadManager downloadManager : initialStoppedDownloads) {
            addDownload(DownloadInfo.buildDownloadInfo(downloadManager.getResourceWriter().getUserDictionary()), downloadManager);
        }
    }

    public synchronized void addDownload(DownloadInfo downloadInfo, DownloadManager downloadManager) {
        if (downloadInfo.type != DownloadInfo.Type.IMAGE) {
            System.out.println("Adding download to observed downloads...");
            DownloadPropertyInfo downloadPropertyInfo = new DownloadPropertyInfo(downloadInfo, downloadManager, client);
            observedDownloads.add(downloadPropertyInfo);
            System.out.println("Download added to observed downloads");
        }
    }

    public synchronized void updateDownloadState(DownloadManager downloadManager) {
        int index = getDownloadPropertyInfoIndex(downloadManager.getId());
        if (index >= 0) {
            DownloadPropertyInfo downloadPropertyInfo = observedDownloads.get(index);
            Util.setLater(downloadPropertyInfo.transferredSizeProperty(), downloadManager.getStatistics().getDownloadedSizeThisResource());
            Util.setLater(downloadPropertyInfo.speedProperty(), downloadManager.getStatistics().getSpeed());
            Util.setLater(downloadPropertyInfo.downloadStateProperty(), downloadManager.getState());
            Util.setLater(downloadPropertyInfo.fileSizeProperty(), downloadManager.getLength());
            Util.setLater(downloadPropertyInfo.priorityProperty(), downloadManager.getPriority());
            Util.setLater(downloadPropertyInfo.streamingNeedProperty(), downloadManager.getStreamingNeed());
            Util.setLater(downloadPropertyInfo.providersCountProperty(), downloadManager.getStatistics().getProviders().size());
        }
    }

    public synchronized void removeDownload(DownloadManager downloadManager) {
        int index = getDownloadPropertyInfoIndex(downloadManager.getId());
        if (index >= 0) {
            observedDownloads.remove(index);
        }
    }

    private int getDownloadPropertyInfoIndex(String transferId) throws IndexOutOfBoundsException {
        DownloadPropertyInfo downloadPropertyInfo = new DownloadPropertyInfo(transferId);
        return observedDownloads.indexOf(downloadPropertyInfo);
    }

    public void stop() {
        if (checkSpeedTimer != null) {
            checkSpeedTimer.kill();
        }
    }
}
