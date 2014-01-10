package roboguice.java.util;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import roboguice.base.util.logging.Ln;

public class ResourceManagerTest {

    @Before
    public void setup()
    {
        ResourceManager.instance().reset();
    }
    
    private Comparator<URL> comp = new Comparator<URL>() {

        @Override
        public int compare(URL lhs, URL rhs) {
            Ln.i("lhs %s", lhs);
            Ln.i("rhs %s", rhs);
            return lhs.toString().contains(".jar") ? 1 : -1;
        }
    };
    
    @Test
    public void testLoadProperties()
    {//there is a jar "test-response.jar" that contains a "path1/resone.properties" file
     //which servers as a second property file
        ResourceManager.instance().addResourcePath( "path1/resone.properties");
        
        assertThat( ResourceManager.instance().getValue("firstFile"), is ( equalTo ( "value" ) ) );
        assertThat( ResourceManager.instance().getValue("secondFile"), is ( equalTo ( "value" ) ) );
    }
    
    @Test
    public void testGetValueThatIsRepeated()
    {
        ResourceManager.instance().addResourcePath( "path2/restwo.properties");
        //value in path2 should be expected
        
        assertThat( ResourceManager.instance().getValue("secondFile"), is( equalTo( "the_better_value" ) ) );
    }
    
    @Test
    public void testGettingValueFromNewResource()
    {
        ResourceManager.instance().addResourcePath( "path1/resone.properties");
        assertThat( ResourceManager.instance().getValue("secondFile"), is ( equalTo ( "value" ) ) );
        
        ResourceManager.instance().addResourcePath( "path2/restwo.properties");
        //value in path2 should be expected
        
        String value = ResourceManager.instance().getValue("secondFile");
        assertThat( value, is( equalTo( "the_better_value" ) ) );
    }
    
    @Test
    public void testComparator()
    {
        ResourceManager.instance().addResourcePath( "path1/resone.properties");
        ResourceManager.instance().addComparator(comp);
        
        Ln.w( ResourceManager.instance().getValue("file") );
        
        assertThat(ResourceManager.instance().getValue("file"), is(equalTo("first")));
    }
}
