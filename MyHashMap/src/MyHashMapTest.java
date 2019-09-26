import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyHashMapTest {
    @Test
    public void test(){
        int MAX_SIZE = 20000000;
        int[] keys = new int[MAX_SIZE];
        int[] values = new int[MAX_SIZE];

        Random random = new Random();
        for (int i = 0; i < keys.length; i++) {
            keys[i] = random.nextInt();
            values[i] = random.nextInt();
        }
        System.out.println("数据生成完毕，开始测试");
        System.out.println("当前测试：MyHashMap");

        long start = 0, end = 0;

        MyHashMap<Integer, Integer> mhm = new MyHashMap<>();

        start = System.currentTimeMillis();
        for (int i = 0; i < MAX_SIZE; i++) {
            mhm.put(keys[i], values[i]);
        }
        end = System.currentTimeMillis();
        System.out.println("插入2kw耗时：" + (end - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < MAX_SIZE; i++) {
            mhm.get(keys[i]);
        }
        end = System.currentTimeMillis();
        System.out.println("查询2kw耗时：" + (end - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < MAX_SIZE; i++) {
            mhm.remove(keys[i]);
        }
        end = System.currentTimeMillis();
        System.out.println("删除2kw耗时：" + (end - start));
    }

    @Test
    public void test2(){
        int MAX_SIZE = 20000000;
        int[] keys = new int[MAX_SIZE];
        int[] values = new int[MAX_SIZE];

        Random random = new Random();
        for (int i = 0; i < keys.length; i++) {
            keys[i] = random.nextInt();
            values[i] = random.nextInt();
        }
        System.out.println("数据生成完毕，开始测试");
        System.out.println("当前测试：标准库HashMap");

        long start = 0, end = 0;

        HashMap<Integer, Integer> mhm = new HashMap<>();

        start = System.currentTimeMillis();
        for (int i = 0; i < MAX_SIZE; i++) {
            mhm.put(keys[i], values[i]);
        }
        end = System.currentTimeMillis();
        System.out.println("插入2kw耗时：" + (end - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < MAX_SIZE; i++) {
            mhm.get(keys[i]);
        }
        end = System.currentTimeMillis();
        System.out.println("查询2kw耗时：" + (end - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < MAX_SIZE; i++) {
            mhm.remove(keys[i]);
        }
        end = System.currentTimeMillis();
        System.out.println("删除2kw耗时：" + (end - start));
    }

    @Test
    public void Test3(){
        test();
        test2();
    }

}
