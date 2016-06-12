package jacz.face.actions.ints;

import jacz.face.state.TransferStatsProperties;
import jacz.peerengineclient.DownloadEvents;
import jacz.peerengineclient.DownloadInfo;
import jacz.peerengineservice.util.datatransfer.DownloadProgressNotificationHandler;
import jacz.peerengineservice.util.datatransfer.master.DownloadManager;

/**
 *
 */
public class DownloadEventsImpl implements DownloadEvents {

    private final TransferStatsProperties transferStatsProperties;

    public DownloadEventsImpl(TransferStatsProperties transferStatsProperties) {
        this.transferStatsProperties = transferStatsProperties;
    }

    @Override
    public void started(DownloadInfo downloadInfo, DownloadManager downloadManager) {
        System.out.println("Download started: " + downloadInfo);
        transferStatsProperties.addDownload(downloadInfo, downloadManager);
    }

    @Override
    public void paused(DownloadInfo downloadInfo, DownloadManager downloadManager) {
        System.out.println("Download paused: " + downloadInfo);
        transferStatsProperties.updateDownloadState(downloadManager);
    }

    @Override
    public void resumed(DownloadInfo downloadInfo, DownloadManager downloadManager) {
        System.out.println("Download resumed: " + downloadInfo);
        transferStatsProperties.updateDownloadState(downloadManager);
    }

    @Override
    public void cancelled(DownloadInfo downloadInfo, DownloadManager downloadManager, DownloadProgressNotificationHandler.CancellationReason reason) {
        System.out.println("Download cancelled: " + downloadInfo);
        transferStatsProperties.removeDownload(downloadManager);
    }

    @Override
    public void stopped(DownloadInfo downloadInfo, DownloadManager downloadManager) {
        System.out.println("Download stopped: " + downloadInfo);
        transferStatsProperties.removeDownload(downloadManager);
    }

    @Override
    public void completed(DownloadInfo downloadInfo, String path, DownloadManager downloadManager) {
        System.out.println("Download completed: " + downloadInfo);
        transferStatsProperties.removeDownload(downloadManager);
    }
}
