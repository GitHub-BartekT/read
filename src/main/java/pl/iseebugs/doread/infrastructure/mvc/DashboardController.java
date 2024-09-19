package pl.iseebugs.doread.infrastructure.mvc;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {
        // Zwracana strona po autoryzacji
        return ResponseEntity.ok("Witaj na dashboardzie, dostęp chroniony!");
    }
}

