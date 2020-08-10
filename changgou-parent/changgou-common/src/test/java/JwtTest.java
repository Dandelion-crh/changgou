import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名: JwtTest
 * 作者: crh
 * 日期: 2020/6/10 0010下午 9:04
 */
public class JwtTest {

    /****
     * 创建Jwt令牌
     */
    @Test
    public void testCreateJwt(){
        JwtBuilder builder = Jwts.builder()
                .setId("888")            //设置唯一编号
                .setSubject("小白")      //设置主题 可以JSON数据
                .setIssuedAt(new Date()) //设置签发日期
                //.setExpiration(new Date()) //过期时间设置
                .signWith(SignatureAlgorithm.HS256,"itcast");  //设置签名 使用HS25算法.并且设置

        //自定义载荷
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name","王五");
        userInfo.put("age",27);
        userInfo.put("address","深圳黑马训练营");
        builder.addClaims(userInfo);

        //Secretkey(字符串) 构建并返回一个字符串
        System.out.println(builder.compact());
    }

    /**
     *解析JWT令牌数据
     */
    @Test
    public void testParseJwt(){
    //没设置过期时间得出的token，运行正常
    //String compactJwt="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiLlsI_nmb0iLCJpYXQiOjE1OTE3OTQ2NTF9.ICtQFEMQBBa_Fn-a_dKJC6Ujkez4_eakg3XL11XIfYg";
    //设置完过期时间后得出下面的token，运行将会报错
    //String compactJwt="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiLlsI_nmb0iLCJpYXQiOjE1OTE3OTUxNjIsImV4cCI6MTU5MTc5NTE2Mn0.uud5t7jw-dNiO33SgfGk_QpwWPz-yUGwJvqPiJYt79k";
    //加入自定义载荷
    String compactJwt="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiLlsI_nmb0iLCJpYXQiOjE1OTE3OTcyNjQsImFkZHJlc3MiOiLmt7HlnLPpu5Hpqazorq3nu4PokKUiLCJuYW1lIjoi546L5LqUIiwiYWdlIjoyN30.vyUL-BBwwYyy_nzXj_R9bkgGtOpRemvsH3hPH7zWsoc";

        Claims claims = Jwts.parser()
                .setSigningKey("itcast")
                .parseClaimsJws(compactJwt)
                .getBody();
        System.out.println(claims);
    }
}
