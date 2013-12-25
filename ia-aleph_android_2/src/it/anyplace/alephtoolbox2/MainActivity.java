package it.anyplace.alephtoolbox2;

import it.anyplace.alephtoolbox2.services.CurrentRosterService;
import it.anyplace.alephtoolbox2.services.PersistenceService;
import it.anyplace.alephtoolbox2.services.RosterDataService;
import it.anyplace.alephtoolbox2.services.RosterSynchronizationService;
import it.anyplace.alephtoolbox2.services.SourceDataService;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

public class MainActivity extends Activity {

    @Inject
    private RosterDataService armyListService;
    @Inject
    private CurrentRosterService currentListService;
    // @Inject
    // private UnitDataViewController unitDataViewController;

    @Inject
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        new AsyncTask<Void, Object, Void>() {
            ProgressBar progressBar;
            TextView loadingInfo;
            int progress;
            Injector injector;

            @Override
            protected void onPreExecute() {

                progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
                progressBar.setProgress(progress = 5);

                loadingInfo = (TextView) findViewById(R.id.loadingInfo);
                loadingInfo.setText("preparing ...");
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                Log.d("onProgressUpdate", (String) values[1]);
                progressBar.setProgress((Integer) values[0]);
                loadingInfo.setText((String) values[1]);
            }

            @Override
            protected Void doInBackground(Void... params) {

                publishProgress(progress += 5, "initializing ...");
                injector = Guice.createInjector(new Module() {

                    @Override
                    public void configure(Binder binder) {
                        binder.bind(Context.class).toInstance(MainActivity.this);
                        binder.bind(Activity.class).toInstance(MainActivity.this);
                        binder.bind(Gson.class).asEagerSingleton();
                        binder.bind(EventBus.class).asEagerSingleton();
                    }
                });

                // List<Class> controllers=Lists.newArrayList()
                // injector.

                // init services
                for (Class serviceClass : Arrays.asList(PersistenceService.class, SourceDataService.class,
                        CurrentRosterService.class, RosterSynchronizationService.class)) {
                    publishProgress(progress += 20, "loading service : " + serviceClass.getSimpleName());
                    // loadingInfo.setText("loading service : " +
                    // serviceClass.getSimpleName());
                    injector.getInstance(serviceClass);

                }
                publishProgress(100, "loading interface ...");
                // injector.getInstance(RosterSynchronizationService.class);
                // injector.getInstance(SourceDataService.class);
                // injector.getInstance(CurrentRosterService.class);

                // init view components

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                setContentView(R.layout.main_view);
                injector.getInstance(ViewFlipperController.class);
                injector.getInstance(CurrentRosterController.class);
                injector.getInstance(AvailableUnitsController.class);
                injector.getInstance(UnitDetailController.class);
                injector.getInstance(MenuController.class);

                injector.injectMembers(MainActivity.this);
                // begin
                final String armylistData = "{'pcap':200,'faction':'Panoceania','includeMercs':false,'models':[{'isc':'Cutters','code':'Default','recordid':'26341055822558700'},{'isc':'Bagh-Mari','code':'Shotgun','recordid':'58486073673702776'},{'isc':'Lieutenant Stephen Rao','code':'Default','recordid':'71578364423476160'},{'isc':'Fusiliers','code':'Default','recordid':'15866463608108460'},{'isc':'Fusiliers','code':'Default','recordid':'94999620621092620'},{'isc':'Order Sergeants','code':'Spitfire','recordid':'64432820701040330'},{'isc':'Order Sergeants','code':'TO Sniper','recordid':'2207046304829418.8'}],'listId':'3542035631835460.5','id':'3542035631835460.5','listName':'','dateMod':'1384377643000','groupMarks':[],'combatGroupSize':10,'specop':null,'mercenaryFactions':null}";
                currentListService.loadRoster(armyListService.parseArmyList(armylistData));
            }

        }.execute();

        // injector.getInstance(CurrentListService.class).loadFaction("Aleph",
        // null);
    }

    // @Override
    // protected void onDestroy() {
    // super.onDestroy();
    // }

}