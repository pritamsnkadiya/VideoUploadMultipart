package spaak.supertec.vedioupload.api;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import spaak.supertec.vedioupload.utils.AppConstants;
import spaak.supertec.vedioupload.utils.Method;

public class ApiClient implements Serializable {

    private static final String TAG = ApiClient.class.getSimpleName();

    private static final boolean production = false;

   // public static final String BASE_URL = "http://192.168.9.104:8000/";

    public static final String BASE_URL = "http://shribalajismultispecialityhospital.com";

    public static boolean isProduction() {
        return production;
    }

    private static Retrofit retrofit = null;

    private static ApiClient apiClient;

    public Context context;

    private static final Object mLock = new Object();

    public ApiClient() {
    }

    public ApiClient(Context context) {
        this.context = context;
    }

    public static ApiClient getSingletonApiClient() {
        synchronized (mLock) {
            if (apiClient == null)
                apiClient = new ApiClient();
            return apiClient;
        }
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60 * 5, TimeUnit.SECONDS)
                    .readTimeout(60 * 5, TimeUnit.SECONDS)
                  //  .addInterceptor(new LoggingInterceptor())
                    .writeTimeout(60 * 5, TimeUnit.SECONDS);

            GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient.build())
                    .addConverterFactory(gsonConverterFactory)
                    .build();
        }
        return retrofit;
    }

    public static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            String token;
            AppConstants.TOKEN = Method.getPreferences(AppConstants.CONTEXT, "Authorization");
            Request original = chain.request();
            if (AppConstants.TOKEN != null && !AppConstants.TOKEN.equalsIgnoreCase("")) {
                token = Method.getPreferences(AppConstants.CONTEXT, "Authorization");
                // // token = "Bearer" + " " + Method.getPreferences(AppConstants.CONTEXT, "Authorization");
            } else {
                token = "";
            }
            Request request = original.newBuilder()
                    .header("Authorization", token)
                    .method(original.method(), original.body())
                    .build();

            long t1 = System.nanoTime();
            String requestLog = String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers());
            if (request.method().compareToIgnoreCase("post") == 0) {
                requestLog = "\n" + requestLog + "\n" + bodyToString(request);
            }

            try {
                Log.d(TAG, "request" + "\n" + requestLog);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            String responseLog = String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers());
            String bodyString = response.body().string();
            Log.d(TAG, "response" + "\n" + responseLog + "\n" + bodyString);


            try {
                return response.newBuilder()
                        .body(ResponseBody.create(response.body().contentType(), bodyString))
                        .build();
            } catch (Exception e) {
                Log.d(TAG, e.getMessage() + "---- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            }
            return null;
        }

        public static String bodyToString(final Request request) {
            try {
                final Request copy = request.newBuilder().build();
                final Buffer buffer = new Buffer();
                copy.body().writeTo(buffer);
                return buffer.readUtf8();
            } catch (final IOException e) {
                return "did not work";
            }
        }
    }
}