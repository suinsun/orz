package net.vsona.orz.api;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.vsona.orz.utils.AppConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient sRetrofitClient;
    private Retrofit sRetrofit;
    static OkHttpClient client;

    private RetrofitClient() {
    }

    private static RetrofitClient getRetrofitClient() {
        if (sRetrofitClient == null) {
            sRetrofitClient = new RetrofitClient();

            //build okHttpClient
            OkHttpClient client = getOkHttpClient();

            Gson gson = new GsonBuilder().create();

            sRetrofitClient.sRetrofit = new Retrofit.Builder().baseUrl(AppConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(client)
                    .build();
        }
        return sRetrofitClient;
    }

    @NonNull
    public static OkHttpClient getOkHttpClient() {
        if (client != null) {
            return client;
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (AppConstants.debug) {
            // Log信息拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //设置 Debug Log 模式
            builder.addInterceptor(loggingInterceptor);
        }

        //设置cookie
//        CookieManager cookieManager = new CookieManager();
//        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//        builder.cookieJar(new JavaNetCookieJar(cookieManager));
//
        //公共参数
        builder.addInterceptor(new QueryParameterInterceptor());
        //Gzip压缩
//        builder.addInterceptor(new GzipRequestInterceptor());

        //开启缓存
//        File cacheFile = new File(RockContextUtils.getApplicationContext().getExternalCacheDir(), "appCache");
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 30);
//        builder.cache(cache).addInterceptor(new CacheInterceptor());

        //开启facebook debug
//        builder.addNetworkInterceptor(new StethoInterceptor());

        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);

        client = builder.build();
        return client;
    }

    public static <T> T createApi(Class<T> clazz) {
        return getRetrofitClient().sRetrofit.create(clazz);
    }

}
