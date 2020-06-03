package net.listcode.commons.rest_tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class TestRestTools {

    private static String testUriBuilder() {
        UriBuilder builder = UriComponentsBuilder.fromUriString("http://www.baidu.com/{aaa}/{bbb}index.html");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("a",  "1");
        map.add("b",  "1");
        builder.queryParams(map);
        Map<String, String> vars = new HashMap<>();

        {
            vars.put("aaa", "AAA");
            vars.put("bbb", "BBB");
        }
        String uriString = builder.build(vars).toString();
        System.out.println(uriString);
        return uriString;
    }
    private static void testUriBuilder2() {
        MyUrlBuilder builder = MyUrlBuilder.fromUriString("http://www.baidu.com/{aaa}/{bbb}index.html")
                .addParam("a", 1)
                .addParam("b", "2")
                .setPathVar("aaa", "AAA")
                .setPathVar("bbb", 999);

        System.out.println(builder.toUrlString());
    }

    private static void testUriBuilder3() {
        //使用bean作为参数
        {
            MyUrlBuilder baiduPrefixBuilder = MyUrlBuilder.fromUriPrefix("https://www.baidu.com");
            baiduPrefixBuilder.addPath("/index.html");
            baiduPrefixBuilder.addParamsWithBean(new User(1, "SU&LI"));
            String s = baiduPrefixBuilder.toUrlString();
            System.out.println("baidu prefix=" + s);
        }
    }
    public static void main(String[] args) {
        testUriBuilder();
        testUriBuilder2();
        testUriBuilder3();

        //1. 动态header, 可以根据token生成函数自动更新每次调用的token,cookie等
        Supplier<Map<String, List<String>>> dynamicHeaderSupplier = () -> {
            Map<String, List<String>> map = new HashMap<>();
            map.put("a1", Arrays.asList("1"));
            map.put("a2", Arrays.asList("1"));
            map.put("h2", Arrays.asList("2"));
            return map;
        };
        //2. 默认header,可以每次自动填入header信息, 如userAgent等
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("h1", Arrays.asList("1"));
        headers.put("h2", Arrays.asList("1"));

        //3. 设置超时和header
        RestTemplate c = RestClientFactory.getClient(100L, 100L,
                headers,
                dynamicHeaderSupplier);
        //4. 根据urlBuilder, 生成url, 可以支持前缀, 路径参数, 请求参数
        String res = c.getForObject("http://admin.aixuexi.com/diy/to_publisher/cover/28", String.class);
        System.out.println(res);

        res = c.getForObject("http://1admin.aixuexi.com/diy/to_publisher/cover/28", String.class);
        System.out.println(res);

    }
    @Data
    @AllArgsConstructor
    public static class User{
        private int id;
        private String name;
    }
}
