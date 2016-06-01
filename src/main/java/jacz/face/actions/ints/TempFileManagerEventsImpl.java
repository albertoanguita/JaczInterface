package jacz.face.actions.ints;

import jacz.peerengineservice.util.tempfile_api.TempFileManagerEvents;

/**
 *
 */
public class TempFileManagerEventsImpl implements TempFileManagerEvents {

    @Override
    public void indexFileGenerated(String indexFilePath) {
        System.out.println("Index file generated: " + indexFilePath);
    }

    @Override
    public void indexFileRecovered(String indexFilePath) {
        System.out.println("Index file recpvered: " + indexFilePath);
    }

    @Override
    public void indexFileErrorRestoredWithBackup(String indexFilePath) {
        System.out.println("Index file error restored with backup: " + indexFilePath);
    }

    @Override
    public void indexFileError(String indexFilePath) {
        System.out.println("Index file error: " + indexFilePath);
    }
}
