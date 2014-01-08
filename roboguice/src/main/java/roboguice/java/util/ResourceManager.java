package roboguice.java.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Collection;
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
    /* Key represents an entry in a resourcePath
     *  Value -
     *           Key     - path to properties file
     *           Value   - properties object 
     */      
     private static final Map<String, HashMap<String,Properties>> properties = Collections.synchronizedMap( new HashMap<String, HashMap<String,Properties>>());
     
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
             resourcePaths.addAll( resourcePaths );
             //don't add resource paths to map yet
             //Lazy init in JavaMemberResourceInjector
         }
         
         return this;
     }
     
     public ResourceManager removeResourcePath( String... paths )
     {
         if ( paths != null && paths.length > 0 )
         {
             resourcePaths.removeAll(resourcePaths);
             /*
              * Returns a Set view of the keys contained in this map. The set is backed by the map, so changes 
              * to the map are reflected in the set, and vice-versa. If the map is modified while an iteration 
              * over the set is in progress (except through the iterator's own remove operation), the results of 
              * the iteration are undefined. The set supports element removal, which removes the corresponding mapping 
              * from the map, via the Iterator.remove, Set.remove, removeAll, retainAll, and clear operations.
              *  It does not support the add or addAll operations.
              */
             properties.keySet().removeAll(resourcePaths); 
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
             
             Map<String, Properties> props = properties.get( resourcePath );
             for ( String propertyFile : props.keySet() )
             {//for each property file 
                 
                 //get property
                 val = props.get(propertyFile).getProperty( name );
                 
                 if ( val != null )
                 {//return if found
                     
                     return val;
                 }
             }
             
         }
         
         //No properties found in cache
         //Start loading properties
         
         for ( String resourcePath : resourcePaths )
         {
             File file = new File(resourcePath);
             
             if ( file.exists() )
             {
                 if ( file.isDirectory() )
                 {// if directory
                     
                     //get all property files
                     Collection<File> allProperties =
                             listFiles(
                                    file,
                                     new FilenameFilter() {
                                         
                                         @Override
                                         public boolean accept(File dir, String filename) {
                                             return filename.endsWith(".properties");
                                         }
                                     }
                             );
                     
                     for (File propertyFile : allProperties) 
                     {//for each property file found
                         
                         Properties prop = ResourceManager.instance().loadProperty(resourcePath, propertyFile);
                         
                         val = prop.get( name );
                         
                         if ( val != null )
                         {// if you found the value stop searching and loading the files
                             return val;
                         }
                         
                     }
                 }
                 else if ( file.getAbsolutePath().endsWith(".properties") )
                 {
                     Properties prop = ResourceManager.instance().loadProperty(resourcePath, file);
                     
                     val = prop.get( name );
                     
                     if ( val != null )
                     {
                         return val;
                     }
                 }
                 
             }
             else
             {
                 Ln.w("Can not find resource path - %s", resourcePath );
             }
         }
         
         return val;
     }
     
     private Collection<File> listFiles( File directory, FilenameFilter fileFilter ) {
         //Find files
         Collection<File> files = new java.util.LinkedList<File>();
         innerListFiles(files, directory, fileFilter, false);
         return files;
     }

     private void innerListFiles(Collection<File> files, File directory, FilenameFilter filter, boolean includeSubDirectories) {
         File[] found = directory.listFiles(filter);
         
         if (found != null) {
             for (File file : found) {
                 if (file.isDirectory()) {
                     if (includeSubDirectories) {
                         files.add(file);
                     }
                     innerListFiles(files, file, filter, includeSubDirectories);
                 } else {
                     files.add(file);
                 }
             }
         }
     }
     
     public Properties loadProperty(String resourcePath, File propertyFile) {
         
         Properties property = new Properties();
         
         //Getting relative path to be able to load resource
         String absPath = propertyFile.getAbsolutePath();
         String relativePath = absPath.substring( absPath.indexOf( resourcePath ) );
         
         InputStream in = JavaResourceListener.class.getClassLoader().getResourceAsStream( relativePath );
         try
         {//and load the property file 
             if ( in != null )
             {
                 property.load( in );
                 
                 //get the current cache map for resource path
                 HashMap<String, Properties> props = properties.get( resourcePath );
                 
                 if ( props == null )
                 {//create a new map to put inside cache
                     props = new HashMap<String, Properties>();
                 }
                 
                 //put the new property inside the resource path cache map
                 props.put(propertyFile.getAbsolutePath(), property);
                 //put resource path cache map into main cache
                 properties.put(resourcePath, props);
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
