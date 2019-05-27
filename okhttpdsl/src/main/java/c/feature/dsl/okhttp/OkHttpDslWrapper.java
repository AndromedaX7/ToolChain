package c.feature.dsl.okhttp;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class OkHttpDslWrapper {
    public static OkHttpDSL create(String url, String body, Method method, MimeType mimeType) {
        OkHttpDSL okHttpDSL = new OkHttpDSL();
        okHttpDSL.getRequestDescription().set(url, method, mimeType, body);
        return okHttpDSL;
    }

    public static OkHttpDSL createPostX_FORM_URLENCODED(String url, String body) {
        OkHttpDSL okHttpDSL = new OkHttpDSL();
        okHttpDSL.getRequestDescription().set(url, Method.POST, MimeType.APPLICATION_X_FORM_URLENCODED, body);
        return okHttpDSL;
    }

    public static OkHttpDSL createPostJson(String url, String body) {
        OkHttpDSL okHttpDSL = new OkHttpDSL();
        okHttpDSL.getRequestDescription().set(url, Method.POST, MimeType.APPLICATION_JSON, body);
        return okHttpDSL;
    }


//     private void test() {
//         create("", "", Method.POST, MimeType.APPLICATION_X_FORM_URLENCODED).callType(File.class, new CallType<File>() {
//             @Override
//             public void exception(@NotNull Throwable throwable) {
//             }
// 
//             @Override
//             public void callType(File result) {
// 
//             }
//         });
//         create(null, null, null, null).callString(new CallString() {
//             @Override
//             public void callString(@NotNull String result) {
//             }
//             @Override
//             public void exception(@NotNull Throwable throwable) {
//             }
//         });
//     }
}
