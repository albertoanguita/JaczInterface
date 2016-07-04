package jacz.face.actions.ints;

import jacz.face.state.FilesStateProperties;
import jacz.peerengineclient.data.FileHashDatabaseEvents;

/**
 * Created by alberto on 7/4/16.
 */
public class FileHashDatabaseEventsImpl implements FileHashDatabaseEvents {

    private final FilesStateProperties filesStateProperties;

    public FileHashDatabaseEventsImpl(FilesStateProperties filesStateProperties) {
        this.filesStateProperties = filesStateProperties;
    }

    @Override
    public void fileAdded(String hash, String path) {
        filesStateProperties.addToLocalRepo(hash, path);
    }

    @Override
    public void fileRemoved(String hash, String path) {
        filesStateProperties.removeFromLocalRepo(hash);
    }
}
