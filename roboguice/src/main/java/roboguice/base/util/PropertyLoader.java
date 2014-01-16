package roboguice.base.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import roboguice.base.util.logging.Ln;

public class PropertyLoader {

    /**
     * Loads a property file into specific ( or new ) {@link Properties} object
     * 
     * @param propertyFile The location of the property file to load
     * @param property ( Optional ) If set, the propertyFile will be loaded into this property
     * @return The {@link Properties} that was loaded, {@code null} if there was an error loading the property
     */
    public static Properties loadProperty( String propertyFile, Properties property, Comparator<URL> comparator )
    {
        boolean set = false;
        Properties tmp = property == null ? new Properties() : property;

        try 
        {
            Enumeration<URL> urls = PropertyLoader.class.getClassLoader().getResources( propertyFile );
            List<URL> allUrls = Collections.list(urls);
            if ( comparator != null )
            {
                Collections.sort(allUrls, comparator);
            }
        
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
        }
        catch ( IOException e1 ) 
        {
            Ln.e(e1);
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
