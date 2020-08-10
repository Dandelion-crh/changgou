package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: shenkunlin
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.fixs4DDUKJcGDieIspEZuINpuWV27B9a8tKb6uwgYWI1dNN8NW9vn3-o36MmqnOPO-ZUQznQwq1oyXSDZHSeE0JDfvl0xPsUsYp5zq0IT0bwJ1-YZPg1eZiNEIZdu3XkloyeVALBBga75Teez99Wwf30fv8RR5z05fWJJJmWWOLWqMUwxuA4ZJ-dRrvUroFVDFWRI2Wyh3LWJJYDeKxsHc9egtfkPpWKDarejuoV3nkoALvEqYkTUa2-ahCkZ95qwLyy7SoTSN5roz7b9j4saFDkwB6kGF45beTx8oAMVyPDIqoVJ0_OzUyiv3XLI7SVnxKPa2yVW4OApUg050fZIQ";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvdOHTyKQHDCT8N5UOgjE+LUlejSeQgRik4BW2CKgZUdYwjwcsYQeMImWs1uxh7gGMkpD2JJZoDPu/QQhraygmvZRDQxVwOwE02Vh6KqBWjjQQUrQFrAYf+n8EQBKIZEMl4F+JObRf8rIZgJvM12yUCW/vX7y73PwC98wly4zqrUyGS1QqdFTk3kUeVbIJl9LozugF/uR5hW7CUfxbfSezGYRdhLeCrKgKIrTJU0XZNpm3OolNjbqv+46Nj/OyjQPSqaK5SBUPgF/F6c+X2KfhhgXJe3m5DSZ37gk+Ej5QC51/Q/RRIEnRRUegiXvT/+EDANsoZss1b4pEEIsp14g/wIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
