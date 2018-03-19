import urllib2
import json
import datetime
import csv
import time
import re
import MySQLdb

app_id = "1410060569295482"
app_secret = "cbad48dd385c8485034138b91f2fb8d1" # DO NOT SHARE WITH ANYONE!

access_token = app_id + "|" + app_secret


def request_until_succeed(url):
	req = urllib2.Request(url)
	try: 
		response = urllib2.urlopen(req)
		if response.getcode() == 200:
			success = True
		return response.read()
	except Exception, e:
		print e
		time.sleep(5)

		print "Error for URL %s: %s" % (url, datetime.datetime.now())
		print "Retrying."
		return 1

# Needed to write tricky unicode correctly to csv
def unicode_normalize(text):
	return text.translate({ 0x2018:0x27, 0x2019:0x27, 0x201C:0x22, 0x201D:0x22,
                            0xa0:0x20 }).encode('utf-8')

def getFacebookPageFeedData(post_id, access_token):

    # Construct the URL string; see http://stackoverflow.com/a/37239851 for
    # Reactions parameters
	base = "https://graph.facebook.com"
	node = "/%s" % post_id 
	fields = "?fields=comments.limit(0).summary(true),shares,reactions.limit(0).summary(true)"
	parameters = "&access_token=%s" % (access_token)
	url = base + node + fields + parameters

    # retrieve data
	data = request_until_succeed(url)
	if(data == 1):
		return 1;
	data = json.loads(data)

	return data
#
#Begin code
#
db = MySQLdb.connect("localhost","root","","storysnooper")

# prepare a cursor object using cursor() method
cursor = db.cursor()

sql="select * from cma"

try:
   # Execute the SQL command
	cursor.execute(sql)
   # Fetch all the rows in a list of lists.
	results = cursor.fetchall()
	for row in results:
		
		status = getFacebookPageFeedData(str(row[0])+"_"+str(row[1]),access_token)
		#url fail
		if(status == 1):
			sql = "delete from cma where page_id="+str(row[0])+" and post_id="+str(row[1])
		else:
			sql = "update cma set n="+str(row[4]+1)
			reactions = 0 if 'reactions' not in status else status['reactions']['summary']['total_count']
			comments = 0 if 'comments' not in status else status['comments']['summary']['total_count']
			shares = 0 if 'shares' not in status else status['shares']['count']
			
			cma = ((reactions+comments+shares)+(row[4]*row[2]))/(row[4]+1)
			sql += ", cma=" + str(cma)
			sql += ", growth=" + str(float(cma)/float(row[2]))
			sql+= " where page_id="+str(row[0])+" and post_id="+str(row[1])
			#To do
			#Add update stat on post table as well
		print sql
		try:
			# Execute the SQL command
			cursor.execute(sql)
			# Commit your changes in the database
			db.commit()
			print"Success"
		except:
			# Rollback in case there is any error
			db.rollback()
			print"Fail"
except:
	print "Error: unable to query data"

# disconnect from server
db.close()