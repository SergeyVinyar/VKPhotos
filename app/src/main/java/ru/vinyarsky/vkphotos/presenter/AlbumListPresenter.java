package ru.vinyarsky.vkphotos.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.vinyarsky.vkphotos.vk.VKRepository;

@InjectViewState
public final class AlbumListPresenter extends MvpPresenter<AlbumListView> {

    private Observable<List<VKRepository.Album>> getAlbums = VKRepository.getAlbums()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    private CompositeDisposable getAlbumsCompositeDisposable;

    private ArrayList<VKRepository.Album> albums = new ArrayList<>(0);

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().displayAlbumList(albums);
        getAlbumsCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(AlbumListView view) {
        super.attachView(view);
        getAlbumsCompositeDisposable.add(
                getAlbums.subscribe(albums -> {
                    getViewState().displayAlbumList(albums);
                }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getAlbumsCompositeDisposable.dispose();
    }

    public void albumClick(int albumId) {
        getViewState().showAlbum(albumId);
    }
}
