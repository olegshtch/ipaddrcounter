package home.tests.ipaddrcounter;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link App}
 *
 * @author olegshtch
 */
public final class AppTest {
    
    @Test
    public void testSingle() throws IOException {
        Object tree = App.readIPs(AppTest.class.getResourceAsStream("/single.txt"));
        Assertions.assertEquals(1, App.countIPs(tree, 256L*256*256*256));
    }
    
    @Test
    public void testFew() throws IOException {
        Object tree = App.readIPs(AppTest.class.getResourceAsStream("/few.txt"));
        Assertions.assertEquals(2, App.countIPs(tree, 256L*256*256*256));
    }
}
