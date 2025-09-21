// just paste in console and hit f9 to extract JSON to clipboard
(() => {
  const STATE_KEY = "__crowdin_extractor_state__";

  // Cleanup previous run
  if (window[STATE_KEY]) {
    console.log("Crowdin Extractor: Removing previous state");
    window.removeEventListener("keydown", window[STATE_KEY].handler);
    window[STATE_KEY].toast?.remove();
    window[STATE_KEY].popover?.remove();
    delete window[STATE_KEY];
  }

  console.log("Crowdin Extractor: Installing F9 hotkey...");

  // --- Extract Crowdin data
  function extractJson() {
    const results = { strings: [], discussions: [] };

    // Extract checked strings
    document.querySelectorAll(".proofread-string-wrapper.checked").forEach((w) => {
      const id = w.getAttribute("data-id") || "";
      const source =
        w.querySelector(".proofread-phrase-singular .singular")?.innerText.trim() ||
        "";
      const key = w.querySelector(".source-string-key")?.innerText.trim() || "";

      const textarea = w.querySelector("textarea.hwt-input");
      let translation = textarea?.value.trim();
      if (!translation) {
        translation = w.querySelector(".hwt-highlights")?.innerText.trim() || "";
      }

      // NEW: QA issues
      const qaIssues = Array.from(
        w.querySelectorAll(".qa-issue-label")
      ).map((el) => el.innerText.trim());

      results.strings.push({ id, key, source, translation, qaIssues });
    });

    // ... rest unchanged for discussions
    document.querySelectorAll("#discussions_messages li").forEach((li) => {
      const id = li.getAttribute("id") || "";
      const issue = li.querySelector(".issue-text")?.innerText.trim() || "";
      const author =
        li.querySelector(".comment-item-author span")?.innerText.trim() || "";
      const text = li.querySelector(".comment-item-text")?.innerText.trim() || "";
      const date =
        li.querySelector(".message_date")?.getAttribute("title") ||
        li.querySelector(".message_date")?.innerText.trim() ||
        "";
      const language = li.querySelector(".message_language")?.innerText.trim() || "";
      results.discussions.push({ id, issue, author, text, date, language });
    });

    return results;
  }

  // --- Audio beep
  function playBeep() {
    try {
      const ctx = new (window.AudioContext || window.webkitAudioContext)();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = "sine";
      osc.frequency.value = 880; // Hz
      osc.connect(gain);
      gain.connect(ctx.destination);
      ctx.resume().then(() => {
        osc.start();
        gain.gain.setValueAtTime(0.1, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(
          0.0001,
          ctx.currentTime + 0.2
        );
        osc.stop(ctx.currentTime + 0.2);
      });
    } catch (e) {
      console.warn("Audio playback failed:", e);
    }
  }

  // --- UI helpers
  function showToast(jsonText) {
    window[STATE_KEY].toast?.remove();
    window[STATE_KEY].popover?.remove();

    const chars = jsonText.length;
    const toast = document.createElement("div");
    toast.innerText = `Copied ${chars} characters`;
    Object.assign(toast.style, {
      position: "fixed",
      bottom: "20px",
      right: "20px",
      padding: "8px 12px",
      backgroundColor: "rgba(0,0,0,0.85)",
      color: "#fff",
      fontSize: "14px",
      borderRadius: "4px",
      cursor: "pointer",
      zIndex: 999999,
      transition: "opacity 0.3s",
    });
    document.body.appendChild(toast);

    toast.addEventListener("click", () => {
      const pop = document.createElement("div");
      Object.assign(pop.style, {
        position: "fixed",
        top: "50%",
        left: "50%",
        transform: "translate(-50%, -50%)",
        background: "#1e1e1e",       // dark bg
        color: "#eee",               // light text
        padding: "12px",
        border: "1px solid #444",
        borderRadius: "6px",
        zIndex: 1000000,
        width: "70%",
        height: "70%",
        display: "flex",
        flexDirection: "column",
        boxShadow: "0 4px 20px rgba(0,0,0,0.6)",
      });

      const textarea = document.createElement("textarea");
      textarea.value = jsonText;
      Object.assign(textarea.style, {
        flex: "1",
        width: "100%",
        background: "#252526",
        color: "#dcdcdc",
        fontFamily: "monospace",
        fontSize: "13px",
        padding: "8px",
        border: "1px solid #555",
        borderRadius: "4px",
        resize: "none",
      });

      const closeBtn = document.createElement("button");
      closeBtn.innerText = "Close";
      Object.assign(closeBtn.style, {
        marginTop: "8px",
        alignSelf: "flex-end",
        background: "#444",
        color: "#eee",
        border: "none",
        padding: "6px 12px",
        borderRadius: "4px",
        cursor: "pointer",
      });
      closeBtn.addEventListener("mouseover", () => {
        closeBtn.style.background = "#666";
      });
      closeBtn.addEventListener("mouseout", () => {
        closeBtn.style.background = "#444";
      });
      closeBtn.addEventListener("click", () => pop.remove());

      pop.appendChild(textarea);
      pop.appendChild(closeBtn);
      document.body.appendChild(pop);

      window[STATE_KEY].popover = pop;
    });

    setTimeout(() => {
      toast.style.opacity = "0";
      setTimeout(() => toast.remove(), 300);
    }, 5000);

    return toast;
  }

  // --- Handler for F9
  function handler(e) {
    if (e.key === "F9") {
      e.preventDefault();
      console.log("Crowdin Extractor: F9 pressed, extracting...");
      const data = extractJson();
      const jsonText = JSON.stringify(data, null, 4);

      // copy to clipboard reliably
      navigator.clipboard
        .writeText(jsonText)
        .then(() => console.log("Copied JSON to system clipboard"))
        .catch((err) => console.warn("Clipboard write failed:", err));

      playBeep();
      const toast = showToast(jsonText);
      window[STATE_KEY].toast = toast;
    }
  }

  // Bind
  window.addEventListener("keydown", handler);
  window[STATE_KEY] = { handler, toast: null, popover: null };
  console.log("Crowdin Extractor: Installed. Press F9 to extract.");
})();