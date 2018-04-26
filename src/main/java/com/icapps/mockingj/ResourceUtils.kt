package com.icapps.mockingj

import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.net.URLDecoder
import java.util.*
import java.util.jar.JarFile

object ResourceUtils {

    /**
     * List directory contents for a resource folder. Not recursive.
     * This is basically a brute-force implementation.
     * Works for regular files and also JARs.
     *
     * @author Greg Briggs
     * @param clazz Any java class that lives in the same place as the resources you want.
     * @param path Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException
     * @throws IOException
     */
    @Throws(URISyntaxException::class, IOException::class)
    fun getResourceListing(clazz: Class<*>, path: String): Collection<File> {
        var dirURL = clazz.classLoader.getResource(path)
        if (dirURL != null && dirURL.protocol.equals("file")) {
            /* A file path: easy enough */
            return listFileTree(File(dirURL.toURI()))
        }

        if (dirURL == null) {
            /*
         * In case of a jar file, we can't actually find a directory.
         * Have to assume the same jar as clazz.
         */
            val me = clazz.name.replace(".", "/") + ".class"
            dirURL = clazz.classLoader.getResource(me)
        }

        if (dirURL!!.protocol.equals("jar")) {
            /* A JAR path */
            val jarPath = dirURL.path.substring(5, dirURL.path.indexOf("!")) //strip out only the JAR file
            val jar = JarFile(URLDecoder.decode(jarPath, "UTF-8"))
            val entries = jar.entries() //gives ALL entries in jar
            val result = HashSet<String>() //avoid duplicates in case it is a subdirectory

            while (entries.hasMoreElements()) {
                val name = entries.nextElement().name
                if (name.startsWith(path)) { //filter according to the path
                    result.add(name)
                }
            }
            return result.map { File(it) }
        }

        throw UnsupportedOperationException("Cannot list files for URL $dirURL")
    }

    private fun listFileTree(dir: File?): Collection<File> {
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