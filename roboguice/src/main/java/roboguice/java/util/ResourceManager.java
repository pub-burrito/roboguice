package roboguice.java.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import roboguice.base.util.PropertyLoader;
import roboguice.java.inject.JavaResourceListener;

import com.google.common.collect.Lists;

public class ResourceManager {

    private static final Set<String> resourcePaths = Collections.synchronizedSet( new LinkedHashSet<String>() );
    /*
     *           Key     - path to properties file
     *           Value   - properties object 
     */      
     private static final Map<String,Properties> properties = Collections.synchronizedMap( new HashMap<String,Properties>());
     private static Comparator<URL> comparator;
     
     private static ResourceManager instance;
     
     public static ResourceManager instance()
     {
         if ( instance == null )
         {
             instance = new ResourceManager();
         }
         
         return instance;
     }
     
     public ResourceManager addResourcePath( String... paths )
     {
         if ( paths != null && paths.length > 0 )
         {             
             resourcePaths.addAll( Arrays.asList( paths ) );
             //don't add resource paths to map yet
             //Lazy init in JavaMemberResourceInjector
         }
         
         return this;
     }
     
     public ResourceManager addComparator( Comparator<URL> comparator )
     {
         ResourceManager.comparator = comparator;
         return this;
     }

     public ResourceManager removeComparator()
     {
         ResourceManager.comparator = null;
         return this;
     }
     
     public ResourceManager removeResourcePath( String... paths )
     {
         if ( paths != null && paths.length > 0 )
         {
             List<String> list = Arrays.asList(paths);
            resourcePaths.removeAll(list);
             /*
              * Returns a Set view of the keys contained in this map. The set is backed by the map, so changes 
              * to the map are reflected in the set, and vice-versa. If the map is modified while an iteration 
              * over the set is in progress (except through the iterator's own remove operation), the results of 
              * the iteration are undefined. The set supports element removal, which removes the corresponding mapping 
              * from the map, via the Iterator.remove, Set.remove, removeAll, retainAll, and clear operations.
              *  It does not support the add or addAll operations.
              */
             properties.keySet().removeAll(list); 
         }
         
         return this;
     }
     
     public ResourceManager reset()
     {
         resourcePaths.clear();
         properties.clear();
         return this;
     }
     
     public String getValue( String name )
     {
         String val = null;
         
         List<String> paths = Lists.reverse( new ArrayList<String>(resourcePaths) );
         
         //Check cache first
         for ( String path : paths)
         {//for every resource path
             if ( properties.containsKey( path ) )
             {
                 //get property
                 val = properties.get(path).getProperty( name );
                 
                 if ( val != null )
                 {//return if found
                     
                     return val;
                 }
             }
             else
             {
                 Properties prop = ResourceManager.instance().loadProperty(path);
                 
                 val = prop.getProperty( name );
                 
                 if ( val != null )
                 {
                     return val;
                 }
             }
             
         }
         
         return val;
     }
 
    Properties loadProperty(String propertyFile) {

        Properties property = new Properties();

        try 
        {
            Enumeration<URL> urls = JavaResourceListener.class.getClassLoader().getResources( propertyFile );
            List<URL> allUrls = Collections.list(urls);
            if ( comparator != null )
            {
                Collections.sort(allUrls, comparator);
            }
            
            for( URL url : allUrls)
            {//for each url
                
                if ( PropertyLoader.loadProperty(url, property) != null )
                {
                    properties.put(propertyFile, property);
                }
            }
        } 
        catch ( IOException e1 ) 
        {
            e1.printStackTrace();
        }
        return property;
    }
     
     public static interface Callback
     {
         public Properties loaded( Properties property );
     }
}
