package sample.patterns.com.osh.sampleapppatterns;

import com.osh.patterns.lib.flow.Flow;
import com.osh.patterns.lib.handlers.Callable;

import org.junit.Test;

import sample.patterns.com.osh.sampleapppatterns.utils.Log;
import sample.patterns.com.osh.sampleapppatterns.utils.Threads;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public class TestFlow {

    @Test
    public void test() {

        String userId = "12sadf";
        String photoUrl = "aaaaaa";

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

        Threads.sleep(20000);
    }

    private void onPhotoSaved(UserLocal userLocal) {
        Log.d("onPhotoSaved");
    }

    private UserRemote getUserById(String id) {
        Log.d("getUserById - " + id);
        Threads.sleep(3000);
        return new UserRemote(id, id, null);
    }


    private UserRemote savePhotoToUserRemote(UserRemote user, String dataUrl) {
        Log.d("savePhotoToUserRemote - " + dataUrl);
        Threads.sleep(3000);
        user.photo = new Photo(dataUrl);
        return user;
    }

    private UserLocal mapUserRemoteToUserLocal(UserRemote userRemote) {
        Log.d("mapUserRemoteToUserLocal");
        Threads.sleep(3000);
        return new UserLocal(userRemote.email, userRemote.name, userRemote.email);
    }

    private UserLocal saveUserLocally(UserLocal user) {
        Log.d("saveUserLocally");
        Threads.sleep(3000);
        return user;
    }


    private static class UserRemote {
        final String name;
        final String email;
        Photo photo;

        private UserRemote(String name, String email, Photo photo) {
            this.name = name;
            this.email = email;
            this.photo = photo;
        }

        public void setPhoto(Photo photo) {
            this.photo = photo;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " name:" + name + " email:" + email;
        }

    }

    private static class UserLocal {

        final String id;
        final String name;
        final String email;

        private UserLocal(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " id:" + id + " name:" + name + " email:" + email;
        }
    }

    private static class Photo {
        final String photoUrl;

        private Photo(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " photoUrl:" + photoUrl;
        }

    }

}
