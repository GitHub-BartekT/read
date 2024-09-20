package pl.iseebugs.doread.infrastructure.mvc;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String dynamicPage(Model model) {
        return "landingPage";  // Odwołuje się do dynamicPage.html w /templates
    }
}
