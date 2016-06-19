package jacz.face.controllers.navigation;

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

    public enum MediaType {
        MOVIES,
        SERIES,
        FAVORITES
    }


    public static class Element {

        public final Window window;

        public final MediaType mediaType;

        public final Integer itemId;

        private Element(Window window, MediaType mediaType, Integer itemId) {
            this.window = window;
            this.mediaType = mediaType;
            this.itemId = itemId;
        }

        public static Element mediaList(MediaType mediaType) {
            return new Element(Window.MEDIA_LIST, mediaType, null);
        }

        public static Element itemDetail(MediaType mediaType, Integer itemId) {
            return new Element(Window.ITEM_DETAIL, mediaType, itemId);
        }

        public static Element transfers() {
            return new Element(Window.TRANSFERS, null, null);
        }

        public static Element peers() {
            return new Element(Window.PEERS, null, null);
        }
    }

    private final List<Element> historyElements;

    private final AtomicInteger historyIndex;

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
}
