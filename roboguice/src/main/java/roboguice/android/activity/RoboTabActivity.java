/*
 * Copyright 2009 Michael Burton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package roboguice.android.activity;

import java.util.HashMap;
import java.util.Map;

import roboguice.android.DroidGuice;
import roboguice.android.activity.event.OnActivityResultEvent;
import roboguice.android.activity.event.OnConfigurationChangedEvent;
import roboguice.android.activity.event.OnContentChangedEvent;
import roboguice.android.activity.event.OnCreateEvent;
import roboguice.android.activity.event.OnDestroyEvent;
import roboguice.android.activity.event.OnNewIntentEvent;
import roboguice.android.activity.event.OnPauseEvent;
import roboguice.android.activity.event.OnRestartEvent;
import roboguice.android.activity.event.OnResumeEvent;
import roboguice.android.activity.event.OnStartEvent;
import roboguice.android.activity.event.OnStopEvent;
import roboguice.android.inject.ContentViewListener;
import roboguice.android.inject.RoboInjector;
import roboguice.base.RoboGuice;
import roboguice.base.event.EventManager;
import roboguice.base.util.RoboContext;

import com.google.inject.Inject;
import com.google.inject.Key;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * A {@link RoboTabActivity} extends from {@link TabActivity} to provide
 * dynamic injection of collaborators, using Google Guice.<br />
 * 
 * @see RoboActivity
 * 
 * @author Toly Pochkin
 */
public class RoboTabActivity extends TabActivity implements RoboContext<Context> {
    protected EventManager eventManager;
    protected HashMap<Key<?>,Object> scopedObjects = new HashMap<Key<?>, Object>();


    @Inject ContentViewListener ignored; // BUG find a better place to put this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final RoboInjector injector = RoboGuice.<DroidGuice>instance().getInjector(this);
        eventManager = injector.getInstance(EventManager.class);
        injector.injectMembersWithoutViews(this);
        super.onCreate(savedInstanceState);
        eventManager.fire(new OnCreateEvent(savedInstanceState));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        eventManager.fire(new OnRestartEvent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventManager.fire(new OnStartEvent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventManager.fire(new OnResumeEvent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventManager.fire(new OnPauseEvent());
    }

    @Override
    protected void onNewIntent( Intent intent ) {
        super.onNewIntent(intent);
        eventManager.fire(new OnNewIntentEvent());
    }

    @Override
    protected void onStop() {
        try {
            eventManager.fire(new OnStopEvent());
        } finally {
            super.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            eventManager.fire(new OnDestroyEvent());
        } finally {
            try {
                RoboGuice.<DroidGuice>instance().destroyInjector(this);
            } finally {
                super.onDestroy();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        final Configuration currentConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        eventManager.fire(new OnConfigurationChangedEvent(currentConfig, newConfig));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        RoboGuice.<DroidGuice>instance().getInjector(this).injectViewMembers(this);
        eventManager.fire(new OnContentChangedEvent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        eventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
    }

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }

}
