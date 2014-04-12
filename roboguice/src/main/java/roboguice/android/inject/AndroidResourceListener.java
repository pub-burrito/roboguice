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
package roboguice.android.inject;

import java.lang.reflect.Field;

import roboguice.base.inject.InjectResource;
import roboguice.base.inject.ResourceListener;

import com.google.inject.TypeLiteral;
import roboguice.base.util.logging.Ln;

import android.app.Application;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


/**
 * Resource listener.
 * @author Mike Burton
 */
public class AndroidResourceListener extends ResourceListener {
    protected Application application;

    public AndroidResourceListener(Application application) {
        super();
        this.application = application;
    }

    @Override
    protected <I> ResourceMemberInjector<I> newResourceMemberInjector(TypeLiteral<I> typeLiteral, Field field ) {
        return new AndroidResourceMembersInjector<I>(field, application, field.getAnnotation(InjectResource.class));
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected ResourceMemberInjector newResourceMemberInjector( Field field ) {
        return new AndroidResourceMembersInjector(field, application, field.getAnnotation(InjectResource.class));
    }

    protected static class AndroidResourceMembersInjector<T> extends ResourceMemberInjector<T> {

        protected Application application;


        public AndroidResourceMembersInjector(Field field, Application application, InjectResource annotation) {
            super( field, annotation );
            this.application = application;
        }

        @Override
        protected Object getValue() {
            
            final Resources resources = application.getResources();
            final int id = getId(resources,annotation);
            final Class<?> t = field.getType();

            try
            {
                if (String.class.isAssignableFrom(t)) {
                    return resources.getString(id);
                } else if (boolean.class.isAssignableFrom(t) || Boolean.class.isAssignableFrom(t)) {
                    return resources.getBoolean(id);
                } else if (ColorStateList.class.isAssignableFrom(t)  ) {
                    return resources.getColorStateList(id);
                } else if (int.class.isAssignableFrom(t) || Integer.class.isAssignableFrom(t)) {
                    return resources.getInteger(id);
                } else if (Drawable.class.isAssignableFrom(t)) {
                    return resources.getDrawable(id);
                } else if (String[].class.isAssignableFrom(t)) {
                    return resources.getStringArray(id);
                } else if (int[].class.isAssignableFrom(t) || Integer[].class.isAssignableFrom(t)) {
                    return resources.getIntArray(id);
                } else if (Animation.class.isAssignableFrom(t)) {
                    return AnimationUtils.loadAnimation(application, id);
                } else if (Movie.class.isAssignableFrom(t)  ) {
                    return resources.getMovie(id);
                }
            }
            catch( Resources.NotFoundException ex){
                Ln.e(ex);
            }
            
            return null;
        }
        
        protected int getId(Resources resources, InjectResource annotation) {
            int id = annotation.value();
            return id>=0 ? id : resources.getIdentifier(annotation.name(),null,null);
        }
    }

}

