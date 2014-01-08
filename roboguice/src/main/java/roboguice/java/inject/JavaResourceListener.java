package roboguice.java.inject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import roboguice.base.inject.InjectResource;
import roboguice.base.inject.ResourceListener;
import roboguice.base.util.logging.Ln;

import com.google.inject.TypeLiteral;

public class JavaResourceListener extends ResourceListener {

    private List<String> resourcePaths;
    
    /* Key represents an entry in a resourcePath
    *  Value -
    *           Key     - path to properties file
    *           Value   - properties object 
    */      
    private static final Map<String, HashMap<String,Properties>> properties = Collections.synchronizedMap( new HashMap<String, HashMap<String,Properties>>());
    
    public JavaResourceListener ( String... resourcePaths )
    {
        //Creating new list so that paths can be modified
        this.resourcePaths = resourcePaths != null && resourcePaths.length > 0 ?
                                Collections.synchronizedList( new ArrayList<String>( Arrays.asList( resourcePaths ) ) ) :
                                Collections.synchronizedList( new ArrayList<String>() ) ;
    }
    
    @Override
    protected <I> ResourceMemberInjector<I> newResourceMember( TypeLiteral<I> typeLiteral, Field field ) {
        return new JavaMemberResourceInjector<I>(field, field.getAnnotation(InjectResource.class), resourcePaths );
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected ResourceMemberInjector newResourceMember( Field field ) {
        return new JavaMemberResourceInjector( field, field.getAnnotation(InjectResource.class), resourcePaths );
    }
    
    public JavaResourceListener addResourcePath( String... paths )
    {
        if ( paths != null && paths.length > 0 )
        {
            resourcePaths.addAll( resourcePaths );
            //don't add resource paths to map yet
            //Lazy init in JavaMemberResourceInjector
        }
        
        return this;
    }
    
    public JavaResourceListener removeResourcePath( String... paths )
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
    
    public JavaResourceListener removeAllPaths()
    {
        resourcePaths.clear();
        return this;
    }
    
    protected static class JavaMemberResourceInjector<T> extends ResourceMemberInjector<T>
    {
        private List<String> resourcePaths;
        
        public JavaMemberResourceInjector(Field field, InjectResource annotation, List<String> resourcePaths ) {
            super(field, annotation);
            this.resourcePaths = resourcePaths;
        }

        @Override
        protected Object getValue() {
            
            Object val = null;
            //Check cache first
            for ( String resourcePath : properties.keySet() )
            {//for every resource path
                
                Map<String, Properties> props = properties.get( resourcePath );
                for ( String propertyFile : props.keySet() )
                {//for each property file 
                    
                    //get property
                    val = props.get(propertyFile).getProperty( annotation.name() );
                    
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
                            
                            Properties prop = loadProperty(resourcePath, propertyFile);
                            
                            val = prop.get( annotation.name() );
                            
                            if ( val != null )
                            {// if you found the value stop searching and loading the files
                                return val;
                            }
                            
                        }
                    }
                    else if ( file.getAbsolutePath().endsWith(".properties") )
                    {
                        Properties prop = loadProperty(resourcePath, file);
                        
                        val = prop.get(annotation.name() );
                        
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

        private Properties loadProperty(String resourcePath, File propertyFile) {
            
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
        
    }

}
