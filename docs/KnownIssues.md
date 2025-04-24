# Known Issues

This page outlines current issues and limitations in the Digital Cookbook Creator application. 

---

## Issue: OCR Inaccuracy and Unstructured Output

**Issue:**  
The ML Kit Text Recognition API consistently produces inaccurate or unstructured results when scanning handwritten recipes. Common issues include missing words, incorrect characters, and inconsistent line breaks or formatting.

**Impact:**  
Users must manually correct and reformat most of the scanned text, which reduces the convenience of the OCR feature and adds time to the recipe creation process.

**Notes:**  
- A manual formatting screen is provided to clean up scanned text before saving.  
- The original proof-of-concept tested ML Kit on simple paragraph-style samples, which failed to reflect real-world recipe layouts.  
- Future versions may explore alternative OCR tools better suited for handwritten input.

---

## Limitation: No Cloud Backup or Sync

**Issue:**  
All data is stored locally on the user's device. The app does not currently support syncing or cloud-based backups.

**Impact:**  
If a user uninstalls the app or switches devices, their saved recipes are lost.

**Notes:**  
The app was designed to function offline-first. However, future plans may include optional cloud integration using Firebase or another service.

---
