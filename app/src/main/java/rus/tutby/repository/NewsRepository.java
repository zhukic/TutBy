package rus.tutby.repository;

import java.sql.SQLException;

import rus.tutby.App;
import rus.tutby.entity.News;
import rus.tutby.repository.datasource.CloudDataStore;
import rus.tutby.repository.datasource.DbDataStore;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by RUS on 06.06.2016.
 */
public class NewsRepository implements IRepository {

    public NewsRepository() {}

    @Override
    public Observable<News> getAllNews(final String url) {
        return new CloudDataStore().userEntityList(url);

    }

    @Override
    public Observable<News> getNewsById(final int id) {
        return new DbDataStore().getNewsFromDb(id);
    }

}
