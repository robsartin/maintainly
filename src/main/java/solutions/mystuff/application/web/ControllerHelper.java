package solutions.mystuff.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.UserResolver;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import solutions.mystuff.domain.port.out
        .VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.oauth2.client
        .authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user
        .OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class ControllerHelper {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ControllerHelper.class);
    private static final String MDC_ORG_ID =
            "organizationId";
    private static final int MAX_PAGE_SIZE = 50;

    private final UserResolver userResolver;
    private final ServiceRecordRepository recordRepository;
    private final ServiceScheduleRepository scheduleRepository;
    private final VendorRepository vendorRepository;

    public ControllerHelper(
            UserResolver userResolver,
            ServiceRecordRepository recordRepository,
            ServiceScheduleRepository scheduleRepository,
            VendorRepository vendorRepository) {
        this.userResolver = userResolver;
        this.recordRepository = recordRepository;
        this.scheduleRepository = scheduleRepository;
        this.vendorRepository = vendorRepository;
    }

    AppUser resolveUser(Principal principal) {
        return userResolver.resolveOrCreate(
                extractUsername(principal));
    }

    private String extractUsername(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken
                oauth) {
            OAuth2User user = oauth.getPrincipal();
            String email = user.getAttribute("email");
            if (email != null && !email.isBlank()) {
                return email;
            }
        }
        return principal.getName();
    }

    void addUserAttrs(AppUser user, Model model) {
        model.addAttribute("username",
                user.getUsername());
        model.addAttribute("organization",
                user.getOrganization());
    }

    void setOrgMdc(AppUser user) {
        if (user.hasOrganization()) {
            MDC.put(MDC_ORG_ID,
                    user.getOrganization().getId()
                            .toString());
        }
    }

    void clearOrgMdc() {
        MDC.remove(MDC_ORG_ID);
    }

    int clampSize(int size) {
        return Math.max(1, Math.min(size, MAX_PAGE_SIZE));
    }

    static void requireNotBlank(
            String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required");
        }
    }

    static void requireMaxLength(
            String value, String fieldName,
            int maxLength) {
        if (value != null
                && value.trim().length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName + " exceeds maximum length of "
                            + maxLength);
        }
    }

    static void requirePositive(
            int value, String fieldName) {
        if (value < 1) {
            throw new IllegalArgumentException(
                    fieldName + " must be at least 1");
        }
    }

    static LocalDate parseDate(
            String value, String fieldName) {
        requireNotBlank(value, fieldName);
        try {
            return LocalDate.parse(value.trim());
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be a valid date"
                            + " (yyyy-MM-dd)");
        }
    }

    void saveRecord(
            UUID orgId, Item item,
            String serviceType,
            ServiceSchedule schedule,
            Vendor vendor,
            String summary, String serviceDate,
            String techName) {
        requireNotBlank(summary, "Summary");
        requireMaxLength(summary, "Summary", 250);
        requireMaxLength(techName, "Technician", 200);
        LocalDate date = parseDate(
                serviceDate, "Service date");
        ServiceRecord record = new ServiceRecord();
        record.setOrganizationId(orgId);
        record.setItem(item);
        record.setServiceType(serviceType);
        record.setServiceSchedule(schedule);
        record.setVendor(vendor);
        record.setServiceDate(date);
        record.setSummary(summary.trim());
        if (techName != null && !techName.isBlank()) {
            record.setDescription(
                    "Technician: " + techName.trim());
        }
        recordRepository.save(record);
        log.info("Saved service record for item {}",
                item.getId());
    }

    Vendor resolveVendor(
            UUID orgId, String vendorId,
            String newVendorName,
            String newVendorPhone) {
        if ("__new__".equals(vendorId)) {
            return createVendor(orgId,
                    newVendorName, newVendorPhone);
        }
        if (vendorId != null && !vendorId.isBlank()) {
            return vendorRepository
                    .findByIdAndOrganizationId(
                            UUID.fromString(vendorId),
                            orgId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Vendor not found"));
        }
        return null;
    }

    void createSchedule(
            UUID orgId, Item item,
            String serviceType, Vendor vendor,
            String nextDueDate,
            int frequencyInterval,
            FrequencyUnit frequencyUnit) {
        requireNotBlank(serviceType, "Service type");
        requireMaxLength(
                serviceType, "Service type", 150);
        requirePositive(
                frequencyInterval, "Frequency interval");
        LocalDate due = parseDate(
                nextDueDate, "Next due date");
        ServiceSchedule newSched =
                new ServiceSchedule();
        newSched.setOrganizationId(orgId);
        newSched.setItem(item);
        newSched.setServiceType(serviceType.trim());
        newSched.setPreferredVendor(vendor);
        newSched.setFrequencyUnit(frequencyUnit);
        newSched.setFrequencyInterval(frequencyInterval);
        newSched.setFirstDueDate(due);
        newSched.setNextDueDate(due);
        scheduleRepository.save(newSched);
        log.info("Created schedule for item {}",
                item.getId());
    }

    private Vendor createVendor(
            UUID orgId, String name, String phone) {
        requireNotBlank(name, "Vendor name");
        requireMaxLength(name, "Vendor name", 200);
        requireMaxLength(phone, "Vendor phone", 50);
        Vendor vendor = new Vendor();
        vendor.setOrganizationId(orgId);
        vendor.setName(name.trim());
        if (phone != null && !phone.isBlank()) {
            vendor.setPhone(phone.trim());
        }
        return vendorRepository.save(vendor);
    }
}
