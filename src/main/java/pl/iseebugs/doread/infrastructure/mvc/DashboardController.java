package pl.iseebugs.doread.infrastructure.mvc;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/api")
class DashboardController {

    @GetMapping("/dashboard")
    public String getDashboard() {
        // Zwracana strona po autoryzacji
        return "dashboard";
    }
}

