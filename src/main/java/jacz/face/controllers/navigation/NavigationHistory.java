package jacz.face.controllers.navigation;

import jacz.face.util.MediaItemType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * todo exceptions
 */
public class NavigationHistory {

    public enum Window {
        MEDIA_LIST,
        ITEM_DETAIL,
        TRANSFERS,
        PEERS,
    }

    public enum DialogIntention {
        NEW,
        EDIT
    }

    public static class Element {

        public final Window window;

        public final MediaItemType mediaItemType;

        public final Integer itemId;

        public Integer localId;

        private Element(Window window, MediaItemType mediaItemType, Integer itemId, Integer localId) {
            this.window = window;
            this.mediaItemType = mediaItemType;
            this.itemId = itemId;
            this.localId = localId;
        }

        public void updateLocalId(Integer localId) {
            this.localId = localId;
        }

        public static Element mediaList(MediaItemType mediaItemType) {
            return new Element(Window.MEDIA_LIST, mediaItemType, null, null);
        }

        public static Element itemDetail(MediaItemType mediaItemType, Integer itemId, Integer localId) {
            return new Element(Window.ITEM_DETAIL, mediaItemType, itemId, localId);
        }

        public static Element transfers() {
            return new Element(Window.TRANSFERS, null, null, null);
        }

        public static Element peers() {
            return new Element(Window.PEERS, null, null, null);
        }
    }

    private final List<Element> historyElements;

    private final AtomicInteger historyIndex;

    private DialogIntention currentDialogIntention;

    private final BooleanProperty canMoveBackwards;

    private final BooleanProperty canMoveForward;

    public NavigationHistory(Element initialElement) {
        historyElements = new ArrayList<>();
        historyElements.add(initialElement);
        historyIndex = new AtomicInteger(0);
        canMoveBackwards = new SimpleBooleanProperty();
        canMoveForward = new SimpleBooleanProperty();
    }

    private void updateProperties() {
        canMoveBackwards.setValue(historyIndex.get() > 0);
        canMoveForward.setValue(historyIndex.get() < historyElements.size() - 1);
    }

    public boolean getCanMoveBackwards() {
        return canMoveBackwards.get();
    }

    public BooleanProperty canMoveBackwardsProperty() {
        return canMoveBackwards;
    }

    public boolean getCanMoveForward() {
        return canMoveForward.get();
    }

    public BooleanProperty canMoveForwardProperty() {
        return canMoveForward;
    }

    public Element getCurrentElement() {
        return historyElements.get(historyIndex.get());
    }

    public Element navigate(Element element) {
        while (historyElements.size() > historyIndex.get() + 1) {
            historyElements.remove(historyIndex.get() + 1);
        }
//        for (int i = historyIndex.get() + 1; i < historyElements.size(); i++) {
//            historyElements.remove(i);
//        }
        historyElements.add(element);
        historyIndex.set(historyIndex.get() + 1);
        updateProperties();
        return element;
    }

    public Element forward() {
        return moveIndex(1);
    }

    public Element backwards() {
        return moveIndex(-1);
    }

    private Element moveIndex(int shift) {
        historyIndex.set(historyIndex.get() + shift);
        updateProperties();
        return historyElements.get(historyIndex.get());
    }

    public DialogIntention getCurrentDialogIntention() {
        return currentDialogIntention;
    }

    public void setCurrentDialogIntention(DialogIntention currentDialogIntention) {
        this.currentDialogIntention = currentDialogIntention;
    }
}
