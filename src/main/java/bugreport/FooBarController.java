package bugreport;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class FooBarController {
  
  @RequestMapping
  @ResponseBody
  public Object handleRequest (Map<String, Object> parameters) {
      System.out.println("-- handling request in controller --");
      return "dummy response from FooBarController";
  }
}