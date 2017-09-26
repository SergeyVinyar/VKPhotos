package ru.vinyarsky.vkphotos.presenter;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.vinyarsky.vkphotos.vk.VKRepository;

public interface AlbumListView extends MvpView {

    @StateStrategyType(SingleStateStrategy.class)
    void displayAlbumList(List<VKRepository.Album> albums);

    @StateStrategyType(SkipStrategy.class)
    void showAlbum(int albumId);
}
