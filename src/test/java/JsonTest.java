import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.annotations.Lang;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 08:09
 * @Description:
 */
@SpringBootTest

public class JsonTest {

    @Test
    public void jsonTest() {

        ArrayList<Long> list = new ArrayList<>();
        list.add(123L);
        list.add(124L);
        list.add(125L);
        String jsonString = JSON.toJSONString(list);

        System.out.println(jsonString);

        JSONArray objects = JSON.parseArray(jsonString);
        System.out.println(objects);
    }
}
