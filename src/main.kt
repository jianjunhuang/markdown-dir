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
    markdownDir.createDir("D:\\gitbook")
}

class MarkdownDir {

    fun createDir(dir: String) {
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
            createDir(bufferedWriter, mainDir)
        } catch (e: IOException) {
            println(e)
        } finally {
            bufferedWriter?.close()
        }
    }

    private fun createDir(writer: BufferedWriter, mainDir: Dir) {
        for (dir in mainDir.dirs) {
            if (dir.dirs.isNotEmpty()) {
                writer.appendln("## [${mainDir.name} [${mainDir.dirs.size}] ](${mainDir.path})")
                createDir(writer, dir)
                writer.appendln("----------------")
            } else {
                writer.appendln(formatTitle(dir))
            }
        }
    }

    private fun formatTitle(fileDir: Dir): String {
        val sb = StringBuilder()
        sb.append(" - [$fileDir.name]($fileDir.path)")
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
                    val subDir = Dir(subFile.name, subFile.path)
                    dir.dirs.add(subDir)
                    coverFileToDir(subFile, subDir)
                } else {
                    val fileName = subFile.name
                    val fileSuffix = fileName.substringAfterLast(".", "")
                    if (fileSuffix == "md") {
                        try {
                            val reader = BufferedReader(FileReader(subFile))
                            val mdDir = Dir(reader.readLine() ?: "".trim().removePrefix("#"), subFile.path)
                            dir.dirs.add(mdDir)
                        } catch (e: IOException) {
                            println(e)
                        } finally {

                        }
                    }
                }
            }
        }
    }

    data class Dir(
        val name: String,
        val path: String,
        val dirs: ArrayList<Dir> = EMPTY_LIST
    )

    companion object {
        val EMPTY_LIST = ArrayList<Dir>()
    }
}
