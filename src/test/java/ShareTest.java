import com.mist.cloud.MistCloudApplication;
import com.mist.cloud.module.share.model.resp.ShareLinkResponse;
import com.mist.cloud.module.share.service.IShareContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.annotation.Resource;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 10:16
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MistCloudApplication.class)
public class ShareTest {

    @Resource
    IShareContext shareContext;

    @Test
    public void url_test() {
        ShareLinkResponse share = shareContext.createShare(null);
    }
}
