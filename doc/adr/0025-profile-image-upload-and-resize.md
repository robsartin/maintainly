# 25. Profile Image Upload and Resize

Date: 2026-03-13

## Status

Accepted

## Context

Profile images (user and organization) were uploaded via a traditional file input
and submit button. Images larger than 128x128 were scaled down proportionally,
preserving aspect ratio, which could produce non-square outputs (e.g., a 400x200
image became 128x64). Non-square images display poorly in circular avatars and
square preview slots.

## Decision

- **Exact 128x128 output**: All uploaded images are scaled to fill and
  center-cropped to exactly 128x128 pixels. This uses `Math.max` scaling
  (cover, not contain) followed by a centered subimage extraction.
- **Click-to-upload**: The image preview area is now clickable. Clicking it
  opens the file picker; selecting a file auto-submits the form. The hidden
  file input uses `data-auto-submit` and `data-image-upload` attributes
  handled by `app.js` event delegation.
- **Validation unchanged**: PNG and JPEG only, max 512 KB upload size.
  Content type and file size checks remain in `ProfileImageServiceImpl`.

## Consequences

- All profile images are square, so circular avatars (`border-radius: 50%`)
  and square previews display consistently.
- Users can update their image with a single click instead of select + submit.
- Images smaller than 128x128 are scaled up to fill the square, which may
  introduce slight blurriness on very small source images. This is acceptable
  for profile images.
