package sample.patterns.com.osh.sampleapppatterns;

import com.osh.patterns.lib.flow.Flow;
import com.osh.patterns.lib.flow.Flow1;
import com.osh.patterns.lib.handlers.Action;
import com.osh.patterns.lib.handlers.actions.Command;

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
    public void testFlow() {

        final String userId = UUID.randomUUID().toString();
        final String photoUrl = "http:/pictures.net/a.jpg";
        Repository repository = new Repository();
        Action<String> action = Flow.first(repository::getUserById)
                .executeOn(Threads.singleThread())
                .resultOn(Threads.mainThread())
                .onError(Log.onError())
                .onCompleted(() -> Log.d("getUserById - done"))
                .onData(result -> Log.d(result.toString()))
                .next(userRemote -> repository.savePhotoToUserRemote(userRemote, photoUrl))
                .onCompleted(() -> Log.d("savePhotoToUserRemote - done"))
                .onData(result -> Log.d(result.toString()))
                .next(repository::mapUserRemoteToUserLocal)
                .onCompleted(() -> Log.d("mapUserRemoteToUserLocal - done"))
                .onData(result -> Log.d(result.toString()))
                .next(repository::saveUserLocally)
                .onCompleted(() -> Log.d("saveUserLocally - done"))
                .onData(this::onPhotoSaved)
                .getCall();

        action.call(userId);

        Threads.loop(20000);
    }


    @Test
    public void testFlow1() {

        final String userId = UUID.randomUUID().toString();
        final String photoUrl = "http:/pictures.net/a.jpg";

        makeCommand(new Repository())
                .execute(new SavePhotoParams(userId, photoUrl), this::onPhotoSaved, Log.onError());

        Threads.loop(7000);
    }

    private Command<Entities.UserLocal, SavePhotoParams> makeCommand(final Repository repository) {

        return (data, consumer, errorConsumer) -> {
            final String userId = data.getUserId();
            final String photoUrl = data.getPhotoUrl();
            Flow1.given(userId)
                    .executeOn(Threads.singleThread())
                    .resultOn(Threads.mainThread())
                    .onError(errorConsumer)
                    .next(repository::getUserById)
                    .next(userRemote -> repository.savePhotoToUserRemote(userRemote, photoUrl))
                    .map(repository::mapUserRemoteToUserLocal).executeOn(Threads.mainThread())
                    .next(repository::saveUserLocally).executeOn(Threads.singleThread())
                    .consumer(consumer)
                    .run();
        };
    }

    private void onPhotoSaved(Entities.UserLocal userLocal) {
        Log.d("onPhotoSaved");
    }

    private static class SavePhotoParams {
        final String userId;
        final String photoUrl;

        private SavePhotoParams(String userId, String photoUrl) {
            this.userId = userId;
            this.photoUrl = photoUrl;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public String getUserId() {
            return userId;
        }
    }

    private static class Repository {

        public Entities.UserRemote getUserById(String id) {
            Log.d("start getUserById");
            Threads.sleep(1000);
            return new Entities.UserRemote(id, "Jhon", "jd@mail.com", null);
        }

        public Entities.UserRemote savePhotoToUserRemote(Entities.UserRemote user, String dataUrl) {
            Log.d("start savePhotoToUserRemote");
            Threads.sleep(1000);
            user.setPhoto(new Entities.Photo(dataUrl));
            return user;
        }

        public Entities.UserLocal mapUserRemoteToUserLocal(Entities.UserRemote userRemote) {
            Log.d("start mapUserRemoteToUserLocal");
            //Threads.sleep(1000);
            return Entities.mapperLocalUserToRemoteUser.map(userRemote);
        }

        public Entities.UserLocal saveUserLocally(Entities.UserLocal user) {
            Log.d("start saveUserLocally");
            Threads.sleep(1000);
            return user;
        }
    }
}
