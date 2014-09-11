package parse.network;

import android.app.Application;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.ObjectPersisterFactory;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.retrofit.RetrofitObjectPersisterFactory;
import com.octo.android.robospice.retrofit.RetrofitSpiceService;
import org.reclaimphilly.android.ReclaimPhilly;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

import java.io.File;

/**
 * Created by gosullivan on 3/7/14.
 */
public class ParseRestService extends RetrofitSpiceService {

    private static final int THREAD_COUNT = 3;

    @Override
    public void onCreate() {
        super.onCreate();

        RestAdapter.Builder builder = createRestAdapterBuilder();
        addRetrofitInterface(ParseApiV10.class);
    }

    @Override
    protected String getServerUrl() {
        return RestAdapterFactory.parseApiUrl;
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        ReclaimPhilly rePHL = (ReclaimPhilly) getApplication();
        return RestAdapterFactory.provideBuilder(rePHL, getServerUrl(), getConverter());
    }

    @Override
    protected Converter createConverter() {
        return GsonFactory.provideGsonConverter();
    }

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        ObjectPersisterFactory persisterFactory = new RetrofitObjectPersisterFactory(application,
                getConverter(), getCacheFolder());
        persisterFactory.setAsyncSaveEnabled(true);
        cacheManager.addPersister(persisterFactory);
        return cacheManager;
    }

    @Override
    public int getThreadCount() {
        return THREAD_COUNT;
    }


    /* package */ File getCacheFolder() {
        return null;
    }

}

