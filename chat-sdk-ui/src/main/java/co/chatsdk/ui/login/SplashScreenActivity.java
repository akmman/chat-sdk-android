package co.chatsdk.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.LayoutRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.HashMap;

import co.chatsdk.core.events.EventType;
import co.chatsdk.core.events.NetworkEvent;
import co.chatsdk.core.hook.Executor;
import co.chatsdk.core.hook.Hook;
import co.chatsdk.core.hook.HookEvent;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.ui.R;
import co.chatsdk.ui.main.BaseActivity;
import io.reactivex.Completable;
import io.reactivex.functions.Consumer;

public class SplashScreenActivity extends BaseActivity {

    public static int AUTH = 1;

    ConstraintLayout mainView;

    protected ImageView imageView;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainView = findViewById(R.id.view_root);

        imageView = (ImageView) mainView.getViewById(R.id.image_view);
        progressBar = (ProgressBar) mainView.getViewById(R.id.progress_bar);

        imageView.setImageResource(ChatSDK.config().logoDrawableResourceID);

        stopProgressBar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        startNextActivity();
    }

    protected void startNextActivity () {
        if (ChatSDK.auth().isAuthenticatedThisSession()) {
            startMainActivity();
        } else if (ChatSDK.auth().isAuthenticated()) {
            startProgressBar();
            if (ChatSDK.auth().isAuthenticating()) {
                ChatSDK.hook().addHook(Hook.sync(data -> {
                    stopProgressBar();
                    startMainActivity();
                }, true), HookEvent.DidAuthenticate);
            } else {
                dm.add(ChatSDK.auth().authenticate()
                        .doFinally(this::stopProgressBar)
                        .subscribe(this::startMainActivity, throwable -> startLoginActivity()));
            }
        } else {
            startLoginActivity();
        }
    }

    protected void startMainActivity () {
        ChatSDK.ui().startMainActivity(this, extras);
    }

    protected void startLoginActivity () {
        startActivityForResult(ChatSDK.ui().getLoginIntent(this, extras), AUTH);
    }

    protected void startProgressBar () {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        progressBar.animate();
    }

    protected void stopProgressBar () {
//        progressBar.setVisibility(View.GONE);
        progressBar.setProgress(0);
        progressBar.setIndeterminate(false);
    }


    protected @LayoutRes
    int activityLayout() {
        return R.layout.activity_splash_screen;
    }

}
