package jacz.face.actions.ints;

import jacz.database.DatabaseMediator;
import jacz.face.state.MediaDatabaseProperties;
import jacz.peerengineclient.databases.integration.IntegrationEvents;

/**
 *
 */
public class IntegrationEventsImpl implements IntegrationEvents {

    private final MediaDatabaseProperties mediaDatabaseProperties;

    public IntegrationEventsImpl(MediaDatabaseProperties mediaDatabaseProperties) {
        this.mediaDatabaseProperties = mediaDatabaseProperties;
    }

    @Override
    public void newIntegratedItem(DatabaseMediator.ItemType type, Integer id) {
        System.out.println("New integrated item. Type: " + type + ", id: " + id);
        mediaDatabaseProperties.updateMediaItem(type, id, false);
    }

    @Override
    public void integratedItemHasBeenModified(DatabaseMediator.ItemType type, Integer id, boolean hasNewMediaContent) {
        System.out.println("Integrated item has been modified. Type: " + type + ", id: " + id);
        mediaDatabaseProperties.updateMediaItem(type, id, false);
    }

    @Override
    public void integratedItemHasNewMedia(DatabaseMediator.ItemType type, Integer id) {
        System.out.println("Integrated item has new media. Type: " + type + ", id: " + id);
        mediaDatabaseProperties.itemHasNewMedia(type, id, false);
    }

    @Override
    public void integratedItemRemoved(DatabaseMediator.ItemType type, Integer id) {
        System.out.println("Integrated items removed");
        mediaDatabaseProperties.mediaItemRemoved(type, id, false);
    }
}
