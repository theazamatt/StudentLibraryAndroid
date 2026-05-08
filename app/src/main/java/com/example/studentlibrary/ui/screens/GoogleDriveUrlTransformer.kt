package com.example.studentlibrary.ui.screens

object GoogleDriveUrlTransformer {
    /**
     * Преобразует ссылку на просмотр Google Drive в ссылку на прямое скачивание файла.
     * Пример: https://drive.google.com/file/d/ID/view -> https://drive.google.com/uc?export=download&id=ID
     */
    fun transform(url: String): String {
        if (!url.contains("drive.google.com")) return url
        
        val fileId = findFileId(url) ?: return url
        return "https://drive.google.com/uc?export=download&id=$fileId"
    }

    private fun findFileId(url: String): String? {
        // Формат /file/d/ID/
        val pattern1 = "/file/d/([^/?]+)".toRegex()
        val match1 = pattern1.find(url)
        if (match1 != null) return match1.groupValues[1]

        // Формат id=ID
        val pattern2 = "id=([^&]+)".toRegex()
        val match2 = pattern2.find(url)
        if (match2 != null) return match2.groupValues[1]

        return null
    }
}
