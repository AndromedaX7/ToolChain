package c.feature.component.common;

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import com.google.gson.Gson
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Scope
import javax.inject.Singleton

open class OriginApp : Application() {
    private lateinit var appComponent: AppComponent;
    private fun setAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build();
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        setAppComponent()
    }

    fun getAppComponent(): AppComponent? {
        return appComponent;
    }

    companion object {
        lateinit var app: OriginApp
        fun app(): OriginApp {
            return app
        }
    }
}

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun app(): OriginApp
    fun gson(): Gson
}


@Module
class AppModule(private val application: OriginApp) {

    @Singleton
    @Provides
    internal fun provideApp(): OriginApp {
        return application
    }

    @Singleton
    @Provides
    internal fun provideGson(): Gson {
        return Gson()
    }
}

abstract class BasicMvpActivity<P : BasicMvpPresenter<*, *>> : AppCompatActivity() {
    @Inject
    protected lateinit var mPresenter: P

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        ButterKnife.bind(this)
        componentInject()
        getIntentData()
        initView()
        initEventAdapter()
        initData()
    }

    protected abstract fun layoutId(): Int

    protected abstract fun initView()
    protected abstract fun initData()
    protected open fun initEventAdapter() {}
    protected open fun getIntentData() {}

    private fun componentInject() {
        setUpActivityComponent((application as OriginApp).getAppComponent())
    }

    protected abstract fun setUpActivityComponent(appComponent: AppComponent?)
}

abstract class BasicMvpFragment<P : BasicMvpPresenter<*, *>> : androidx.fragment.app.Fragment() {
    @Inject
    protected lateinit var mPresenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        componentInject()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(layoutId(), container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        initView()
        initEventAdapter()
        initData()
    }

    protected abstract fun layoutId(): Int

    protected abstract fun initView()
    protected abstract fun initData()
    protected open fun initEventAdapter() {

    }

    private fun componentInject() {
        setupFragmentComponent(OriginApp.app.getAppComponent())
    }

    protected abstract fun setupFragmentComponent(appComponent: AppComponent?)
}

interface ModelCommon
interface ViewCommon
abstract class BasicModel protected constructor(protected val application: OriginApp, protected val gson: Gson)

open class BasicMvpPresenter<M, V> protected constructor(protected val mModel: M, protected val mRootView: V) {
    protected fun onComplete() {}
    fun onError(throwable: Throwable) {
        throwable.printStackTrace()
    }

    open fun onDestroy() {

    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope


@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentScope


fun retrofitCreator(client: OkHttpClient, basedUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(basedUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
}

fun provideOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient().newBuilder()
    if (BuildConfig.DEBUG) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(interceptor)
    }
    return builder
        .connectTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .build()
}


val okHttpClient = provideOkHttpClient();