package jacz.face.actions.ints;

import jacz.database.DatabaseMediator;
import jacz.peerengineclient.databases.integration.IntegrationEvents;

/**
 *
 */
public class IntegrationEventsImpl implements IntegrationEvents {

    @Override
    public void newIntegratedItem(DatabaseMediator.ItemType type, Integer id) {
        System.out.println("New integrated item. Type: " + type + ", id: " + id);
    }

    @Override
    public void integratedItemHasNewMediaContent(DatabaseMediator.ItemType type, Integer id) {
        System.out.println("Integrated item has new media content. Type: " + type + ", id: " + id);
    }

    @Override
    public void integratedItemHasBeenModified(DatabaseMediator.ItemType type, Integer id) {
        System.out.println("Integrated item has been modified. Type: " + type + ", id: " + id);
    }

    @Override
    public void integratedItemsRemoved() {
        System.out.println("Integrated items removed");
    }
}
