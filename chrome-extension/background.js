const MENU_ID = "translate-to-darija";

chrome.runtime.onInstalled.addListener(() => {
  chrome.contextMenus.create({
    id: MENU_ID,
    title: "Translate to Darija",
    contexts: ["selection"]
  });
});

chrome.contextMenus.onClicked.addListener(async (info, tab) => {
  if (info.menuItemId !== MENU_ID || !tab?.id) {
    return;
  }

  await chrome.storage.session.set({
    selectedText: info.selectionText || "",
    lastUpdatedAt: Date.now()
  });

  await chrome.sidePanel.open({ tabId: tab.id });
});

chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message?.type === "GET_SELECTED_TEXT") {
    chrome.storage.session.get(["selectedText"]).then((result) => {
      sendResponse({ text: result.selectedText || "" });
    });
    return true;
  }
  return false;
});
