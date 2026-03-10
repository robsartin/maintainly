package solutions.mystuff.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.ServiceType;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.UserResolver;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import solutions.mystuff.domain.port.out
        .ServiceTypeRepository;
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
    private final ServiceTypeRepository typeRepository;
    private final VendorRepository vendorRepository;

    public ControllerHelper(
            UserResolver userResolver,
            ServiceRecordRepository recordRepository,
            ServiceScheduleRepository scheduleRepository,
            ServiceTypeRepository typeRepository,
            VendorRepository vendorRepository) {
        this.userResolver = userResolver;
        this.recordRepository = recordRepository;
        this.scheduleRepository = scheduleRepository;
        this.typeRepository = typeRepository;
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

    void saveRecord(
            UUID orgId, Item item,
            ServiceType serviceType,
            ServiceSchedule schedule,
            Vendor vendor,
            String summary, String serviceDate,
            String techName) {
        LocalDate date = LocalDate.parse(serviceDate);
        ServiceRecord record = new ServiceRecord();
        record.setOrganizationId(orgId);
        record.setItem(item);
        record.setServiceType(serviceType);
        record.setServiceSchedule(schedule);
        record.setVendor(vendor);
        record.setServiceDate(date);
        record.setSummary(summary);
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

    ServiceType findServiceType(
            UUID serviceTypeId, UUID orgId) {
        return typeRepository.findByIdAndOrganizationId(
                        serviceTypeId, orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Service type not found"));
    }

    void createSchedule(
            UUID orgId, Item item,
            ServiceType serviceType, Vendor vendor,
            String nextDueDate,
            int frequencyInterval,
            FrequencyUnit frequencyUnit) {
        LocalDate due = LocalDate.parse(nextDueDate);
        ServiceSchedule newSched =
                new ServiceSchedule();
        newSched.setOrganizationId(orgId);
        newSched.setItem(item);
        newSched.setServiceType(serviceType);
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
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Vendor name is required");
        }
        Vendor vendor = new Vendor();
        vendor.setOrganizationId(orgId);
        vendor.setName(name.trim());
        if (phone != null && !phone.isBlank()) {
            vendor.setPhone(phone.trim());
        }
        return vendorRepository.save(vendor);
    }
}
