package solutions.mystuff.application.web;

import java.util.List;

import solutions.mystuff.domain.model.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LinkHeaderBuilder")
class LinkHeaderBuilderTest {

    @Test
    @DisplayName("should URL-encode query parameter")
    void shouldUrlEncodeQuery() {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        PageResult<String> page = new PageResult<>(
                List.of("a"), 0, 10, true);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", page, "a b");
        String link = response.getHeader("Link");
        assertNotNull(link);
        assertTrue(link.contains("q=a+b"),
                "Space should be encoded as +");
    }

    @Test
    @DisplayName("should encode special characters in query")
    void shouldEncodeSpecialChars() {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        PageResult<String> page = new PageResult<>(
                List.of("a"), 0, 10, true);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", page, "foo&bar");
        String link = response.getHeader("Link");
        assertNotNull(link);
        assertTrue(link.contains("q=foo%26bar"),
                "& should be percent-encoded");
    }

    @Test
    @DisplayName("should omit query when null")
    void shouldOmitQueryWhenNull() {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        PageResult<String> page = new PageResult<>(
                List.of("a"), 0, 10, true);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", page, null);
        String link = response.getHeader("Link");
        assertNotNull(link);
        assertFalse(link.contains("q="));
    }

    @Test
    @DisplayName("should omit query when blank")
    void shouldOmitQueryWhenBlank() {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        PageResult<String> page = new PageResult<>(
                List.of("a"), 0, 10, true);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", page, "  ");
        String link = response.getHeader("Link");
        assertNotNull(link);
        assertFalse(link.contains("q="));
    }

    @Test
    @DisplayName("should add next link when hasNext")
    void shouldAddNextLink() {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        PageResult<String> page = new PageResult<>(
                List.of("a"), 0, 10, true);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", page, null);
        String link = response.getHeader("Link");
        assertNotNull(link);
        assertTrue(link.contains("rel=\"next\""));
        assertFalse(link.contains("rel=\"prev\""));
    }

    @Test
    @DisplayName("should add prev link on later page")
    void shouldAddPrevLink() {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        PageResult<String> page = new PageResult<>(
                List.of("a"), 1, 10, true);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", page, null);
        String link = response.getHeader("Link");
        assertNotNull(link);
        assertTrue(link.contains("rel=\"prev\""));
        assertTrue(link.contains("rel=\"next\""));
    }

    @Test
    @DisplayName("should include page and size in links")
    void shouldIncludePageAndSize() {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        PageResult<String> page = new PageResult<>(
                List.of("a"), 0, 25, true);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", page, null);
        String link = response.getHeader("Link");
        assertNotNull(link);
        assertTrue(link.contains("size=25"));
        assertTrue(link.contains("page=1"));
    }

    @Test
    @DisplayName("should omit header when no links needed")
    void shouldOmitHeaderWhenNoLinks() {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        PageResult<String> page = new PageResult<>(
                List.of("a"), 0, 10, false);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", page, null);
        assertNull(response.getHeader("Link"));
    }
}
