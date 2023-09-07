import cn.hutool.crypto.digest.MD5;
import org.junit.Test;

/**
 * @Author: securemist
 * @Datetime: 2023/9/7 19:42
 * @Description:
 */
public class Md5Test {

    @Test
    public void md5Test() {
        String string1 =new MD5().digestHex("1234");
        System.out.println(string1.toString());
        String string = new MD5().digestHex("12345");
        System.out.println(string);
        System.out.println(string1.equals(string) );

    }

}
