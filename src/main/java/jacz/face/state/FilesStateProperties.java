package jacz.face.state;

import jacz.face.util.Util;
import jacz.peerengineservice.PeerId;
import jacz.peerengineservice.util.datatransfer.master.DownloadManager;
import javafx.beans.property.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides properties for watching the state of individual files, given the hash
 */
public class FilesStateProperties extends GenericStateProperties {

    public enum FileState {
        LOCAL,
        REMOTE,
        DOWNLOADING,
        PAUSED,
        STOPPED
    }

    public static class FileInfo {

        private final String hash;

        private final ObjectProperty<FileState> state;

        private final StringProperty path;

        private final ObjectProperty<Set<PeerId>> providers;

        private final ObjectProperty<DownloadManager> downloadManager;

        private final IntegerProperty downloadProgress;

        private final DoubleProperty speed;

        private FileInfo(String hash, FileState state, String path, Set<PeerId> providers, DownloadManager downloadManager, int progress, double speed) {
            this.hash = hash;
            this.state = new SimpleObjectProperty<>(state);
            this.path = new SimpleStringProperty(path);
            this.providers = new SimpleObjectProperty<>(providers);
            this.downloadManager = new SimpleObjectProperty<>(downloadManager);
            this.downloadProgress = new SimpleIntegerProperty(progress);
            this.speed = new SimpleDoubleProperty(speed);
        }

        private static FileInfo buildLocalFileInfo(String hash, String path, Set<PeerId> providers) {
            return new FileInfo(hash, FileState.LOCAL, path, providers, null, MAX_PROGRESS, 0d);
        }

        private static FileInfo buildRemoteFileInfo(String hash, Set<PeerId> providers) {
            return new FileInfo(hash, FileState.REMOTE, null, providers, null, 0, 0d);
        }

        private static FileInfo buildDownloadingFileInfo(String hash, Set<PeerId> providers, DownloadManager downloadManager, int progress, double speed) {
            return new FileInfo(hash, FileState.DOWNLOADING, null, providers, downloadManager, progress, speed);
        }

        private static FileInfo buildPausedFileInfo(String hash, Set<PeerId> providers, DownloadManager downloadManager, int progress) {
            return new FileInfo(hash, FileState.PAUSED, null, providers, downloadManager, progress, 0d);
        }

        private static FileInfo buildStoppedFileInfo(String hash, Set<PeerId> providers, int progress) {
            return new FileInfo(hash, FileState.DOWNLOADING, null, providers, null, progress, 0d);
        }

        private void addToLocalRepo(String path) {
            updateState(FileState.LOCAL);
            updatePath(path);
            updateDownloadManager(null);
        }

        private void removeFromLocalRepo() {
            updateState(FileState.REMOTE);
            updatePath(null);
        }

        private void startDownloading(DownloadManager downloadManager) {
            updateState(FileState.DOWNLOADING);
            updateDownloadManager(downloadManager);
        }

        private void pauseDownload() {
            updateState(FileState.PAUSED);
            updateSpeed();
        }

        private void resumeDownload() {
            updateState(FileState.DOWNLOADING);
            updateSpeed();
        }

        private void stopDownloading() {
            updateState(FileState.STOPPED);
            updateSpeed();
        }

        private void cancelDownloading() {
            updateState(FileState.REMOTE);
        }

        private void updateState(FileState fileState) {
            Util.setLater(state, fileState);
        }

        private void updatePath(String path) {
            Util.setLater(this.path, path);
        }

        private void updateProviders(Set<PeerId> providers) {
            Util.setLater(this.providers, providers);
        }

        private void updateDownloadManager(DownloadManager downloadManager) {
            Util.setLater(this.downloadManager, downloadManager);
        }

        private void updateDownloadProgress() {
            Util.setLater(this.downloadProgress, Util.calculatePerTenThousand(downloadManager.getValue().getStatistics().getDownloadedSizeThisResource(), downloadManager.getValue().getLength()));
        }

        private void updateSpeed() {
            Util.setLater(this.speed, downloadManager.getValue().getStatistics().getSpeed());
        }

        public String getHash() {
            return hash;
        }

        public FileState getState() {
            return state.get();
        }

        public ObjectProperty<FileState> stateProperty() {
            return state;
        }

        public String getPath() {
            return path.get();
        }

        public StringProperty pathProperty() {
            return path;
        }

        public Set<PeerId> getProviders() {
            return providers.get();
        }

        public ObjectProperty<Set<PeerId>> providersProperty() {
            return providers;
        }

        public DownloadManager getDownloadManager() {
            return downloadManager.get();
        }

        public ObjectProperty<DownloadManager> downloadManagerProperty() {
            return downloadManager;
        }

        public int getDownloadProgress() {
            return downloadProgress.get();
        }

        public IntegerProperty downloadProgressProperty() {
            return downloadProgress;
        }

        public double getSpeed() {
            return speed.get();
        }

        public DoubleProperty speedProperty() {
            return speed;
        }
    }

    private static final int MAX_PROGRESS = 10000;

    private final Map<String, FileInfo> observedFiles;

    public FilesStateProperties() {
        observedFiles = new HashMap<>();
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    public synchronized FileInfo getFileInfo(String hash) {
        if (!observedFiles.containsKey(hash)) {
            observedFiles.put(hash, fetchFileInfo(hash));
        }
        return observedFiles.get(hash);
    }

    public synchronized void addToLocalRepo(String hash, String path) {
        System.out.println("add to local");
        if (observedFiles.containsKey(hash)) {
            FileInfo fileInfo = getFileInfo(hash);
            fileInfo.addToLocalRepo(path);
        }
    }

    public synchronized void removeFromLocalRepo(String hash) {
        System.out.println("remove from local");
        if (observedFiles.containsKey(hash)) {
            FileInfo fileInfo = getFileInfo(hash);
            fileInfo.removeFromLocalRepo();
        }
    }

    public synchronized void startFileDownload(String hash, DownloadManager downloadManager) {
        if (observedFiles.containsKey(hash)) {
            FileInfo fileInfo = getFileInfo(hash);
            fileInfo.startDownloading(downloadManager);
        }
    }

    public synchronized void updateFileDownload(String hash) {
        if (observedFiles.containsKey(hash)) {
            FileInfo fileInfo = getFileInfo(hash);
            fileInfo.updateDownloadProgress();
            fileInfo.updateSpeed();
        }
    }

    public synchronized void pauseFileDownload(String hash) {
        if (observedFiles.containsKey(hash)) {
            FileInfo fileInfo = getFileInfo(hash);
            fileInfo.pauseDownload();
        }
    }

    public synchronized void resumeFileDownload(String hash) {
        if (observedFiles.containsKey(hash)) {
            FileInfo fileInfo = getFileInfo(hash);
            fileInfo.resumeDownload();
        }
    }

    public synchronized void stopFileDownload(String hash) {
        if (observedFiles.containsKey(hash)) {
            FileInfo fileInfo = getFileInfo(hash);
            fileInfo.stopDownloading();
        }
    }

    public synchronized void cancelFileDownload(String hash) {
        if (observedFiles.containsKey(hash)) {
            FileInfo fileInfo = getFileInfo(hash);
            fileInfo.cancelDownloading();
        }
    }

    public synchronized void updateProviders(String hash, Set<PeerId> providers) {
        if (observedFiles.containsKey(hash)) {
            FileInfo fileInfo = getFileInfo(hash);
            fileInfo.updateProviders(providers);
        }
    }

    /**
     * A file is no longer being monitored by a view -> remove it from the set of monitored apps
     *
     * @param hash hash of the removed app
     */
    public synchronized void forgetFile(String hash) {
        // todo use
        observedFiles.remove(hash);
    }

    private FileInfo fetchFileInfo(String hash) {
        Set<PeerId> providers = client.getFileProviders(hash);
        // check in the local file hash database first
        if (client.containsFileByHash(hash)) {
            // available in the local file hash database
            String path = client.getFile(hash);
            return FileInfo.buildLocalFileInfo(hash, path, providers);
        } else {
            // the file is remote (might be downloading)
            DownloadManager downloadManager = client.getFileAPI().getHashDownloadManager(hash);
            if (downloadManager != null) {
                // the file is being downloaded now
                return FileInfo.buildDownloadingFileInfo(hash, providers, downloadManager, Util.calculatePerTenThousand(downloadManager.getStatistics().getDownloadedSizeThisResource(), downloadManager.getLength()), downloadManager.getStatistics().getSpeed());
            } else {
                // the file is remote
                return FileInfo.buildRemoteFileInfo(hash, providers);
            }
        }
    }
}
