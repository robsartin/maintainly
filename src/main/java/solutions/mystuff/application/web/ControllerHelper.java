package solutions.mystuff.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.ServiceType;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.UserResolver;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import solutions.mystuff.domain.port.out
        .VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    private final VendorRepository vendorRepository;

    public ControllerHelper(
            UserResolver userResolver,
            ServiceRecordRepository recordRepository,
            VendorRepository vendorRepository) {
        this.userResolver = userResolver;
        this.recordRepository = recordRepository;
        this.vendorRepository = vendorRepository;
    }

    AppUser resolveUser(Principal principal) {
        return userResolver.resolveOrCreate(
                principal.getName());
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
