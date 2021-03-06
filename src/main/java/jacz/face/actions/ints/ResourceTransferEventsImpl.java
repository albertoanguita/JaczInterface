package jacz.face.actions.ints;

import jacz.face.state.FilesStateProperties;
import jacz.face.state.TransferStatsProperties;
import jacz.peerengineservice.PeerId;
import jacz.peerengineservice.util.datatransfer.*;
import jacz.peerengineservice.util.datatransfer.master.DownloadManager;
import jacz.peerengineservice.util.datatransfer.slave.UploadManager;

import java.util.Set;

/**
 * Created by Alberto on 28/04/2016.
 */
public class ResourceTransferEventsImpl implements ResourceTransferEvents {

    private final TransferStatsProperties transferStatsProperties;

    private final FilesStateProperties filesStateProperties;

    public ResourceTransferEventsImpl(TransferStatsProperties transferStatsProperties, FilesStateProperties filesStateProperties) {
        this.transferStatsProperties = transferStatsProperties;
        this.filesStateProperties = filesStateProperties;
    }

    @Override
    public void addLocalResourceStore(String name) {

    }

    @Override
    public void setLocalGeneralResourceStore() {

    }

    @Override
    public void addForeignResourceStore(String name) {

    }

    @Override
    public void removeLocalResourceStore(String name) {

    }

    @Override
    public void removeLocalGeneralResourceStore() {

    }

    @Override
    public void removeForeignResourceStore(String name) {

    }

    @Override
    public void updateResourceProviders(String resourceId, Set<PeerId> providers) {
        filesStateProperties.updateProviders(resourceId, providers);
    }

    @Override
    public void globalDownloadInitiated(String resourceStoreName, String resourceID, double streamingNeed, String totalHash, String totalHashAlgorithm) {

    }

    @Override
    public void peerDownloadInitiated(PeerId serverPeerId, String resourceStoreName, String resourceID, double streamingNeed, String totalHash, String totalHashAlgorithm) {

    }

    @Override
    public void setMaxDesiredDownloadSpeed(Float totalMaxDesiredSpeed) {

    }

    @Override
    public void setMaxDesiredUploadSpeed(Float totalMaxDesiredSpeed) {

    }

    @Override
    public void setAccuracy(double accuracy) {

    }

    @Override
    public void approveResourceRequest(ResourceRequest request, ResourceStoreResponse response) {

    }

    @Override
    public void denyUnavailableSubchannelResourceRequest(ResourceRequest request, ResourceStoreResponse response) {

    }

    @Override
    public void deniedResourceRequest(ResourceRequest request, ResourceStoreResponse response) {

    }

    @Override
    public void periodicDownloadsNotification(DownloadsManager downloadsManager) {
        for (DownloadManager downloadManager : downloadsManager.getAllDownloads()) {
//            Double speed = downloadManager.getStatistics().getSpeed();
//            speed /= 1024d;
//            long size = downloadManager.getStatistics().getDownloadedSizeThisResource();
//            Long length = downloadManager.getLength();
//            Double part = null;
//            if (length != null) {
//                part = (double) size / (double) length * 100d;
//            }
//            System.out.println("Speed for " + downloadManager.getResourceID() + ": " + speed + "KB, downloaded part: " + part);
            transferStatsProperties.updateDownloadState(downloadManager);
            filesStateProperties.updateFileDownload(downloadManager.getResourceID());
        }
    }

    @Override
    public void periodicUploadsNotification(UploadsManager uploadsManager) {
//        for (UploadManager uploadManager : uploadsManager.getAllUploads()) {
//            Double speed = uploadManager.getStatistics().getSpeed();
//            speed /= 1024d;
//            long size = uploadManager.getStatistics().getUploadedSizeThisResource();
//            System.out.println("Speed for " + uploadManager.getResourceID() + ": " + speed + "KB, uploaded size: " + size);
//            System.out.println("Assigned size: " + uploadManager.getStatistics().getAssignedPart().size());
//        }
        transferStatsProperties.updateUploads(uploadsManager.getAllUploads());
    }
}
