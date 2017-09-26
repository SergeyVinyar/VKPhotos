package ru.vinyarsky.vkphotos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;

import butterknife.BindView;
import butterknife.ButterKnife;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.vinyarsky.vkphotos.R;
import ru.vinyarsky.vkphotos.vk.VKRepository;

public class MainActivity extends AppCompatActivity
        implements
            AlbumListFragment.Listener {

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_main_progressbar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        VKSdk.login(this, VKScope.PHOTOS/*, VKScope.OFFLINE*/);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        VKCallback<VKAccessToken> vkCallback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Fragment existedFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment);
                if (existedFragment == null)
                    showAlbumListFragment();
            }

            @Override
            public void onError(VKError error) {
                MainActivity.this.finish();
            }
        };

        VKSdk.onActivityResult(requestCode, resultCode, data, vkCallback);
    }

    public void showAlbumListFragment() {
        showSingleTopFragment(AlbumListFragment.class, AlbumListFragment::newInstance);
    }

    @Override
    public void showAlbumFragment(int albumId) {
//        Fragment newFragment = AlbumFragment.newInstance();
//
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//        transaction.replace(R.id.activity_main_fragment, newFragment);
//        transaction.addToBackStack(null);
//
//        transaction.commit();
    }

    /**
     * Shows new fragment. If it's already existed show the existed instance.
     */
    private void showSingleTopFragment(Class fragmentClass, com.annimon.stream.function.Supplier<Fragment> supplyFragment) {
        String fragmentTag = fragmentClass.getName();

        int fragmentId = getBackstackEntryIdForFragmentTag(fragmentTag);
        if (fragmentId == -1) {
            Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment);
            Fragment newFragment = supplyFragment.get();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (oldFragment != null)
                transaction.remove(oldFragment);

            transaction.replace(R.id.activity_main_fragment, newFragment);

            if (oldFragment != null)
                transaction.addToBackStack(fragmentTag);

            transaction.commit();
        }
        else {
            getSupportFragmentManager().popBackStackImmediate(fragmentId, 0);
        }
    }

    /**
     * Returns id of a backstack entry for a fragment with particular tag.
     * -1 if not found.
     */
    private int getBackstackEntryIdForFragmentTag(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int id = 0; id < fragmentManager.getBackStackEntryCount(); id++)
            if (tag.equals(fragmentManager.getBackStackEntryAt(id).getName()))
                return id;
        return -1;
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
