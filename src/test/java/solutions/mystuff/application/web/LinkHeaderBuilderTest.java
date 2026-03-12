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
                List.of("a"), 0, 10, 1, 1);
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
                List.of("a"), 0, 10, 1, 1);
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
                List.of("a"), 0, 10, 1, 1);
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
                List.of("a"), 0, 10, 1, 1);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", page, "  ");
        String link = response.getHeader("Link");
        assertNotNull(link);
        assertFalse(link.contains("q="));
    }

    @Test
    @DisplayName("should add first and last links")
    void shouldAddFirstAndLastLinks() {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        PageResult<String> page = new PageResult<>(
                List.of("a"), 0, 10, 20, 2);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", page, null);
        String link = response.getHeader("Link");
        assertNotNull(link);
        assertTrue(link.contains("rel=\"first\""));
        assertTrue(link.contains("rel=\"last\""));
        assertTrue(link.contains("rel=\"next\""));
    }

    @Test
    @DisplayName("should include page and size in links")
    void shouldIncludePageAndSize() {
        MockHttpServletResponse response =
                new MockHttpServletResponse();
        PageResult<String> page = new PageResult<>(
                List.of("a"), 0, 25, 1, 1);
        LinkHeaderBuilder.addLinkHeader(
                response, "/items", page, null);
        String link = response.getHeader("Link");
        assertNotNull(link);
        assertTrue(link.contains("size=25"));
        assertTrue(link.contains("page=0"));
    }
}
