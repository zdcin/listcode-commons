package net.listcode.commons.rest_tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;
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
public class RestClientFactoryBackUp {

    //     *  @param baseUri baseUri作用不知道是啥
    //     *  @param defaultUriVariables 这里是路径参数,不是请求中的kv参数, kv参数可能要用uribuilder来构建
    //     *  @param requestCustomizers 请求修改器, 不知道什么用
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
                                         //@Nullable String baseUri,
                                         // @Nullable Map<String, ?> defaultUriVariables,
                                         @Nullable Map<String, List<String>> defaultHeaders,
                                         //@Nullable Set<RestTemplateRequestCustomizer<?>> requestCustomizers,
                                         @Nullable Supplier<Map<String, List<String>>> dynamicHeaderSupplier
                                         ) {
        RestTemplateBuilder builder = new RestTemplateBuilder();

        builder.setConnectTimeout(Duration.ofMillis(connectionTimeoutOfMs))
                .setReadTimeout(Duration.ofMillis(readTimeoutOfMs));
//        if (baseUri != null) {
//            builder.uriTemplateHandler(new DefaultUriBuilderFactory(baseUri));
//        }
//        if (defaultHeaders != null) {
//            for (String k : defaultHeaders.keySet()) {
//                List<String> v = defaultHeaders.get(k);
//                builder.defaultHeader(k, new String[v.size()]);
//            }
//        }

        //添加请求修改器 和 动态header生成器, 执行时动态修改header
//        {
//
//            Set<RestTemplateRequestCustomizer<?>> all = new HashSet<>();
//            if (requestCustomizers != null) {
//                all.addAll(requestCustomizers);
//            }
//            if (dynamicHeaderSupplier != null) {
//                RestTemplateRequestCustomizer temp = request -> {
//                    log.info("run dynamicHeaderSupplier...");
//                    System.out.println("run dynamicHeaderSupplier...");
//                    Map<String, List<String>> dynamicHeaders = dynamicHeaderSupplier.get();
//                    HttpHeaders hs = request.getHeaders();
//                    if (dynamicHeaders != null) {
//                        for (String k : dynamicHeaders.keySet()) {
//                            List<String> v = dynamicHeaders.get(k);
//                            hs.replace(k, v);
//                        }
//                    }
//                };
//                all.add(temp);
//            }
//            builder.requestCustomizers(all);
//        }

        RestTemplate rest = builder.build();
//        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
//        uriBuilderFactory.builder()
//        rest.

        if (defaultHeaders != null) {


            rest.getClientHttpRequestInitializers().add(new ClientHttpRequestInitializer() {
                @Override
                public void initialize(ClientHttpRequest request) {
                    log.info("run defaultHeaders..."  + request.hashCode());
                    System.out.println("run defaultHeaders...");
                    HttpHeaders hs = request.getHeaders();


                    for (String k : defaultHeaders.keySet()) {
                        List<String> v = defaultHeaders.get(k);
//                        builder.defaultHeader(k, new String[v.size()]);
                        hs.addAll(k, v);
                    }
                    System.out.println("hs3=" + hs.toString());
                }
            });
        }

        if (dynamicHeaderSupplier != null) {
            rest.getClientHttpRequestInitializers().add(new ClientHttpRequestInitializer() {
                @Override
                public void initialize(ClientHttpRequest request) {
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
                }
            });
        }



//        if (defaultUriVariables != null) {
//            rest.setDefaultUriVariables(defaultUriVariables);
//        }
        return rest;
    }

//    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
//
//        //具备动态header能力,  在实现一个自定义的ClientHttpRequestInitializer ,既可以自己控制header了,
////        rest.getClientHttpRequestInitializers().removeIf(上次有效的请求初始器);
////        rest.getClientHttpRequestInitializers().add(仅本次有效的请求初始器);
//        /*
//        第二种控制动态header方式, RestTemplate使用线程绑定模式, 赋值一个自定义请求初始器
//        自定义的请求初始器使用threadLocal方式加载动态header, 加入header元素过期机制, 或者每次都新生成
//
//         */
//        RestTemplateBuilder builder = new RestTemplateBuilder();
//        builder.
//        builder.rootUri(null).additionalCustomizers()
//        //设置超时时间, chunkSize等
//        RestTemplate rest = new RestTemplate(factory);
//
//        //设置baseUrl,使用时只需要填入path,
//        DefaultUriBuilderFactory f = new DefaultUriBuilderFactory("https://baidu.com");
//        f.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
//        //默认路径参数
//        f.setDefaultUriVariables(new HashMap<>());
//
//        rest.setUriTemplateHandler(f);
//
//
//        //TODO 设置默认header, baseAtuh, 等
//        ClientHttpRequestInitializer x = new RestTemplateBuilderClientHttpRequestInitializer() ;
//
//        rest.setClientHttpRequestInitializers(x);
//
//        return  rest;
//    }
//
//    @Bean
//    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setReadTimeout(5000);//单位为ms
//        factory.setConnectTimeout(5000);//单位为ms
//        return factory;
//    }
//
//    public static void main(String[] args) {
//        TestRestTemplate o = new TestRestTemplate();
//        RestTemplate rest = o.restTemplate(o.simpleClientHttpRequestFactory());
//
//
//        rest.getForEntity("http://baidu.com")
//    }
}
