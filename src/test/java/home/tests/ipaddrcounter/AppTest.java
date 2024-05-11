package home.tests.ipaddrcounter;

import java.io.ByteArrayInputStream;
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
    
    @Test
    public void testBlock() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; ++i) {
            sb.append("1.2.3.").append(i).append('\n');
        }
        Object tree = App.readIPs(new ByteArrayInputStream(sb.toString().getBytes()));
        Assertions.assertEquals(256, App.countIPs(tree, 256L*256*256*256));
        Object[] treeArr = Assertions.assertInstanceOf(Object[].class, tree);
        treeArr = Assertions.assertInstanceOf(Object[].class, treeArr[1]);
        treeArr = Assertions.assertInstanceOf(Object[].class, treeArr[2]);
        // check if it's ALL_NODES object
        Assertions.assertEquals(App.ALL_NODES, treeArr[3]);
    }
    
    @Test
    public void testBlockTwo() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; ++i) {
            for (int j = 0; j < 256; ++j) {
                sb.append("1.2.").append(j).append('.').append(i).append('\n');
            }
        }
        Object tree = App.readIPs(new ByteArrayInputStream(sb.toString().getBytes()));
        Assertions.assertEquals(256*256, App.countIPs(tree, 256L*256*256*256));
        Object[] treeArr = Assertions.assertInstanceOf(Object[].class, tree);
        treeArr = Assertions.assertInstanceOf(Object[].class, treeArr[1]);
        // check if it's ALL_NODES object
        Assertions.assertEquals(App.ALL_NODES, treeArr[2]);
    }
}
