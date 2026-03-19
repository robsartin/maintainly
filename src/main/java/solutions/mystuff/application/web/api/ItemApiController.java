package solutions.mystuff.application.web.api;

import java.security.Principal;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ItemSpec;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.PageResult;
import solutions.mystuff.domain.port.in.ItemManagement;
import solutions.mystuff.domain.port.in.ItemQuery;
import solutions.mystuff.domain.port.in.UserResolver;
import solutions.mystuff.application.web.LinkHeaderBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for item CRUD operations.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Client->>ItemApiController: GET /api/v1/items
 *     ItemApiController->>UserResolver: resolveOrCreate
 *     ItemApiController->>ItemQuery: findByOrganization
 *     ItemApiController-->>Client: JSON PageResponse
 *     Client->>ItemApiController: GET /api/v1/items?category=HVAC
 *     ItemApiController->>ItemQuery: findByCategoryAndOrganization
 *     ItemApiController-->>Client: JSON PageResponse
 * </div>
 *
 * @see ItemQuery
 * @see ItemManagement
 */
@RestController
@RequestMapping("/api/v1/items")
@Tag(name = "Items API",
        description = "REST API for item management")
public class ItemApiController {

    private final ItemQuery itemQuery;
    private final ItemManagement itemService;
    private final UserResolver userResolver;

    /** Creates an item API controller. */
    public ItemApiController(
            ItemQuery itemQuery,
            ItemManagement itemService,
            UserResolver userResolver) {
        this.itemQuery = itemQuery;
        this.itemService = itemService;
        this.userResolver = userResolver;
    }

    /** Lists items with pagination and optional search. */
    @Operation(summary = "List items")
    @GetMapping
    public PageResponse<ItemResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Search query")
            @RequestParam(required = false) String q,
            @Parameter(description = "Category filter")
            @RequestParam(required = false)
                    String category,
            Principal principal,
            HttpServletResponse response) {
        UUID orgId = resolveOrgId(principal);
        int clamped = Math.max(1,
                Math.min(size, 100));
        String cat = normalizeCategory(category);
        PageResult<Item> result = queryItems(
                q, cat, orgId, page, clamped);
        LinkHeaderBuilder.addLinkHeader(
                response, "/api/v1/items",
                result, q, cat);
        return PageResponse.from(
                result, ItemResponse::from);
    }

    private PageResult<Item> queryItems(
            String q, String category,
            UUID orgId, int page, int size) {
        boolean hasQuery = q != null && !q.isBlank();
        boolean hasCat = category != null;
        if (hasQuery && hasCat) {
            return itemQuery
                    .searchByCategoryAndOrganization(
                            orgId, q, category,
                            page, size);
        } else if (hasQuery) {
            return itemQuery.searchByOrganization(
                    orgId, q, page, size);
        } else if (hasCat) {
            return itemQuery
                    .findByCategoryAndOrganization(
                            orgId, category, page, size);
        } else {
            return itemQuery.findByOrganization(
                    orgId, page, size);
        }
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }
        return category;
    }

    /** Gets a single item by ID. */
    @Operation(summary = "Get item by ID")
    @GetMapping("/{id}")
    public ItemResponse get(
            @PathVariable UUID id,
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        Item item = itemQuery
                .findByIdAndOrganization(id, orgId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Item not found"));
        return ItemResponse.from(item);
    }

    /** Creates a new item. */
    @Operation(summary = "Create item")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse create(
            @RequestBody ItemSpec spec,
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        return ItemResponse.from(
                itemService.createItem(orgId, spec));
    }

    /** Updates an existing item. */
    @Operation(summary = "Update item")
    @PutMapping("/{id}")
    public ItemResponse update(
            @PathVariable UUID id,
            @RequestBody ItemSpec spec,
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        return ItemResponse.from(
                itemService.updateItem(
                        orgId, id, spec));
    }

    /** Deletes an item. */
    @Operation(summary = "Delete item")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id,
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        itemService.deleteItem(orgId, id);
    }

    private UUID resolveOrgId(Principal principal) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        if (!user.hasOrganization()) {
            throw new IllegalArgumentException(
                    "No organization assigned");
        }
        return user.getOrganization().getId();
    }
}
