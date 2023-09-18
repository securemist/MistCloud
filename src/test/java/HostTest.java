import com.mist.cloud.MistCloudApplication;
import io.swagger.models.auth.In;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.net.*;
import java.util.Enumeration;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 09:47
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MistCloudApplication.class)
public class HostTest {
    @Resource
    Environment environment;

    @Value(("${net.host}"))
    String host;

    @Test
    public void testHost() throws UnknownHostException, SocketException {
        System.out.println(host);
    }
}
