//include external dependencies
@Grapes(
        @Grab(group='net.sourceforge.jtds', module='jtds', version='1.3.1')
)
@GrabConfig(systemClassLoader=true)

// importing several classes
import groovy.sql.Sql
import java.io.FileWriter
import com.opencsv.CSVWriter

//main class
static void main(String[] args) {
    // Creating a file list and add all the files' names in that list of which the path is specified
    def folder = new File("/Users/Burak.Alan/Downloads")
    def fileList = []

    folder.eachFile{
        fileList << it.name
    }

    // Reading the csv file and put them row by row in the list.
    // columns names retrieved from that list's first row index to be held in string array.
    def csvFile = new File("/Users/Burak.Alan/Desktop/okr.csv")
    def rows = csvFile.readLines()
    def columnNames = rows[0].split(',')

    // Writing the list to a csv file and export it to the path specified.
    // The script also closes the csvWriter to complete the writing process.
    def fileWriter = new FileWriter("/Users/Burak.Alan/Desktop/exported_file.csv")
    def csvWriter = new CSVWriter(fileWriter)

    rows.each{ row ->
        csvWriter.writeNext(row)
    }

    csvWriter.close()

    // connecting to a SSMS and creating a table there. putting the imported list to that table.
    // The script closes the sql connection to release the resources used by the connection.
    def dbUrl = ""
    def dbUser = ""
    def dbPassword = ""
    def sql = Sql.newInstance(dbUrl, dbUser, dbPassword, "net.sourceforge.jtds.jdbc.Driver")
        // the collect method is used to apply a closure to each element in a collection
        // and return a new collection containing the results of the closure applied to each element.
        // ${} is used to interpolate expressions into strings. you can put class or function and run it inside the string.
    def createTableQuery = "CREATE TABLE OKR_PRACTICE (${columnNames.collect { "`$it` VARCHAR(255)" }.join(',')})"
    sql.execute(createTableQuery)

    for (int i = 1; i < rows.size(); i++) {
        def rowValues = rows[i].split(',')
        def insertQuery = "INSERT INTO OKR_PRACTICE VALUES (${(1..columnNames.size()).collect { "?" }.join(',')})"
        sql.execute(insertQuery, rowValues)
    }

    sql.close()

}