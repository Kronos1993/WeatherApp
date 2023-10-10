package com.kronos.webclient.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kronos.webclient.UrlProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.*

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {

    @Provides
    @Singleton
    fun provideHttpCache(@ApplicationContext application: Context): Cache? {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideGson(): Gson? {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("dd/MM/yyyy")
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(@ApplicationContext application: Context, cache: Cache?): OkHttpClient? {
        val client = OkHttpClient.Builder()
/*        //get cert .pfx, change for certificate file in resources raw folder
        var caFIS = application.resources.openRawResource(-1)

        //set cert into a keystore
        val keystore = KeyStore.getInstance("PKCS12")
        keystore.load(caFIS,"keystore password".toCharArray())

        //create a key manager
        val keyManagerFactory = KeyManagerFactory.getInstance("X509")
        keyManagerFactory.init(keystore,"keystore password".toCharArray())

        //create the SSL
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(keyManagerFactory.keyManagers, null, SecureRandom())

        val socketFactory = sslContext.socketFactory*/

        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                    return arrayOf()
                }
            }
        )

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory
        val trustManagerFactory: TrustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers: Array<TrustManager> =
            trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            "Unexpected default trust managers:" + trustManagers.contentToString()
        }

        val trustManager =
            trustManagers[0] as X509TrustManager

        client.sslSocketFactory(sslSocketFactory, trustManager)
        client.hostnameVerifier(HostnameVerifier { _, _ -> true })
        client.readTimeout(60, TimeUnit.SECONDS)
        client.connectTimeout(60, TimeUnit.SECONDS)
        client.build()

        client.cache(cache)
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson?, okHttpClient: OkHttpClient?,urlProvider: UrlProvider): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(urlProvider.getApiUrl())
            .client(okHttpClient)
            .build()
    }

}