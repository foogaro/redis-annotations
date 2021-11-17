package com.foogaro.data.redis.fts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={FTSDetector.class})
public class SpringBootRedisFTSApplicationTests {

    @Test
    public void doTest() {
        System.out.println("Doom!");
        FTSDetector detector = new FTSDetector("com.foogaro.data.redis.fts");
    }
}
