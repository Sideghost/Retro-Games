package tetris

enum class Dir(val dx: Int, val dy: Int) { UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0) }

/*
data class Dir(val name:String, val ordinal:Int)
val UP = Dir("UP",0)
val DOWN = Dir("DOWN",1)
    ...
*/

fun f(dir: Dir) {
	println("${dir.dx},${dir.dy}")
	println(dir.name)
	println(dir.ordinal)
	Dir.values().forEach { print("$it ") }
	println()
}