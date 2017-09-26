package ru.vinyarsky.vkphotos.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKServiceActivity;
import com.vk.sdk.api.VKError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.vinyarsky.vkphotos.R;
import ru.vinyarsky.vkphotos.presenter.AlbumListPresenter;
import ru.vinyarsky.vkphotos.presenter.AlbumListView;
import ru.vinyarsky.vkphotos.vk.VKRepository;

public final class AlbumListFragment extends MvpAppCompatFragment implements AlbumListView {

    public static AlbumListFragment newInstance() { // For possible arguments in future
        return new AlbumListFragment();
    }

    @InjectPresenter
    AlbumListPresenter presenter;

    @BindView(R.id.album_list_fragment_album_list)
    RecyclerView recyclerView;

    @BindView(R.id.album_list_fragment_no_data_label)
    TextView noDataLabel;

    private Listener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (!(activity instanceof Listener))
            throw new IllegalArgumentException("Activity must implement AddressListFragment.Listener");
        listener = (Listener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new AlbumAdapter());

        return view;
    }

    @Override
    public void displayAlbumList(List<VKRepository.Album> list) {
        ((AlbumAdapter) recyclerView.getAdapter()).swapAlbumList(list);
        if (list.size() == 0)
            noDataLabel.setVisibility(View.VISIBLE);
        else
            noDataLabel.setVisibility(View.GONE);
    }

    @Override
    public void showAlbum(int albumId) {
        listener.showAlbumFragment(albumId);
    }

    //    private void loginToVK() {
//        // https://github.com/VKCOM/vk-android-sdk/issues/170
//        Intent intent = new Intent(getActivity(), VKServiceActivity.class);
//        intent.putExtra("arg1", "Authorization");
//        ArrayList scopes = new ArrayList<>();
//        scopes.add(VKScope.PHOTOS);
//        intent.putStringArrayListExtra("arg2", scopes);
//        intent.putExtra("arg4", VKSdk.isCustomInitialize());
//        startActivityForResult(intent, VKServiceActivity.VKServiceType.Authorization.getOuterCode());
//    }

    private static class AlbumAdapter extends RecyclerView.Adapter {

        List<VKRepository.Album> albumList;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_album_list_item, parent, false);
            return new AlbumViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VKRepository.Album album = albumList.get(position);

            AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
            albumViewHolder.titleView.setText(album.getTitle());
        }

        @Override
        public int getItemCount() {
            return albumList != null ? albumList.size() : 0;
        }

        public void swapAlbumList(List<VKRepository.Album> albumList) {
            this.albumList = albumList;
            notifyDataSetChanged();
        }
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.album_list_fragment_album_image)
        public ImageView imageView;

        @BindView(R.id.album_list_fragment_album_title)
        public TextView titleView;

        AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface Listener {
        void showAlbumFragment(int albumId);

        void showProgress();
        void hideProgress();
    }
}
