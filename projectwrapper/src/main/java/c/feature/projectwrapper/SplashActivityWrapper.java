package c.feature.projectwrapper;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.xdja.uaac.api.TokenCallback;
import com.xdja.uaac.api.UaacApi;

public abstract class SplashActivityWrapper extends AppCompatActivity {

    public abstract void login(String token);
    public  abstract  void uaacApiError(String error);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UaacApi.getToken(this, new TokenCallback() {
            @Override
            public void onSuccess(final String s, boolean b) {
                if (b) {
                    login(s);
                } else {
                    getWindow().getDecorView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            login(s);
                        }
                    }, 5000);
                }
            }

            @Override
            public void onError(String s) {
                uaacApiError(s);
            }
        });
    }
}
