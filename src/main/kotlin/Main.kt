const val uninitializedChar = '\u0000'
const val xChar = 'x'
val rowRegex = Regex("^[\\dx]{9}$")
val numbers = Array(9) { CharArray(9) }

fun main() {
    println("Enter sudoku rows separated by line. Use '$xChar' for missing number:")
    var rowNumber = 0
    do {
        val row = readNewRow(rowNumber) ?: continue
        numbers[rowNumber] = row.toCharArray()
        rowNumber++
    } while (numbers[8][8] == uninitializedChar)

    try {
        solveNumberRecursively()
    } catch (e: TheSudokuIsSolvedGoodEnding) {
        println("The sudoku is solved! Numbers are:")
        for (row in 0..8) {
            for (column in 0..8) {
                print(numbers[row][column])
                if (column % 3 == 2) {
                    print(" ")
                }
            }
            println()
            if (row % 3 == 2) {
                println()
            }
        }
        return
    }
    println("The sudoku is invalid! Nice try but this algorithm is superior!")
}

fun readNewRow(rowNumber: Int): String? {
    val row = readLine()
    if (row?.length != 9) {
        println("Row is not 9 length. Try again!")
        return null
    }

    if (!row.matches(rowRegex)) {
        println("Row contains illegal characters. Try again!")
        return null
    }

    if (rowContainsDuplicateNumbers(row)) {
        println("Row contains a number more than once. Try again!")
        return null
    }

    if (anyColumnContainsDuplicateNumbers(numbers, row, rowNumber)) {
        println("A column already contains a number from this row in the same column index. Try again!")
        return null
    }

    if (anyGridContainsDuplicateNumbers(numbers, row, rowNumber)) {
        println("Grids contain duplicate numbers. Try again!")
        return null
    }

    return row
}

fun rowContainsDuplicateNumbers(row: String): Boolean {
    val regex = Regex("(\\d).*\\1")
    return regex.containsMatchIn(row)
}

fun anyColumnContainsDuplicateNumbers(numbers: Array<CharArray>, newRow: String, rowNumber: Int): Boolean {
    if (rowNumber == 0) return false

    for (column in newRow.indices) {
        if (newRow[column] == xChar) continue

        for (row in 0 until rowNumber) {
            if (newRow[column] == numbers[row][column]) {
                return true
            }
        }
    }

    return false
}

fun anyGridContainsDuplicateNumbers(numbers: Array<CharArray>, newRow: String, rowNumber: Int): Boolean {
    if (rowNumber == 0) return false

    for (column in newRow.indices) {
        if (newRow[column] == xChar) continue

        for (row in 0 until rowNumber) {
            for (columnToCheck in 0..8) {
                val sameGrid = columnToCheck / 3 == column / 3 && row / 3 == rowNumber / 3
                if (sameGrid && numbers[row][columnToCheck] == newRow[column]) {
                    return true
                }
            }
        }
    }

    return false
}

fun solveNumberRecursively() {
    val (row, column) = findNextXCoordinates()
    for (newNumberForX in 1..9) {
        val newNumberAsChar = newNumberForX.digitToChar()
        if (!newNumberFucksUpEverything(row, column, newNumberAsChar)) {
            numbers[row][column] = newNumberAsChar
            try {
                solveNumberRecursively()
            } catch (_: TheSudokuIsFuckedUpException) {
            }
        }
    }
    numbers[row][column] = xChar
    throw TheSudokuIsFuckedUpException()
}

fun findNextXCoordinates(): Coordinates {
    for (row in 0..8) {
        for (column in 0..8) {
            if (numbers[row][column] == xChar) {
                return Coordinates(row, column)
            }
        }
    }
    throw TheSudokuIsSolvedGoodEnding()
}

data class Coordinates(
    val row: Int,
    val column: Int,
)

class TheSudokuIsSolvedGoodEnding : java.lang.Exception("Sudoku is solved!")
class TheSudokuIsFuckedUpException : java.lang.Exception("Abort mission! I repeat, abort mission!")

fun newNumberFucksUpEverything(newNumberRow: Int, newNumberColumn: Int, newNumber: Char): Boolean {
    val numberRowGrid = newNumberRow / 3
    val numberColumnGrid = newNumberColumn / 3
    for (row in 0..8) {
        for (column in 0..8) {
            val sameRow = row == newNumberRow
            val sameColumn = column == newNumberColumn
            val sameGrid = column / 3 == numberColumnGrid && row / 3 == numberRowGrid

            if ((sameRow || sameColumn || sameGrid) && numbers[row][column] == newNumber) {
                return true
            }
        }
    }

    return false
}