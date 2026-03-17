package solutions.mystuff.application.web.api;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.application.web.ControllerHelper;
import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.port.in.ItemQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for querying items as JSON.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant C as Client
 *     participant A as ItemApiController
 *     participant Q as ItemQuery
 *     C->>A: GET /api/items
 *     A->>Q: findByOrganization(orgId)
 *     Q-->>A: PageResult~Item~
 *     A-->>C: List~ItemDto~
 * </div>
 *
 * @see ItemDto
 * @see ItemQuery
 */
@RestController
@RequestMapping("/api/items")
public class ItemApiController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ItemApiController.class);
    private final ControllerHelper helper;
    private final ItemQuery itemQuery;

    /** Creates an item API controller with the required dependencies. */
    public ItemApiController(
            ControllerHelper helper,
            ItemQuery itemQuery) {
        this.helper = helper;
        this.itemQuery = itemQuery;
    }

    /** Returns a paginated list of items as JSON. */
    @GetMapping
    public List<ItemDto> listItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        int safePage = Math.max(0, page);
        int safeSize = helper.clampSize(size);
        log.info("API list items page={}", safePage);
        PageResult<Item> result =
                itemQuery.findByOrganization(
                        orgId, safePage, safeSize);
        return result.content().stream()
                .map(ItemDto::from).toList();
    }

    /** Returns a single item by ID as JSON. */
    @GetMapping("/{id}")
    public ItemDto getItem(
            @PathVariable("id") UUID itemId,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId = user.getOrganization().getId();
        Item item = itemQuery
                .findByIdAndOrganization(itemId, orgId)
                .orElseThrow(() -> new NotFoundException(
                        "Item not found"));
        return ItemDto.from(item);
    }
}
