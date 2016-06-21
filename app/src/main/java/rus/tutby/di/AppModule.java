package rus.tutby.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rus.tutby.App;
import rus.tutby.interactors.GetNewsListUseCase;

/**
 * Created by RUS on 05.06.2016.
 */
@Module(library = true, injects = {GetNewsListUseCase.class})
public class AppModule {

    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return app;
    }

}
