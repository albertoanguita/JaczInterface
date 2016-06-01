package jacz.face.actions.ints;

import jacz.peerengineclient.databases.synch.DatabaseSynchEvents;
import jacz.peerengineservice.PeerId;
import jacz.peerengineservice.util.data_synchronization.SynchError;

/**
 * Created by Alberto on 28/04/2016.
 */
public class DatabaseSynchEventsImpl implements DatabaseSynchEvents {

    @Override
    public void remoteSynchStarted(PeerId remotePeerId) {
        System.out.println("Remote synch started with " + remotePeerId);
    }

    @Override
    public void remoteSynchProgress(PeerId remotePeerId, int progress) {
        System.out.println("Remote synch progress with " + remotePeerId + ", progress: " + progress);
    }

    @Override
    public void remoteSynchError(PeerId remotePeerId, SynchError error) {
        System.out.println("Remote synch error with " + remotePeerId + ", error: " + error);
    }

    @Override
    public void remoteSynchTimeout(PeerId remotePeerId) {
        System.out.println("Remote synch timeout with " + remotePeerId);
    }

    @Override
    public void remoteSynchCompleted(PeerId remotePeerId) {
        System.out.println("Remote synch complete with " + remotePeerId);
    }

    @Override
    public void sharedSynchStarted(PeerId remotePeerId) {
        System.out.println("Shared synch started with " + remotePeerId);
    }

    @Override
    public void sharedSynchProgress(PeerId remotePeerId, int progress) {
        System.out.println("Shared synch progress with " + remotePeerId + ", progress: " + progress);
    }

    @Override
    public void sharedSynchError(PeerId remotePeerId, SynchError error) {
        System.out.println("Shared synch error with " + remotePeerId + ", error: " + error);
    }

    @Override
    public void sharedSynchTimeout(PeerId remotePeerId) {
        System.out.println("Shared synch timeout with " + remotePeerId);
    }

    @Override
    public void sharedSynchCompleted(PeerId remotePeerId) {
        System.out.println("Shared synch complete with " + remotePeerId);
    }
}
