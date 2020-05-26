package net.listcode.commons.rest_tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * 跟MyUrlBuilder 一起配合可以做到设置超时, 静态header, 动态header, path变量拼装, 请求参数拼装(可以是kv,也可以是bean)等
 * @author leo
 */
@Slf4j
public class RestClientFactory {

    /**
     *  此方法返回的restTemplate对象不能保证线程安全,需要结合header,动态header, requestCustomizers等参数综合判定
     *  想要当做线程安全使用,又有一定的动态性,可以合理使用dynamicHeaderSupplier实现
     *  也可以使用线程绑定的get方法
     *
     *  @param readTimeoutOfMs
     *  @param connectionTimeoutOfMs
     *  @param defaultHeaders 已经可以生效
     *  @param dynamicHeaderSupplier 动态header生成器, 可用于生成每次变化的token, 定期变化的cookie等
     */
    public static RestTemplate getClient(long readTimeoutOfMs,
                                         long connectionTimeoutOfMs,
                                         @Nullable Map<String, List<String>> defaultHeaders,
                                         @Nullable Supplier<Map<String, List<String>>> dynamicHeaderSupplier
                                         ) {
        // builder 的api太烂了, 这些set方法是无副作用的,会返回新对象, 不能原地改
        RestTemplateBuilder builder = new RestTemplateBuilder();

        //超时设置
        builder = builder.setConnectTimeout(Duration.ofMillis(connectionTimeoutOfMs));
        builder= builder.setReadTimeout(Duration.ofMillis(readTimeoutOfMs));
        if (defaultHeaders != null) {
            for (String k : defaultHeaders.keySet()) {
                List<String> v = defaultHeaders.get(k);
                String[] arr = (String[]) v.toArray();
                builder = builder.defaultHeader(k, arr);
            }
        }

        RestTemplate rest = builder.build();

//        RestTemplate rest = factory.

        // 默认header,通过别的方式设置不成功,通过动态请求修改器实现
//        if (defaultHeaders != null) {
//            rest.getClientHttpRequestInitializers().add(request -> {
//                log.info("run defaultHeaders..."  + request.hashCode());
//                System.out.println("run defaultHeaders...");
//                HttpHeaders hs = request.getHeaders();
//
//
//                for (String k : defaultHeaders.keySet()) {
//                    List<String> v = defaultHeaders.get(k);
//                    hs.addAll(k, v);
//                }
//                System.out.println("hs3=" + hs.toString());
//            });
//        }

        // 动态header
        if (dynamicHeaderSupplier != null) {
            rest.getClientHttpRequestInitializers().add(request -> {
                log.info("run ClientHttpRequestInitializer..."  + request.hashCode());
                System.out.println("run ClientHttpRequestInitializer...");
                HttpHeaders hs = request.getHeaders();
                System.out.println("hs=" + hs.hashCode() + "," + hs.toString());
                Map<String, List<String>> dynamicHeaders = dynamicHeaderSupplier.get();
                if (dynamicHeaders != null) {
                    for (String k : dynamicHeaders.keySet()) {
                        List<String> v = dynamicHeaders.get(k);
                        hs.put(k, v);
                    }
                }
                System.out.println("hs2=" + hs.toString());
            });
        }

        return rest;
    }

}
