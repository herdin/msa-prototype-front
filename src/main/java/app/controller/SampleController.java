package app.controller;

import app.dto.request.UserReqeust;
import app.service.UserService;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/*
TODO
    - RestTemplate 을 사용한 서비스 연동
    - 더 나은 RestTemplate 사용
        - https://taetaetae.github.io/2020/03/22/better-rest-template-1-retryable/
*/
@RestController
public class SampleController {
    Logger logger = LoggerFactory.getLogger(SampleController.class);

    @Autowired
    UserService userService;

    @Autowired
    RestTemplate restTemplate;

    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<String> exceptionHandler(UnknownHostException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @GetMapping("/hello")
    public String hello() throws UnknownHostException {
        return "hello, " + InetAddress.getLocalHost().toString();
    }

    @GetMapping("/remoteHello")
    public String retryHello(String url) throws URISyntaxException {
        logger.debug("remote -> {}", url);
        ResponseEntity<String> responseEntity = restTemplate.exchange(new URI(url), HttpMethod.GET, null, String.class);
        return responseEntity.getBody();
    }

    //회원가입
    @PostMapping("/user/{id}/{name}")
    public ResponseEntity<UserReqeust> addUser(@PathVariable  int id, @PathVariable  String name) {
        logger.debug("this is recv -> {}, {}", id, name);
        //회원 insert
        ///
        boolean isOk = userService.addUser(new UserReqeust());
        if(isOk) {
            UserReqeust userReqeust = new UserReqeust(id+1, name);
            return ResponseEntity.ok(userReqeust);
//            return ResponseEntity.status(200).body()
        } else {
            return ResponseEntity.badRequest().body(new UserReqeust());
        }
    }

    @GetMapping("/user/detail")
    public String addUser2(@ModelAttribute UserReqeust userReqeust) {
        logger.debug("this is recv -> {}", userReqeust);
        return "hello, " + userReqeust.toString();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserReqeust> getUser(@PathVariable int id) {
        //서비스 에서 디비조회했다 치고
        UserReqeust userReqeust = new UserReqeust(id, "get user 입니다.");
        return ResponseEntity.ok(userReqeust);
    }

}
