import java.io.*

/**
 *
 *
 * @param args : README.md's dir
 */
fun main(args: Array<String>) {
//    var dir = "."
//    if (args.isNotEmpty()) {
//        dir = args[0]
//    }
    val markdownDir = MarkdownDir()
    markdownDir.generateDir("/home/jianjunhuang/code/gitbook")
}

class MarkdownDir {

    fun generateDir(dir: String) {
        val mainFile = File(dir)
        if (!mainFile.exists() || !mainFile.isDirectory) {
            println("please make sure input an exists dir!!")
            return
        }
        val readeMe = File(mainFile, "SUMMERY.md")
        if (!readeMe.exists()) {
            readeMe.createNewFile()
        }
        val mainDir = Dir(mainFile.name, mainFile.path)
        coverFileToDir(mainFile, mainDir)
        var bufferedWriter: BufferedWriter? = null
        try {
            bufferedWriter = BufferedWriter(FileWriter(readeMe))
            generateDir(bufferedWriter, mainDir, 0)
        } catch (e: IOException) {
            println(e)
        } finally {
            bufferedWriter?.close()
        }
    }

    private fun generateDir(writer: BufferedWriter, mainDir: Dir, level: Int) {
        for (dir in mainDir.dirs) {
            if (dir.dirs.isNotEmpty()) {
                if (level <= 0) {
                    writer.appendln("## [${dir.name} [${dir.dirs.size}] ](${dir.path})")
                    generateDir(writer, dir, level + 1)
                    writer.appendln("----------------")
                } else {
                    writer.appendln(formatTitle(dir, level))
                    generateDir(writer, dir, level + 1)
                }
            } else {
                if (dir.isFile)
                    writer.appendln(formatTitle(dir, level))
            }
        }
    }

    private fun formatTitle(fileDir: Dir, level: Int): String {
        val sb = StringBuilder()
        var index = level
        while (--index > 0) {
            sb.append("  ")
        }
        sb.append("- [${fileDir.name}](${fileDir.path})")
        return sb.toString()
    }

    private fun coverFileToDir(file: File, dir: Dir) {
        val files = file.listFiles()
        files?.let {
            for (subFile in it) {
                println(subFile.name)
                if (subFile.isDirectory) {
                    if (subFile.name.startsWith('.')) {
                        continue
                    }
                    val subDir = Dir(subFile.name, subFile.path, isFile = false)
                    dir.dirs.add(subDir)
                    coverFileToDir(subFile, subDir)
                } else {
                    val fileName = subFile.name
                    val fileSuffix = fileName.substringAfterLast(".", "")
                    if (fileSuffix == "md") {
                        var reader: BufferedReader? = null
                        try {
                            reader = BufferedReader(FileReader(subFile))
                            val name = reader.readLine() ?: ""
                            val mdDir = Dir(name.trim().removePrefix("#"), subFile.path, isFile = true)
                            dir.dirs.add(mdDir)
                        } catch (e: IOException) {
                            println(e)
                        } finally {
                            reader?.close()
                        }
                    }
                }
            }
        }
    }

    data class Dir(
        val name: String,
        val path: String,
        val dirs: ArrayList<Dir> = ArrayList(),
        var isFile: Boolean = false
    )

    companion object {
        val EMPTY_LIST = ArrayList<Dir>()
    }
}
