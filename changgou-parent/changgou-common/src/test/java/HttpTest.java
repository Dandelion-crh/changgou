import com.changgou.util.HttpClient;
import org.junit.Test;

import java.io.IOException;

/**
 * 类名: HttpTest
 * 作者: crh
 * 日期: 2020/6/16 0016上午 7:35
 */
public class HttpTest {

    @Test
    public void httpTests(){
        HttpClient client = new HttpClient("https://www.baidu.com");
        client.setHttps(true);
        try {
            client.get();
            String result = client.getContent();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
