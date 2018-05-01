package sample.patterns.com.osh.sampleapppatterns;

import com.osh.patterns.lib.flow.Flow;
import com.osh.patterns.lib.handlers.Callable;
import com.osh.patterns.lib.handlers.Executor;
import com.osh.patterns.lib.handlers.Provider;
import com.osh.patterns.lib.handlers.data.ErrorHandler;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public class TestFlow {

    private final static ScheduledExecutorService mainThread = Executors.newSingleThreadScheduledExecutor();
    private final static ScheduledExecutorService workerThread = Executors.newSingleThreadScheduledExecutor();
    private static Provider<Executor> resultExecutorProvider = () -> (Executor) mainThread::execute;
    private static Provider<Executor> jobExecutorProviderS = () -> (Executor) workerThread::execute;
    private static ErrorHandler errorHandler = Throwable::printStackTrace;

    private static void log(String log) {
        Date time = new Date();
        System.out.println("#" + Thread.currentThread().getId() + ":" + time.toString() + ":" + log);
    }

    private static void sleep(long sec) {
        try {
            Thread.sleep(sec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {

        String userId = "12sadf";
        String photoUrl = "aaaaaa";

        Callable<String> action = Flow.first(this::getUserById)
                .executeOn(jobExecutorProviderS)
                .resultOn(resultExecutorProvider)
                .onCompleted(() -> log("getUserById - done"))
                .onData(result -> log(result.toString()))
                .next(userRemote -> savePhotoToUserRemote(userRemote, photoUrl))
                .onCompleted(() -> log("savePhotoToUserRemote - done"))
                .onData(result -> log(result.toString()))
                .next(this::mapUserRemoteToUserLocal)
                .onCompleted(() -> log("mapUserRemoteToUserLocal - done"))
                .onData(result -> log(result.toString()))
                .next(this::saveUserLocally)
                .onCompleted(() -> log("saveUserLocally - done"))
                .onData(this::onPhotoSaved)
                .getCall();

        action.call(userId);

        sleep(20000);
    }

    private void onPhotoSaved(UserLocal userLocal) {
        log("onPhotoSaved");
    }

    private UserRemote getUserById(String id) {
        log("getUserById - " + id);
        sleep(3000);
        return new UserRemote(id, id, null);
    }


    private UserRemote savePhotoToUserRemote(UserRemote user, String dataUrl) {
        log("savePhotoToUserRemote - " + dataUrl);
        sleep(3000);
        user.photo = new Photo(dataUrl);
        return user;
    }

    private UserLocal mapUserRemoteToUserLocal(UserRemote userRemote) {
        log("mapUserRemoteToUserLocal");
        sleep(3000);
        return new UserLocal(userRemote.email, userRemote.name, userRemote.email);
    }

    private UserLocal saveUserLocally(UserLocal user) {
        log("saveUserLocally");
        sleep(3000);
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
    }

    private static class Photo {
        final String photoUrl;

        private Photo(String photoUrl) {
            this.photoUrl = photoUrl;
        }
    }

}
