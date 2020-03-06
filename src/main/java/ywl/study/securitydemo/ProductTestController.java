package ywl.study.securitydemo;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product")
public class ProductTestController {
    @RequestMapping("/info")
    @ResponseBody
    public String productInfo(){
        String currentUser="";
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails){
            currentUser=((UserDetails)principal).getUsername();
        }else{
            currentUser=principal.toString();
        }
        return " some product info,currentUser is: "+currentUser;
    }
}
