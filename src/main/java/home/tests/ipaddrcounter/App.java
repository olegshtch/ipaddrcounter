/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package home.tests.ipaddrcounter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.zip.ZipFile;

public class App {

    // leaf of IPs trees means all children are set
    private static final Object ALL_NODES = new Object();
    
    // temp objects to reduce allocations
    private static BitSet BITSET_POOL = null;
    private static Object[] ARRAY_POOL = null;

    // Tree of IPs
    // null - no children are set
    // ALL_NODES - all childre are set
    // BitSet(256) - otherwise on the last 4th level
    // Object[256] - otherwise on the 1st-3rd levels
    private static Object TREE = null;
    
    public static Object[] getNewObjects() {
        if (ARRAY_POOL != null) {
            var result = ARRAY_POOL;
            ARRAY_POOL = null;
            Arrays.fill(result, null);
            return result;
        }
        return new Object[256];
    }
    
    public static BitSet getNewBitSet() {
        if (BITSET_POOL != null) {
            var result = BITSET_POOL;
            BITSET_POOL = null;
            result.set(0, 255, false);
            return result;
        }
        return new BitSet(256);
    }
    
    public static void insertIP(byte[] ip) {
        // root
        if (TREE == ALL_NODES) {
            return;
        }
        boolean noFresh = true; // if is's just created then don't check for compression
        Object[] treeArr;
        if (TREE instanceof Object[] arr) {
            treeArr = arr;
        } else if (TREE == null) {
            treeArr = getNewObjects();
            TREE = treeArr;
            noFresh = false;
        } else {
            return;
        }

        // level 1
        final int ip0 = ip[0] & 0xff;
        if (treeArr[ip0] == ALL_NODES) {
            return;
        }
        boolean noFresh1 = false; // if is's just created then don't check for compression
        Object[] treeArr1;
        if (treeArr[ip0] instanceof Object[] arr) {
            treeArr1 = arr;
        } else if (treeArr[ip0] == null) {
            treeArr1 = getNewObjects();
            treeArr[ip0] = treeArr1;
            noFresh1 = true;
        } else {
            return;
        }
        
        // level 2
        final int ip1 = ip[1] & 0xff;
        if (treeArr1[ip1] == ALL_NODES) {
            return;
        }
        boolean noFresh2 = false; // if is's just created then don't check for compression
        Object[] treeArr2;
        if (treeArr1[ip1] instanceof Object[] arr) {
            treeArr2 = arr;
        } else if (treeArr1[ip1] == null) {
            treeArr2 = getNewObjects();
            treeArr1[ip1] = treeArr2;
            noFresh2 = true;
        } else {
            return;
        }
        
        // leaves
        final int ip2 = ip[2] & 0xff;
        if (treeArr2[ip2] == ALL_NODES) {
            return;
        }
        boolean noFresh3 = true; // if is's just created then don't check for compression
        BitSet treeArr3;
        if (treeArr2[ip2] instanceof BitSet arr) {
            treeArr3 = arr;
        } else if (treeArr2[ip2] == null) {
            treeArr3 = getNewBitSet();
            treeArr2[ip2] = treeArr3;
            noFresh3 = true;
        } else {
            return;
        }
        final int ip3 = ip[3] & 0xff;
        treeArr3.set(ip3);
        
        // compress
        if (noFresh3 && treeArr3.cardinality() == 256) {
            BITSET_POOL = treeArr3;
            treeArr2[ip2] = ALL_NODES;
        } else {
            return;
        }

        if (noFresh2) {
            for (int i = 0; i < 256; i ++) {
                if (treeArr2[i] != ALL_NODES) {
                    return;
                }
            }
            ARRAY_POOL = treeArr2;
            treeArr1[ip1] = ALL_NODES;
        } else {
            return;
        }
        
        if (noFresh1) {
            for (int i = 0; i < 256; i ++) {
                if (treeArr1[i] != ALL_NODES) {
                    return;
                }
            }
            ARRAY_POOL = treeArr1;
            treeArr[ip0] = ALL_NODES;           
        } else {
            return;
        }
        
        if (noFresh) {
            for (int i = 0; i < 256; i ++) {
                if (treeArr[i] != ALL_NODES) {
                    return;
                }
            }
            TREE = ALL_NODES;
        }
    }
    
    public static void readIPs(InputStream is) throws IOException {
        try (var bis = new BufferedInputStream(is)) {
            int currentByte = 0;
            byte[] bytes = new byte[4];

            int ch;
            while ((ch = bis.read()) != -1) {
                if (ch == (int)'.') {
                    currentByte++;
                } else if (ch == (int)'\n') {
                    // ToDo: process IP
                    //System.out.println("Line: " + (bytes[0] & 0xff) + "." + (bytes[1] & 0xff) + "." + (bytes[2] & 0xff) + "."+ (bytes[3] & 0xff));
                    insertIP(bytes);
                    currentByte = 0;
                    bytes[0] = bytes[1] = bytes[2] = bytes[3] = 0;
                } else if (ch >= (int)'0' && ch <= (int)'9') {
                    bytes[currentByte] = (byte)(bytes[currentByte] * 10 + (ch - (int)'0'));
                }
            }
        }
    }
    
    public static long countIPs(Object tree, long all_nodes_count) {
        if (tree == ALL_NODES) {
            return all_nodes_count;
        }
        if (tree == null) {
            return 0L;
        }
        long sum = 0;
        if (tree instanceof Object[] arr) {
            for (int i = 0; i < 256; i++) {
                sum += countIPs(arr[i], all_nodes_count / 256);
            }
        } else if (tree instanceof BitSet set) {
            sum += set.cardinality();
        }
        return sum;
    }
    
    public static void main(String[] args) throws IOException {
        try (var zip = new ZipFile(new File(args[0]), ZipFile.OPEN_READ)) {
            var entries = zip.entries();
            if (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                try (var is = zip.getInputStream(entry)) {
                    readIPs(is);
                }
                System.out.println("Count: " + countIPs(TREE, 256L*256*256*256));
            }
        }
    }
}
