package roboguice.base.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import roboguice.base.util.logging.Ln;

public class PropertyLoader {

    /**
     * Returns a list of {@link URL}s return from {@link ClassLoader#getResources(String)}
     * sorted by the comparator given. If no comparator is given, order is not guaranteed.
     * 
     * @param resource The path of the resource to look for
     * @param comparator The comparator to sort the resources by
     * @return A list of URLs sorted by the given comparator
     */
    public static List<URL> urlsFor( String resource, Comparator<URL> comparator )
    {
        return urlsFor( resource, comparator, PropertyLoader.class );
    }

    /**
     * Returns a list of {@link URL}s return from {@link ClassLoader#getResources(String)}
     * sorted by the comparator given. If no comparator is given, order is not guaranteed.
     * 
     * @param resource The path of the resource to look for
     * @param comparator The comparator to sort the resources by
     * @param clazz ( Optional ) If set, resources will be searched for with this class
     * @return A list of URLs sorted by the given comparator
     */
    public static List<URL> urlsFor( String resource, Comparator<URL> comparator, Class<?> clazz )
    {
        if ( clazz == null )
        {
            clazz = PropertyLoader.class;
        }
        
        List<URL> allUrls = new ArrayList<URL>();
        
        try {
            Enumeration<URL> urls = clazz.getClassLoader().getResources( resource );
            
            if ( urls != null )
            {
                try
                {
                    while (urls.hasMoreElements()) {
                        URL url = (URL) urls.nextElement();

                        if ( url != null )
                        {
                            allUrls.add( url );
                        }
                    }
                } 
                catch (NullPointerException e)
                {
                    Ln.w(e, "Error getting all URLs");
                }
            }
            
            
            if ( comparator != null )
            {
                Collections.sort(allUrls, comparator);
            }
            
        } catch (IOException e) {
            Ln.e( e );
        }
        
        return allUrls;
    }
    
    /**
     * Loads a property file into specific ( or new ) {@link Properties} object
     * 
     * @param propertyFile The location of the property file to load
     * @param property ( Optional ) If set, the propertyFile will be loaded into this property
     * @param comparator (Optional ) If set, properties will be sorted by this comparator
     * @return The {@link Properties} that was loaded, {@code null} if there was an error loading the property
     */
    public static Properties loadProperty( String propertyFile, Properties property, Comparator<URL> comparator )
    {
       return loadProperty(propertyFile, property, comparator, PropertyLoader.class );
    }

    /**
     * Loads a property file into specific ( or new ) {@link Properties} object
     * 
     * @param propertyFile The location of the property file to load
     * @param property ( Optional ) If set, the propertyFile will be loaded into this property
     * @param comparator (Optional ) If set, properties will be sorted by this comparator
     * @param clazz (Optional ) Used to load properties
     * @return The {@link Properties} that was loaded, {@code null} if there was an error loading the property
     */
    public static Properties loadProperty( String propertyFile, Properties property, Comparator<URL> comparator, Class<?> clazz )
    {
        if ( clazz == null )
        {
            clazz = PropertyLoader.class;
        }
        
        boolean set = false;
        Properties tmp = property == null ? new Properties() : property;
        
        List<URL> allUrls = urlsFor( propertyFile, comparator );
        
        for( URL url : allUrls)
        {//for each url
            
            InputStream in = null;
            try 
            {//and load the property file
                in = url.openStream();
                
                if ( in != null )
                {
                    tmp.load( in );
                    set = true;
                } 
                else 
                {
                    Ln.w( "Could not find [%s] resource - can not inject any resources in specified file.", propertyFile );
                }
            } 
            catch (Exception e) 
            {
                Ln.e( e, "Error loading property file [%s]", propertyFile );
            } 
            finally 
            {
                try 
                {
                    in.close();
                } catch (Exception ex) 
                {
                    // ignore
                }
            }
        }
        
        return set ? tmp : null;
    }

    /**
     * Loads a property file into specific ( or new ) {@link Properties} object
     * 
     * @param propertyFile The {@link URL} of the property file to load
     * @param property ( Optional ) If set, the propertyFile will be loaded into this property
     * @return The {@link Properties} that was loaded, {@code null} if there was an error loading the property
     */
    public static Properties loadProperty( URL propertyFile, Properties property )
    {
        boolean set = false;
        Properties tmp = property == null ? new Properties() : property;
        InputStream in = null;
        
        try 
        {// and load the property file
            in = propertyFile.openStream();
            
            if ( in != null ) 
            {
                tmp.load( in );
                set = true;
            } 
            else 
            {
                Ln.w( "Could not find [%s] resource - can not inject any resources in specified file.", propertyFile );
            }
        } 
        catch (Exception e) 
        {
            Ln.e( e, "Error loading property file [%s]", propertyFile );
        } 
        finally 
        {
            try 
            {
                in.close();
            } catch (Exception ex) 
            {
                // ignore
            }
        }
        
        return set ? tmp : null;
    }
}
