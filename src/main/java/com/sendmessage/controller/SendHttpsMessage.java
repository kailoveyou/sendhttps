package com.sendmessage.controller;

import com.sendmessage.utils.CertificateValidationIgnored;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * Created by Administrator on 2019/3/2.
 */

@RestController
@RequestMapping("/")
public class SendHttpsMessage {

    @PostConstruct
    public String sendUtilOne(){
        String osName = System.getProperties().getProperty("os.name");
        String url = "";
        if (osName.equals("Linux")) {
            System.out.println("linux");
            url = "https://172.16.190.124:8999/ddos/dispose/dispose_result";
        } else {
            System.out.println("other");
            url = "https://112.33.254.174:10046/ddos/dispose/alarminfo_dispose";
        }
        String data = "{\"data\":\"R60n5B9ateEIM+s5SXq9oyvFEkdlfXKZLcYgDYZl2c6dGKnO3ObvfB2QjYHJ8O6jA+o2xoPmA/jn\\r\\nEU9GERNYU9V2fvMSeVzY23JCa5X0WEFeJ2dLKIqiFk3cFsED1D71xFs1w7+8XNlFk1vSn/uhdDu+\\r\\neBKQkOTF2uk+NEpT1SBfQwV0OXwTi7kNWX2JwYt7L7+C518c813K9uViLYtHGJqv++Sz1DsmJntO\\r\\nRMiMfDnb1Dt1g5SzRgwvYumAm9z37/I/SWgP4UjIG6wQvBZB9h2YcgzhsYl+vdw43G1G2kkWpA9h\\r\\nCx5rMXccsdv5gxtsufwhdJ8BQ2q4WuuJGVtL7E4KhM4b4yBMmgd5wuewK/yB3pkcmbkQvKNjpk16\\r\\nTxY2IsgG/8OPoGaONiLbeypGgUx6p8yyWNCc/0ielBxzfcuDhxbRweu9Z/ipjKts6alANEh3JPR5\\r\\nsnkoyZp1c8fye6b0ZtVcp5tA+h/oXkFG7jrArE2URXVG0PuhGvnYprJmXZs98kQu4ZItQzE8D401\\r\\ntOS3sk+Jwh1YQ/9R3TbLGJM+DvqQFxShETECmbD3NI4zaivn26NmF+0UuvyEZTXlCidU7ytmn/C4\\r\\np8d67ZEIP18KRFhqmHx95D7OPBzhDM9HhoZSvH6BwkICjA+ckcIFbIgS5YE3KrKnEXpKNReWINU=\",\"provinceId\":\"ha\",\"timestamp\":1551494505245}";
        String back = "";
        try {
            back = CertificateValidationIgnored.sendHttpsPostjson(url, data, "UTF-8");
            System.out.println("返回值为:"+back);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return back;
    }


}
