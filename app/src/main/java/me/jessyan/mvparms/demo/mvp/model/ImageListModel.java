package me.jessyan.mvparms.demo.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.mvp.BaseModel;

import io.rx_cache.DynamicKey;
import io.rx_cache.EvictDynamicKey;
import io.rx_cache.Reply;
import me.jessyan.mvparms.demo.mvp.contract.ImageListContract;
import me.jessyan.mvparms.demo.mvp.model.api.cache.CacheManager;
import me.jessyan.mvparms.demo.mvp.model.api.cache.CommonCache;
import me.jessyan.mvparms.demo.mvp.model.api.service.CommonService;
import me.jessyan.mvparms.demo.mvp.model.api.service.ServiceManager;
import me.jessyan.mvparms.demo.mvp.model.entity.ImageEntity;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;



/**
 * 通过Template生成对应页面的MVP和Dagger代码,请注意输入框中输入的名字必须相同
 * 由于每个项目包结构都不一定相同,所以每生成一个文件需要自己导入import包名,可以在设置中设置自动导入包名
 * 请在对应包下按以下顺序生成对应代码,Contract->Model->Presenter->Activity->Module->Component
 * 因为生成Activity时,Module和Component还没生成,但是Activity中有它们的引用,所以会报错,但是不用理会
 * 继续将Module和Component生成完后,编译一下项目再回到Activity,按提示修改一个方法名即可
 * 如果想生成Fragment的相关文件,则将上面构建顺序中的Activity换为Fragment,并将Component中inject方法的参数改为此Fragment
 */

/**
 * Created by xing on 2016/12/1.
 */

public class ImageListModel extends BaseModel<ServiceManager, CacheManager> implements ImageListContract.Model {
    private Gson mGson;
    private Application mApplication;
    private CommonService mCommonService;
    private CommonCache mCommonCache;

    public ImageListModel(ServiceManager serviceManager, CacheManager cacheManager, Gson gson, Application application) {
        super(serviceManager, cacheManager);
        this.mGson = gson;
        this.mApplication = application;
        this.mCommonService = mServiceManager.getCommonService();
        this.mCommonCache = mCacheManager.getCommonCache();
    }


    @Override
    public Observable<ImageEntity> getImageList(String cacheName,int page, int id, final boolean update) {
        Observable<ImageEntity> imageList = mCommonService
                .getImageList(page, id);
        //使用rxcache缓存,上拉刷新则不读取缓存,加载更多读取缓存
        return mCommonCache
                .getImageList(imageList
                        , new DynamicKey(cacheName + page)
                        , new EvictDynamicKey(update))
                .flatMap(new Func1<Reply<ImageEntity>, Observable<ImageEntity>>() {
                    @Override
                    public Observable<ImageEntity> call(Reply<ImageEntity> listReply) {
                        Timber.tag("ImageEntity").w("ImageListModel+info信息"+listReply.getData().getTngou().size()+"update"+update);
                        return Observable.just(listReply.getData());
                    }
                });
    }
}