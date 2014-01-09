package roboguice.java.util;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import roboguice.base.util.logging.Ln;
import roboguice.java.inject.JavaResourceListener;

public class ResourceManager {

    private static final Set<String> resourcePaths = Collections.synchronizedSet( new HashSet<String>() );
    /*
     *           Key     - path to properties file
     *           Value   - properties object 
     */      
     private static final Map<String,Properties> properties = Collections.synchronizedMap( new HashMap<String,Properties>());
     
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
             resourcePaths.addAll( Arrays.asList(paths) );
             //don't add resource paths to map yet
             //Lazy init in JavaMemberResourceInjector
         }
         
         return this;
     }
     
     public ResourceManager removeResourcePath( String... paths )
     {
         if ( paths != null && paths.length > 0 )
         {
             resourcePaths.removeAll(Arrays.asList(paths));
             /*
              * Returns a Set view of the keys contained in this map. The set is backed by the map, so changes 
              * to the map are reflected in the set, and vice-versa. If the map is modified while an iteration 
              * over the set is in progress (except through the iterator's own remove operation), the results of 
              * the iteration are undefined. The set supports element removal, which removes the corresponding mapping 
              * from the map, via the Iterator.remove, Set.remove, removeAll, retainAll, and clear operations.
              *  It does not support the add or addAll operations.
              */
             properties.keySet().removeAll(Arrays.asList(paths)); 
         }
         
         return this;
     }
     
     public ResourceManager removeAllPaths()
     {
         resourcePaths.clear();
         return this;
     }
     
     public Object getValue( String name )
     {
         Object val = null;
         //Check cache first
         for ( String resourcePath : properties.keySet() )
         {//for every resource path
             
             //get property
             val = properties.get(resourcePath).getProperty( name );
             
             if ( val != null )
             {//return if found
                 
                 return val;
             }
             
         }
         
         //No properties found in cache
         //Start loading properties
         for ( String resourcePath : resourcePaths )
         {
                 Properties prop = ResourceManager.instance().loadProperty(resourcePath);
                 
                 val = prop.get( name );
                 
                 if ( val != null )
                 {
                     return val;
                 }
         }
         
         return val;
     }
 
     public Properties loadProperty(String propertyFile) {
         
         Properties property = new Properties();
                 
         InputStream in = JavaResourceListener.class.getClassLoader().getResourceAsStream( propertyFile );
         try
         {//and load the property file 
             if ( in != null )
             {
                 property.load( in );

                 //put resource path cache map into main cache
                 properties.put(propertyFile, property);
             } 
             else
             {
                 Ln.w( "Could not find [%s] resource - can not inject any resources in specified file.", propertyFile );
             }
         }
         catch ( Exception e )
         {
             Ln.e( e, "Error loading property file [%s]", propertyFile );
         }
         finally
         {
             try
             {
                 in.close();
             }
             catch ( Exception ex )
             {
                 // ignore
             }
         }
         
         return property;
     }
     
     public static interface Callback
     {
         public Properties loaded( Properties property );
     }
}
