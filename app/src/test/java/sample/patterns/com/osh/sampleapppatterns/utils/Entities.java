package sample.patterns.com.osh.sampleapppatterns.utils;

import com.osh.patterns.lib.function.Mapper;

/**
 * Created by Oleg Shatava on 02.05.18.
 */

public final class Entities {
    public static Mapper<UserLocal, UserRemote> mapperLocalUserToRemoteUser = new Mapper<UserLocal, UserRemote>() {
        @Override
        public UserLocal map(UserRemote remote) {
            return new UserLocal(remote.id, remote.name, remote.email, remote.photo);
        }
    };

    private Entities() {
        throw new IllegalStateException("Not intended for create new instance");
    }

    public static class UserRemote {
        final String id;
        final String name;
        final String email;
        Photo photo;

        public UserRemote(String id, String name, String email, Photo photo) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.photo = photo;
        }

        public void setPhoto(Photo photo) {
            this.photo = photo;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName()
                    + " id:" + id
                    + " name:" + name
                    + " email:" + email
                    + (photo != null ? " photo:" + photo.toString() : "");
        }

    }

    public static class UserLocal extends UserRemote {

        public UserLocal(String id, String name, String email, Photo photo) {
            super(id, name, email, photo);
        }
    }

    public static class Photo {
        final String photoUrl;

        public Photo(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName()
                    + " photoUrl:" + photoUrl;
        }

    }

}
