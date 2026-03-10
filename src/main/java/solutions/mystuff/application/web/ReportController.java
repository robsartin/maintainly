package solutions.mystuff.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    private final ServiceScheduleRepository scheduleRepo;
    private final ControllerHelper helper;

    public ReportController(
            ServiceScheduleRepository scheduleRepo,
            ControllerHelper helper) {
        this.scheduleRepo = scheduleRepo;
        this.helper = helper;
    }

    @GetMapping("/reports")
    public String reports(
            Principal principal, Model model) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            model.addAttribute("noOrganization", true);
            return "reports";
        }
        helper.addUserAttrs(user, model);
        return "reports";
    }

    @GetMapping("/reports/due-next-month")
    public void dueNextMonth(
            Principal principal,
            HttpServletResponse response)
            throws Exception {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId = user.getOrganization().getId();
            YearMonth next = YearMonth.now().plusMonths(1);
            LocalDate cutoff = next.atEndOfMonth();
            List<ServiceSchedule> schedules =
                    scheduleRepo
                            .findActiveByOrganizationId(
                                    orgId);
            List<ServiceSchedule> due = schedules.stream()
                    .filter(s -> s.getNextDueDate() != null
                            && !s.getNextDueDate()
                                    .isAfter(cutoff))
                    .toList();
            String orgName = user.getOrganization()
                    .getName();
            DueNextMonthPdf.write(
                    response, due, cutoff, orgName);
        } finally {
            helper.clearOrgMdc();
        }
    }
}
