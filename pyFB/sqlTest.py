import MySQLdb

db = MySQLdb.connect("localhost","root","","storysnooper")

# prepare a cursor object using cursor() method
cursor = db.cursor()

sql="select * from page"

try:
   # Execute the SQL command
   cursor.execute(sql)
   # Fetch all the rows in a list of lists.
   results = cursor.fetchall()
   for row in results:
      fname = row[0]
      lname = row[1]
      age = row[2]
      # Now print fetched result
      print "fname=%s,lname=%s,age=%s" % \
             (fname, lname, age)
except:
   print "Error: unable to fecth data"

# disconnect from server
db.close()