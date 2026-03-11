package solutions.mystuff.application.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure
        .AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web
        .servlet.request
        .SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web
        .servlet.request
        .SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Settings Controller Integration")
class SettingsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should show settings page")
    void shouldShowSettingsPage() throws Exception {
        mockMvc.perform(get("/settings")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model()
                        .attributeExists("user"));
    }

    @Test
    @DisplayName("should upload org image")
    void shouldUploadOrgImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "logo.png", "image/png",
                smallPng());
        mockMvc.perform(multipart("/settings/org-image")
                        .file(file)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings"));
    }

    @Test
    @DisplayName("should upload user image")
    void shouldUploadUserImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png",
                smallPng());
        mockMvc.perform(multipart("/settings/user-image")
                        .file(file)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings"));
    }

    @Test
    @DisplayName("should serve org image after upload")
    void shouldServeOrgImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "logo.png", "image/png",
                smallPng());
        mockMvc.perform(multipart("/settings/org-image")
                .file(file)
                .with(user("dev").roles("USER"))
                .with(csrf()));

        mockMvc.perform(get("/profile-image/org")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType("image/png"));
    }

    @Test
    @DisplayName("should serve user image after upload")
    void shouldServeUserImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png",
                smallPng());
        mockMvc.perform(multipart("/settings/user-image")
                .file(file)
                .with(user("dev").roles("USER"))
                .with(csrf()));

        mockMvc.perform(get("/profile-image/user")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType("image/png"));
    }

    @Test
    @DisplayName("should return 404 for missing user image")
    void shouldReturn404ForMissingImage()
            throws Exception {
        mockMvc.perform(get("/profile-image/user")
                        .with(user("newuser2")
                                .roles("USER")))
                .andExpect(status().isNotFound());
    }

    private byte[] smallPng() throws Exception {
        BufferedImage img = new BufferedImage(
                64, 64, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream out =
                new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }
}
