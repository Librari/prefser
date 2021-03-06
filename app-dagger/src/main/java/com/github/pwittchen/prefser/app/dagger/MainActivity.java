package com.github.pwittchen.prefser.app.dagger;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.github.pwittchen.prefser.library.Prefser;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * This is simple exemplary app, which shows basic usage of Prefser with Dagger DI library
 */
public class MainActivity extends BaseActivity {

    @Inject
    public Prefser prefser;

    @InjectView(R.id.value)
    protected EditText value;

    private final static String EMPTY_STRING = "";
    private final static String MY_KEY = "MY_KEY";
    private Subscription subscriptionForSinglePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String text = prefser.get(MY_KEY, String.class, EMPTY_STRING);
        value.setText(text);

        createSubscriptionForSinglePreference();
    }

    private void createSubscriptionForSinglePreference() {

        // here, we created new Subscriber as an extended example,
        // but we can also use simple Action1 interface with call(String key) method
        // as in createSubscriptionForAllPreferences() method

        subscriptionForSinglePreference = prefser.observe(MY_KEY, String.class, EMPTY_STRING)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        // this will never be called until we call it explicitly
                        // subscriber.onCompleted() is not called
                        // in Observable<String> observe(final SharedPreferences sharedPreferences)
                        // method inside Prefser class, because we want to observe preference constantly
                        // and we do not want to terminate subscriber
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(
                                MainActivity.this,
                                String.format("Problem with accessing key %", MY_KEY),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Object o) {
                        value.setText(String.valueOf(o));
                        Toast.makeText(
                                MainActivity.this,
                                String.format("Value in %s changed, really!", MY_KEY),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscriptionForSinglePreference.unsubscribe();
    }

    @OnClick(R.id.save)
    public void onSaveClicked() {
        prefser.put(MY_KEY, value.getText().toString());
    }

    @OnClick(R.id.put_lenny_face)
    public void onPutLennyFaceClicked() {
        prefser.put(MY_KEY, "Hi! I'm Lenny ( ͡° ͜ʖ ͡°)");
    }

    @OnClick(R.id.remove)
    public void onRemoveClicked() {
        prefser.remove(MY_KEY);
    }
}
