package com.icapps.mockingj

import java.io.File
import java.util.*

object FileUtils {

    fun listFileTree(dir: File?): Collection<File> {
        val fileTree = HashSet<File>()

        val files = dir?.listFiles() ?: return fileTree

        for (entry in files) {
            if (entry.isHidden)
                continue

            if (entry.isFile) {
                fileTree.add(entry)
            } else {
                fileTree.addAll(listFileTree(entry))
            }
        }
        return fileTree
    }

}