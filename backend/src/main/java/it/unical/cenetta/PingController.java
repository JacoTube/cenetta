package it.unical.cenetta;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class PingController {
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/porcodio")
    public String cazziata() {
        return "CHE CAZZO BESTEMMI COGLIONE";
    }   
}