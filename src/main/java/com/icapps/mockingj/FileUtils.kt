package com.icapps.mockingj

import java.io.File
import java.util.*

object FileUtils {

    fun listFileTree(dir: File?): Collection<File> {
        val fileTree = HashSet<File>()
        if (dir?.listFiles() == null) {
            return fileTree
        }
        for (entry in dir.listFiles()!!) {
            if (entry.isFile)
                fileTree.add(entry)
            else
                fileTree.addAll(listFileTree(entry))
        }
        return fileTree
    }

}