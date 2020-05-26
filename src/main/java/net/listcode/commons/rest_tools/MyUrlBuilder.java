package net.listcode.commons.rest_tools;

import net.listcode.commons.Fn;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自己写的url构造器
 * 根据一个url生成基本部分; 可以设置query参数, 可以赋值路径变量
 *
 * TODO: 考虑默认的urlEncoding 字符集问题等
 * 注意!! 复杂的url不要使用这个类,比如里边带了特定浏览器才能是别的特殊字符等
 *
 * @author leo
 */
public class MyUrlBuilder {
//    {
//        UriBuilder builder = UriComponentsBuilder.fromUriString("http://www.baidu.com/{aaa}/{bbb}index.html");
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("a",  "1");
//        map.add("b",  "1");
//        builder.queryParams(map);
//        Map<String, String > vars = new HashMap<>();
//
//        {
//            vars.put("aaa", "AAA");
//            vars.put("bbb", "BBB");
//        }
//        String uriString = builder.build(vars).toString();
//        System.out.println(uriString);
//    }


    private UriBuilder inner;
    private boolean usePrefix = false;
    private Map<String, String> vars = new HashMap<>();

    private MyUrlBuilder(UriBuilder builder){
        this.inner = builder;
    }

    /**
     * 从一个全url生成, url中可以带path变量
     * @param uriString
     * @return
     */
    public static MyUrlBuilder fromUriString(String uriString){
        UriBuilder builder = UriComponentsBuilder.fromUriString(uriString);
        return new MyUrlBuilder(builder);
    }

    /**
     * 从一个url前缀生成, 必须包含协议头和一个完整域名(ip:port也可以)
     * @param uriPrefixString
     * @return
     */
    public static MyUrlBuilder fromUriPrefix(String uriPrefixString){
        UriBuilder inner = UriComponentsBuilder.fromUriString(uriPrefixString);
        MyUrlBuilder myUrlBuilder = new MyUrlBuilder(inner);
        myUrlBuilder.usePrefix = true;
        return myUrlBuilder;
    }

    /**
     * 前缀模式生成时,可以调用此方法, 跟前缀拼成完整url,
     * @param path
     * @return
     */
    public MyUrlBuilder addPath(String path) {
        if (!this.usePrefix) {
            throw new IllegalStateException("MyUrLBuilder must created by 'MyUrLBuilder.fromUriPrefix' method!");
        }
        this.inner.path(path);
        return this;
    }

    /**
     * 添加单个请求参数, 参数可以多值
     * @param key
     * @param values
     * @return
     */
    public MyUrlBuilder addParam(String key, Object... values) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.addAll(key, Fn.map(Arrays.asList(values), Object::toString));
        this.inner.queryParams(map);

        return this;
    }

    /**
     * 添加多个请求参数, 参数只能单值
     * @param params
     * @return
     */
    public MyUrlBuilder addParams(Map<String, Object> params) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (String k : params.keySet()) {
            map.add(k, params.get(k).toString());
        }
        this.inner.queryParams(map);

        return this;
    }

    /**
     * 添加多个请求参数, 参数只能多值
     * @param params
     * @return
     */
    public MyUrlBuilder addParamsWithMuti(Map<String, List<Object>> params) {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (String k : params.keySet()) {
            map.addAll(k, Fn.map(params.get(k), Object::toString));
        }
        this.inner.queryParams(map);

        return this;
    }

    /**
     * 直接拿一个bean的所有属性作为请求参数
     * @param paramsBean
     * @return
     */
    public MyUrlBuilder addParamsWithBean(Object paramsBean) {
        BeanMap bm = new BeanMap(paramsBean);

        Map<String, Object> paramsMap = Fn.toMap(bm.entrySet(),
                x -> x.getValue().toString(),
                x -> x.getValue() == null ? "" : x.getValue().toString());
        return this.addParams(paramsMap);
    }

    /**
     * 为路径中path变量赋值 ,如 http://a.com/{coverId} 变为  http://a.com/1
     * @param key
     * @param var
     * @return
     */
    public MyUrlBuilder setPathVar(String key, Object var) {
        this.vars.put(key, var.toString());
        return this;
    }

    /**
     * 为路径中path多个变量赋值
     * @param params
     * @return
     */
    public MyUrlBuilder setPathVars(Map<String, Object> params) {
        for(String k : params.keySet()) {
            this.vars.put(k, params.get(k).toString());
        }

        return this;
    }

    /**
     * 拼成最终可用的url
     * @return
     */
    public String toUrlString() {
        return this.inner.build(this.vars).toString();
    }

    /**
     * 拼成最终可用的url
     * @return
     * @throws MalformedURLException
     */
    public URL toURL() throws MalformedURLException {
        return this.inner.build(this.vars).toURL();
    }

}
