package sample.patterns.com.osh.sampleapppatterns;

import com.osh.patterns.lib.flow.Flow;
import com.osh.patterns.lib.handlers.Callable;

import org.junit.Test;

import java.util.UUID;

import sample.patterns.com.osh.sampleapppatterns.utils.Entities;
import sample.patterns.com.osh.sampleapppatterns.utils.Log;
import sample.patterns.com.osh.sampleapppatterns.utils.Threads;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public class TestFlow {

    @Test
    public void test() {

        final String userId = UUID.randomUUID().toString();
        final String photoUrl = "http:/pictures.net/a.jpg";

        Callable<String> action = Flow.first(this::getUserById)
                .executeOn(Threads.singleThread())
                .resultOn(Threads.mainThread())
                .onError(Log.onError())
                .onCompleted(() -> Log.d("getUserById - done"))
                .onData(result -> Log.d(result.toString()))
                .next(userRemote -> savePhotoToUserRemote(userRemote, photoUrl))
                .onCompleted(() -> Log.d("savePhotoToUserRemote - done"))
                .onData(result -> Log.d(result.toString()))
                .next(this::mapUserRemoteToUserLocal)
                .onCompleted(() -> Log.d("mapUserRemoteToUserLocal - done"))
                .onData(result -> Log.d(result.toString()))
                .next(this::saveUserLocally)
                .onCompleted(() -> Log.d("saveUserLocally - done"))
                .onData(this::onPhotoSaved)
                .getCall();

        action.call(userId);

        Threads.loop(20000);
    }

    private void onPhotoSaved(Entities.UserLocal userLocal) {
        Log.d("onPhotoSaved");
    }

    private Entities.UserRemote getUserById(String id) {
        Log.d("start getUserById");
        Threads.sleep(1000);
        return new Entities.UserRemote(id, "Jhon", "jd@mail.com", null);
    }

    private Entities.UserRemote savePhotoToUserRemote(Entities.UserRemote user, String dataUrl) {
        Log.d("start savePhotoToUserRemote");
        Threads.sleep(1000);
        user.setPhoto(new Entities.Photo(dataUrl));
        return user;
    }

    private Entities.UserLocal mapUserRemoteToUserLocal(Entities.UserRemote userRemote) {
        Log.d("start mapUserRemoteToUserLocal");
        Threads.sleep(1000);
        return Entities.mapperLocalUserToRemoteUser.map(userRemote);
    }

    private Entities.UserLocal saveUserLocally(Entities.UserLocal user) {
        Log.d("start saveUserLocally");
        Threads.sleep(1000);
        return user;
    }

}
