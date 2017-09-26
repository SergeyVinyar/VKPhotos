package ru.vinyarsky.vkphotos.vk;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoAlbum;
import com.vk.sdk.api.model.VKAttachments;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;

public final class VKRepository {

    private VKRepository() {
    }

    public static Observable<List<Album>> getAlbums() {
        return new Observable<List<Album>>() {
            @Override
            protected void subscribeActual(Observer<? super List<Album>> observer) {
                VKParameters parameters = new VKParameters();
                parameters.put("need_covers", 1);

                VKRequest request = new VKRequest("photos.getAlbums", parameters);

                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        VKAttachments attachments = new VKAttachments(response.json);
                        ArrayList<Album> result = new ArrayList<>(attachments.size());
                        for (int i = 0; i < attachments.size(); i++) {
                            VKApiPhotoAlbum album = (VKApiPhotoAlbum) attachments.get(i);
                            result.add(new Album(album.id, album.title, album.thumb_src));
                        }
                        observer.onNext(result);
                        observer.onComplete();
                    }

                    @Override
                    public void onError(VKError error) {
                        observer.onError(new Exception(error.errorMessage));
                    }
                });
            }
        };
    }


    public static class Album {

        private final int id;
        private final String title;
        private final String thumb_src;

        /* package */ Album(int id, String title, String thumb_src) {
            this.id = id;
            this.title = title;
            this.thumb_src = thumb_src;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getThumb_src() {
            return thumb_src;
        }
    }
}
