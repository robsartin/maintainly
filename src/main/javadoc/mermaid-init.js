document.addEventListener("DOMContentLoaded", function() {
    var script = document.createElement("script");
    script.src = "https://cdn.jsdelivr.net/npm/mermaid@11/dist/mermaid.min.js";
    script.onload = function() {
        mermaid.initialize({startOnLoad: true, theme: "default"});
        mermaid.run({querySelector: ".mermaid"});
    };
    document.head.appendChild(script);
});
