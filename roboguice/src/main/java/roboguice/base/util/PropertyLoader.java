package roboguice.base.util;

import java.io.InputStream;
import java.net.URL;
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
    public static Properties loadProperty( String propertyFile, Properties property )
    {
        boolean set = false;
        Properties tmp = property == null ? new Properties() : property;

        InputStream in = PropertyLoader.class.getResourceAsStream(propertyFile);
        try 
        {// and load the property file
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
