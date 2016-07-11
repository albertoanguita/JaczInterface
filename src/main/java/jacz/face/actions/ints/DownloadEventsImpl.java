package jacz.face.actions.ints;

import jacz.face.state.FilesStateProperties;
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

    private final FilesStateProperties filesStateProperties;

    public DownloadEventsImpl(TransferStatsProperties transferStatsProperties, FilesStateProperties filesStateProperties) {
        this.transferStatsProperties = transferStatsProperties;
        this.filesStateProperties = filesStateProperties;
    }

    @Override
    public void started(DownloadInfo downloadInfo, DownloadManager downloadManager) {
        System.out.println("Download started: " + downloadInfo);
        transferStatsProperties.addDownload(downloadInfo, downloadManager);
        filesStateProperties.startFileDownload(downloadInfo.fileHash, downloadManager);
    }

    @Override
    public void paused(DownloadInfo downloadInfo, DownloadManager downloadManager) {
        System.out.println("Download paused: " + downloadInfo);
        transferStatsProperties.updateDownloadState(downloadManager);
        filesStateProperties.pauseFileDownload(downloadInfo.fileHash);
    }

    @Override
    public void resumed(DownloadInfo downloadInfo, DownloadManager downloadManager) {
        System.out.println("Download resumed: " + downloadInfo);
        transferStatsProperties.updateDownloadState(downloadManager);
        filesStateProperties.resumeFileDownload(downloadInfo.fileHash);
    }

    @Override
    public void cancelled(DownloadInfo downloadInfo, DownloadManager downloadManager, DownloadProgressNotificationHandler.CancellationReason reason, Exception e) {
        System.out.println("Download cancelled: " + downloadInfo + ". Reason: " + reason);
        if (e != null) {
            e.printStackTrace();
        }
        transferStatsProperties.removeDownload(downloadManager);
        filesStateProperties.cancelFileDownload(downloadInfo.fileHash);
    }

    @Override
    public void stopped(DownloadInfo downloadInfo, DownloadManager downloadManager) {
        System.out.println("Download stopped: " + downloadInfo);
        transferStatsProperties.updateDownloadState(downloadManager);
        filesStateProperties.stopFileDownload(downloadInfo.fileHash);
    }

    @Override
    public void completed(DownloadInfo downloadInfo, String path, DownloadManager downloadManager) {
        System.out.println("Download completed: " + downloadInfo);
        transferStatsProperties.removeDownload(downloadManager);
    }
}
